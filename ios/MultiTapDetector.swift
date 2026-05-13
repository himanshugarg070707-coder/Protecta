//
//  MultiTapDetector.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import Foundation

class MultiTapDetector {
    private let requiredTaps: Int
    private let timeWindow: TimeInterval
    private var currentTapCount = 0
    private var firstTapTimestamp: TimeInterval = 0

    init(tapsRequired: Int, timeWindow: TimeInterval) {
        self.requiredTaps = tapsRequired
        self.timeWindow = timeWindow
    }

    func registerTap() -> Bool {
        let now = Date().timeIntervalSince1970

        if firstTapTimestamp == 0 || (now - firstTapTimestamp > timeWindow) {
            firstTapTimestamp = now
            currentTapCount = 1
            return false
        }

        currentTapCount += 1
        if currentTapCount >= requiredTaps {
            reset()
            return true
        }

        return false
    }

    func reset() {
        firstTapTimestamp = 0
        currentTapCount = 0
    }
}