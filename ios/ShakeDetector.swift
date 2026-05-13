//
//  ShakeDetector.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import Foundation

class ShakeDetector {
    private let shakeThresholdGravity: Double = 2.7
    private let shakeSlopTimeMs: TimeInterval = 0.7
    private let shakeCountResetTimeMs: TimeInterval = 3.0
    private let gravityEarth: Double = 9.80665

    private var shakeTimestamp: TimeInterval = 0
    private var shakeCount = 0

    var onShakeDetected: (() -> Void)?

    func processAcceleration(x: Double, y: Double, z: Double) {
        let gX = x / gravityEarth
        let gY = y / gravityEarth
        let gZ = z / gravityEarth

        let gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        if gForce > shakeThresholdGravity {
            let now = Date().timeIntervalSince1970

            if shakeTimestamp + shakeSlopTimeMs > now {
                return
            }

            if shakeTimestamp + shakeCountResetTimeMs < now {
                shakeCount = 0
            }

            shakeTimestamp = now
            shakeCount += 1

            if shakeCount >= 2 {
                onShakeDetected?()
                shakeCount = 0
            }
        }
    }
}