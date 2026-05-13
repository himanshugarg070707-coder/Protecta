//
//  EmergencyContactsViewController.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import UIKit

class EmergencyContactsViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    private let tableView = UITableView()
    private let emptyLabel = UILabel()
    private let addButton = UIBarButtonItem(barButtonSystemItem: .add, target: nil, action: nil)

    private let repository = ContactsRepository()
    private var contacts: [EmergencyContact] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Emergency Contacts"
        setupUI()
        loadContacts()
    }

    private func setupUI() {
        view.backgroundColor = .white

        tableView.dataSource = self
        tableView.delegate = self
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "ContactCell")
        tableView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(tableView)

        emptyLabel.text = "No emergency contacts added yet.\nTap + to add one."
        emptyLabel.textAlignment = .center
        emptyLabel.numberOfLines = 0
        emptyLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(emptyLabel)

        addButton.target = self
        addButton.action = #selector(addContact)
        navigationItem.rightBarButtonItem = addButton

        NSLayoutConstraint.activate([
            tableView.topAnchor.constraint(equalTo: view.topAnchor),
            tableView.bottomAnchor.constraint(equalTo: view.bottomAnchor),
            tableView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tableView.trailingAnchor.constraint(equalTo: view.trailingAnchor),

            emptyLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            emptyLabel.centerYAnchor.constraint(equalTo: view.centerYAnchor)
        ])
    }

    private func loadContacts() {
        contacts = repository.getAllContacts()
        tableView.reloadData()
        emptyLabel.isHidden = !contacts.isEmpty
    }

    @objc private func addContact() {
        showAddEditDialog(for: nil)
    }

    private func showAddEditDialog(for contact: EmergencyContact?) {
        let alert = UIAlertController(title: contact == nil ? "Add Contact" : "Edit Contact", message: nil, preferredStyle: .alert)

        alert.addTextField { $0.placeholder = "Name" }
        alert.addTextField { $0.placeholder = "Phone Number" }
        alert.addTextField { $0.keyboardType = .phonePad }

        if let contact = contact {
            alert.textFields?[0].text = contact.name
            alert.textFields?[1].text = contact.phoneNumber
        }

        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
        alert.addAction(UIAlertAction(title: contact == nil ? "Add" : "Update", style: .default) { _ in
            guard let name = alert.textFields?[0].text?.trimmingCharacters(in: .whitespaces), !name.isEmpty,
                  let phone = alert.textFields?[1].text?.trimmingCharacters(in: .whitespaces), !phone.isEmpty else {
                self.showToast("Please enter name and phone number")
                return
            }

            if contact == nil {
                let newContact = EmergencyContact(name: name, phoneNumber: phone)
                _ = self.repository.addContact(newContact)
            } else {
                var updated = contact
                updated.name = name
                updated.phoneNumber = phone
                _ = self.repository.updateContact(updated)
            }
            self.loadContacts()
        })

        present(alert, animated: true)
    }

    // MARK: - UITableViewDataSource
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return contacts.count
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "ContactCell", for: indexPath)
        let contact = contacts[indexPath.row]
        cell.textLabel?.text = "\(contact.name) - \(contact.phoneNumber)"
        return cell
    }

    // MARK: - UITableViewDelegate
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        let contact = contacts[indexPath.row]
        showAddEditDialog(for: contact)
    }

    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            let contact = contacts[indexPath.row]
            _ = repository.deleteContact(id: contact.id)
            loadContacts()
        }
    }

    private func showToast(_ message: String) {
        let alert = UIAlertController(title: nil, message: message, preferredStyle: .alert)
        present(alert, animated: true)
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            alert.dismiss(animated: true)
        }
    }
}