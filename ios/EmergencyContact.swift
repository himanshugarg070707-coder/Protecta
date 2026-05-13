//
//  EmergencyContact.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import Foundation

struct EmergencyContact: Codable {
    var id: Int64
    var name: String
    var phoneNumber: String

    init(id: Int64 = 0, name: String, phoneNumber: String) {
        self.id = id
        self.name = name
        self.phoneNumber = phoneNumber
    }
}