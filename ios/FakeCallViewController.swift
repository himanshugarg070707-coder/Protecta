//
//  FakeCallViewController.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import UIKit

class FakeCallViewController: UIViewController {
    var callerName: String?

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Fake Call"
        view.backgroundColor = .white
        // TODO: Implement fake call UI
        let label = UILabel()
        label.text = "Incoming Call from \(callerName ?? "Unknown")\n(Under Development)"
        label.textAlignment = .center
        label.numberOfLines = 0
        label.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(label)
        NSLayoutConstraint.activate([
            label.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            label.centerYAnchor.constraint(equalTo: view.centerYAnchor)
        ])
    }
}