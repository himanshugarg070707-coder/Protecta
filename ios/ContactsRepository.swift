//
//  ContactsRepository.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import Foundation

class ContactsRepository {
    private let contactsKey = "emergencyContacts"

    func addContact(_ contact: EmergencyContact) -> Int64 {
        var contacts = getAllContacts()
        let newId = (contacts.map { $0.id }.max() ?? 0) + 1
        var newContact = contact
        newContact.id = newId
        contacts.append(newContact)
        saveContacts(contacts)
        return newId
    }

    func updateContact(_ contact: EmergencyContact) -> Bool {
        var contacts = getAllContacts()
        if let index = contacts.firstIndex(where: { $0.id == contact.id }) {
            contacts[index] = contact
            saveContacts(contacts)
            return true
        }
        return false
    }

    func deleteContact(id: Int64) -> Bool {
        var contacts = getAllContacts()
        if let index = contacts.firstIndex(where: { $0.id == id }) {
            contacts.remove(at: index)
            saveContacts(contacts)
            return true
        }
        return false
    }

    func getAllContacts() -> [EmergencyContact] {
        if let data = UserDefaults.standard.data(forKey: contactsKey),
           let contacts = try? JSONDecoder().decode([EmergencyContact].self, from: data) {
            return contacts
        }
        return []
    }

    private func saveContacts(_ contacts: [EmergencyContact]) {
        if let data = try? JSONEncoder().encode(contacts) {
            UserDefaults.standard.set(data, forKey: contactsKey)
        }
    }
}