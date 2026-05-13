//
//  LiveTrackingViewController.swift
//  Protecta
//
//  Created by GitHub Copilot
//

import UIKit

class LiveTrackingViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Live Tracking"
        view.backgroundColor = .white
        // TODO: Implement live tracking UI
        let label = UILabel()
        label.text = "Live Tracking Feature\n(Under Development)"
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