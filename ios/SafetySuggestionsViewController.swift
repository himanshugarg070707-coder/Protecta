//
//  SafetySuggestionsViewController.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import UIKit

class SafetySuggestionsViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Safety Suggestions"
        view.backgroundColor = .white
        // TODO: Implement safety suggestions UI
        let label = UILabel()
        label.text = "Safety Suggestions\n(Under Development)"
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