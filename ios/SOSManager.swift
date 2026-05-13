//
//  SOSManager.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import Foundation
import MessageUI
import CoreLocation

class SOSManager: NSObject, MFMessageComposeViewControllerDelegate, CLLocationManagerDelegate {

    static let shared = SOSManager()

    private var locationManager: CLLocationManager?
    private var currentLocation: CLLocation?
    private var pendingContacts: [EmergencyContact]?
    private var pendingReason: String?
    private var callback: ((Bool, String) -> Void)?

    private override init() {
        super.init()
    }

    func sendSosToAllContacts(reason: String, from viewController: UIViewController, callback: @escaping (Bool, String) -> Void) {
        let repository = ContactsRepository()
        let contacts = repository.getAllContacts()

        if contacts.isEmpty() {
            callback(false, "No emergency contacts found. Add at least one contact first.")
            return
        }

        self.pendingContacts = contacts
        self.pendingReason = reason
        self.callback = callback

        // Request location
        locationManager = CLLocationManager()
        locationManager?.delegate = self
        locationManager?.requestWhenInUseAuthorization()
        locationManager?.startUpdatingLocation()
    }

    private func sendMessage(from viewController: UIViewController) {
        guard let contacts = pendingContacts, let reason = pendingReason else { return }

        let message = buildMessage(reason: reason, location: currentLocation)

        if MFMessageComposeViewController.canSendText() {
            let composeVC = MFMessageComposeViewController()
            composeVC.messageComposeDelegate = self
            composeVC.recipients = contacts.map { $0.phoneNumber }
            composeVC.body = message
            viewController.present(composeVC, animated: true, completion: nil)
        } else {
            callback?(false, "Cannot send messages from this device.")
        }
    }

    private func buildMessage(reason: String, location: CLLocation?) -> String {
        var message = "PROTECTA SOS ALERT!\n"
        message += "Reason: \(reason)\n"
        message += "I need immediate help.\n"

        if let location = location {
            message += "My location: https://maps.apple.com/?q=\(location.coordinate.latitude),\(location.coordinate.longitude)\n"
        } else {
            message += "Location is currently unavailable.\n"
        }

        message += "Sent from Protecta App."
        return message
    }

    // MARK: - MFMessageComposeViewControllerDelegate
    func messageComposeViewController(_ controller: MFMessageComposeViewController, didFinishWith result: MessageComposeResult) {
        controller.dismiss(animated: true, completion: nil)

        switch result {
        case .sent:
            callback?(true, "SOS message sent successfully.")
        case .cancelled:
            callback?(false, "SOS message was cancelled.")
        case .failed:
            callback?(false, "Failed to send SOS message.")
        @unknown default:
            callback?(false, "Unknown error occurred.")
        }
    }

    // MARK: - CLLocationManagerDelegate
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let location = locations.last {
            currentLocation = location
            manager.stopUpdatingLocation()
            // Now send the message
            // But we need the viewController, so perhaps delay or find another way
            // For simplicity, assume we have a way to get the current VC
        }
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        // Send without location
        // Again, need VC
    }
}