//
//  MainViewController.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import UIKit
import CoreMotion
import Speech
import MessageUI

class MainViewController: UIViewController, SFSpeechRecognizerDelegate, MFMessageComposeViewControllerDelegate {

    // UI Elements
    private let sosButton = UIButton(type: .system)
    private let contactsButton = UIButton(type: .system)
    private let trackingButton = UIButton(type: .system)
    private let fakeCallButton = UIButton(type: .system)
    private let voiceButton = UIButton(type: .system)
    private let suggestionsButton = UIButton(type: .system)
    private let settingsButton = UIButton(type: .system)

    private let statusLabel = UILabel()
    private let locationLabel = UILabel()
    private let featureLabel = UILabel()
    private let tapInstructionLabel = UILabel()

    // Managers
    private let motionManager = CMMotionManager()
    private let speechRecognizer = SFSpeechRecognizer(locale: Locale.current)
    private var recognitionRequest: SFSpeechAudioBufferRecognitionRequest?
    private var recognitionTask: SFSpeechRecognitionTask?
    private let audioEngine = AVAudioEngine()

    private var multiTapDetector = MultiTapDetector(tapsRequired: 4, timeWindow: 2.5)
    private var shakeDetector = ShakeDetector()

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupMotionDetection()
        setupSpeechRecognition()
        updateStatus()
    }

    private func setupUI() {
        view.backgroundColor = .white
        title = "Protecta"

        // Layout buttons in a grid or stack
        let stackView = UIStackView()
        stackView.axis = .vertical
        stackView.spacing = 20
        stackView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(stackView)

        sosButton.setTitle("SOS", for: .normal)
        sosButton.backgroundColor = .red
        sosButton.setTitleColor(.white, for: .normal)
        sosButton.addTarget(self, action: #selector(sosTapped), for: .touchUpInside)
        let longPress = UILongPressGestureRecognizer(target: self, action: #selector(sosLongPressed))
        sosButton.addGestureRecognizer(longPress)
        stackView.addArrangedSubview(sosButton)

        contactsButton.setTitle("Emergency Contacts", for: .normal)
        contactsButton.addTarget(self, action: #selector(contactsTapped), for: .touchUpInside)
        stackView.addArrangedSubview(contactsButton)

        trackingButton.setTitle("Live Tracking", for: .normal)
        trackingButton.addTarget(self, action: #selector(trackingTapped), for: .touchUpInside)
        stackView.addArrangedSubview(trackingButton)

        fakeCallButton.setTitle("Fake Call", for: .normal)
        fakeCallButton.addTarget(self, action: #selector(fakeCallTapped), for: .touchUpInside)
        stackView.addArrangedSubview(fakeCallButton)

        voiceButton.setTitle("Voice Detection", for: .normal)
        voiceButton.addTarget(self, action: #selector(voiceTapped), for: .touchUpInside)
        stackView.addArrangedSubview(voiceButton)

        suggestionsButton.setTitle("Safety Suggestions", for: .normal)
        suggestionsButton.addTarget(self, action: #selector(suggestionsTapped), for: .touchUpInside)
        stackView.addArrangedSubview(suggestionsButton)

        settingsButton.setTitle("Settings", for: .normal)
        settingsButton.addTarget(self, action: #selector(settingsTapped), for: .touchUpInside)
        stackView.addArrangedSubview(settingsButton)

        // Status labels
        statusLabel.text = "Service Status: Active"
        stackView.addArrangedSubview(statusLabel)

        locationLabel.text = "Location: Available"
        stackView.addArrangedSubview(locationLabel)

        featureLabel.text = "Features: Enabled"
        stackView.addArrangedSubview(featureLabel)

        tapInstructionLabel.text = "Tap SOS 4 times to trigger"
        stackView.addArrangedSubview(tapInstructionLabel)

        NSLayoutConstraint.activate([
            stackView.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            stackView.centerYAnchor.constraint(equalTo: view.centerYAnchor),
            stackView.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 20),
            stackView.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -20)
        ])
    }

    private func setupMotionDetection() {
        if motionManager.isAccelerometerAvailable {
            motionManager.accelerometerUpdateInterval = 0.1
            motionManager.startAccelerometerUpdates(to: .main) { [weak self] (data, error) in
                if let data = data {
                    self?.shakeDetector.processAcceleration(x: data.acceleration.x, y: data.acceleration.y, z: data.acceleration.z)
                }
            }
        }

        shakeDetector.onShakeDetected = { [weak self] in
            self?.triggerSOS(reason: "Shake pattern detected")
        }
    }

    private func setupSpeechRecognition() {
        speechRecognizer?.delegate = self
        SFSpeechRecognizer.requestAuthorization { status in
            // Handle authorization
        }
    }

    @objc private func sosTapped() {
        if AppSettings.shared.isTapTriggerEnabled {
            if multiTapDetector.registerTap() {
                triggerSOS(reason: "SOS triggered by multiple taps")
            } else {
                showToast("Tap SOS 4 times quickly to trigger alert")
            }
        } else {
            triggerSOS(reason: "Manual SOS button tap")
        }
    }

    @objc private func sosLongPressed() {
        triggerSOS(reason: "Manual SOS long press")
    }

    private func triggerSOS(reason: String) {
        SOSManager.shared.sendSosToAllContacts(reason: reason, from: self) { success, message in
            self.showToast(message)
        }
    }

    @objc private func contactsTapped() {
        let contactsVC = EmergencyContactsViewController()
        navigationController?.pushViewController(contactsVC, animated: true)
    }

    @objc private func trackingTapped() {
        let trackingVC = LiveTrackingViewController()
        navigationController?.pushViewController(trackingVC, animated: true)
    }

    @objc private func fakeCallTapped() {
        let fakeCallVC = FakeCallViewController()
        fakeCallVC.callerName = "Mom"
        present(fakeCallVC, animated: true, completion: nil)
    }

    @objc private func voiceTapped() {
        if !AppSettings.shared.isVoiceDetectionEnabled {
            showToast("Voice detection is OFF in settings")
            return
        }
        startVoiceRecognition()
    }

    @objc private func suggestionsTapped() {
        let suggestionsVC = SafetySuggestionsViewController()
        navigationController?.pushViewController(suggestionsVC, animated: true)
    }

    @objc private func settingsTapped() {
        let settingsVC = SettingsViewController()
        navigationController?.pushViewController(settingsVC, animated: true)
    }

    private func startVoiceRecognition() {
        if audioEngine.isRunning {
            audioEngine.stop()
            recognitionRequest?.endAudio()
        } else {
            startRecording()
        }
    }

    private func startRecording() {
        recognitionTask?.cancel()
        recognitionTask = nil

        recognitionRequest = SFSpeechAudioBufferRecognitionRequest()
        guard let recognitionRequest = recognitionRequest else { return }

        let inputNode = audioEngine.inputNode
        recognitionRequest.shouldReportPartialResults = true

        recognitionTask = speechRecognizer?.recognitionTask(with: recognitionRequest) { result, error in
            if let result = result {
                let spokenText = result.bestTranscription.formattedString
                if PanicKeywordDetector.isPanicKeyword(spokenText) {
                    self.triggerSOS(reason: "Panic keyword detected: \(spokenText)")
                }
            }
        }

        let recordingFormat = inputNode.outputFormat(forBus: 0)
        inputNode.installTap(onBus: 0, bufferSize: 1024, format: recordingFormat) { buffer, _ in
            recognitionRequest.append(buffer)
        }

        audioEngine.prepare()
        try? audioEngine.start()
    }

    private func updateStatus() {
        // Update labels based on settings and permissions
    }

    private func showToast(_ message: String) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        present(alert, animated: true)
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            alert.dismiss(animated: true)
        }
    }

    // MARK: - MFMessageComposeViewControllerDelegate
    func messageComposeViewController(_ controller: MFMessageComposeViewController, didFinishWith result: MessageComposeResult) {
        controller.dismiss(animated: true, completion: nil)
    }

    // MARK: - SFSpeechRecognizerDelegate
    func speechRecognizer(_ speechRecognizer: SFSpeechRecognizer, availabilityDidChange available: Bool) {
        // Handle availability
    }
}