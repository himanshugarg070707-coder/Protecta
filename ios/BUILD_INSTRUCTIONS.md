# iOS Build Instructions for Protecta App

Since iOS development requires macOS and Xcode, follow these steps to build the app:

## Prerequisites
- macOS computer
- Xcode 15+ installed
- iOS device or simulator for testing

## Steps to Build

1. **Create Xcode Project**
   - Open Xcode
   - File > New > Project
   - Choose "App" template
   - Product Name: Protecta
   - Interface: Storyboard (or SwiftUI if preferred)
   - Language: Swift
   - Create the project in a folder

2. **Add Source Files**
   - Copy all .swift files from the `ios` directory to your Xcode project's source folder
   - Replace the default ViewController.swift with MainViewController.swift
   - Rename MainViewController.swift to ViewController.swift or update the storyboard

3. **Configure Frameworks**
   - In Xcode, select your project in the navigator
   - Go to "General" tab
   - Under "Frameworks, Libraries, and Embedded Content", add:
     - MessageUI.framework
     - CoreMotion.framework
     - Speech.framework
     - AVFoundation.framework
     - CoreLocation.framework

4. **Configure Permissions**
   - Open Info.plist
   - Add these keys:
     ```
     <key>NSLocationWhenInUseUsageDescription</key>
     <string>This app needs location access for SOS alerts</string>
     <key>NSMicrophoneUsageDescription</key>
     <string>This app needs microphone access for voice detection</string>
     <key>NSSpeechRecognitionUsageDescription</key>
     <string>This app needs speech recognition for panic keyword detection</string>
     ```

5. **Update AppDelegate (if using UIKit)**
   - In AppDelegate.swift, set the root view controller:
     ```swift
     func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
         window = UIWindow(frame: UIScreen.main.bounds)
         window?.rootViewController = MainViewController()
         window?.makeKeyAndVisible()
         return true
     }
     ```

6. **Build and Run**
   - Select a simulator or connected device
   - Product > Run (⌘R)
   - Or use xcodebuild command line:
     ```
     xcodebuild -project Protecta.xcodeproj -scheme Protecta -sdk iphonesimulator -configuration Debug build
     ```

## Troubleshooting
- If you get framework errors, ensure all frameworks are added
- For speech recognition, ensure the device supports it
- Test on physical device for location and microphone features

## Generate IPA for Distribution
```
xcodebuild -project Protecta.xcodeproj -scheme Protecta -sdk iphoneos -configuration Release archive
```

This will create an archive that can be exported as IPA for TestFlight or App Store.