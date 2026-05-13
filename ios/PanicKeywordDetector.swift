//
//  PanicKeywordDetector.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import Foundation

class PanicKeywordDetector {
    private static let panicKeywords = [
        "help",
        "save me",
        "emergency",
        "danger",
        "i am unsafe"
    ]

    static func isPanicKeyword(_ speechText: String) -> Bool {
        let normalized = speechText.lowercased()
        return panicKeywords.contains { normalized.contains($0) }
    }

    static func detectKeyword(_ speechText: String) -> String? {
        let normalized = speechText.lowercased()
        return panicKeywords.first { normalized.contains($0) }
    }
}