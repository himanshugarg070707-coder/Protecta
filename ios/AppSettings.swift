//
//  AppSettings.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import Foundation

class AppSettings {
    static let shared = AppSettings()

    private let defaults = UserDefaults.standard

    var isShakeDetectionEnabled: Bool {
        get { defaults.bool(forKey: "shakeDetectionEnabled") }
        set { defaults.set(newValue, forKey: "shakeDetectionEnabled") }
    }

    var isTapTriggerEnabled: Bool {
        get { defaults.bool(forKey: "tapTriggerEnabled") }
        set { defaults.set(newValue, forKey: "tapTriggerEnabled") }
    }

    var isVoiceDetectionEnabled: Bool {
        get { defaults.bool(forKey: "voiceDetectionEnabled") }
        set { defaults.set(newValue, forKey: "voiceDetectionEnabled") }
    }

    // Add more settings as needed
}