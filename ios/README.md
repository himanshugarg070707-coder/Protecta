# Protecta iOS App

This is the iOS version of the Protecta safety app, ported from the Android version.

## Features

- SOS alerts via SMS to emergency contacts
- Shake detection for automatic SOS
- Multi-tap SOS trigger
- Voice detection for panic keywords
- Emergency contacts management
- Fake call feature
- Live tracking (placeholder)
- Safety suggestions (placeholder)
- Settings (placeholder)

## Setup

1. Create a new iOS project in Xcode named "Protecta".
2. Add the Swift files from this directory to your project.
3. Add the necessary frameworks in your project settings:
   - MessageUI
   - CoreMotion
   - Speech
   - AVFoundation
   - CoreLocation
4. Set up the main storyboard or use programmatic UI.
5. In AppDelegate, set MainViewController as the root view controller.
6. Add necessary permissions in Info.plist:
   - Privacy - Location When In Use Usage Description
   - Privacy - Microphone Usage Description
   - Privacy - Speech Recognition Usage Description

## Differences from Android

- SMS sending: iOS requires user interaction via MFMessageComposeViewController, cannot send programmatically.
- Location: Uses CLLocationManager.
- Storage: Uses UserDefaults for simplicity instead of SQLite.
- UI: Uses UIKit instead of Android layouts.

## TODO

- Implement remaining view controllers (LiveTracking, FakeCall, SafetySuggestions, Settings)
- Add proper UI layouts
- Handle permissions properly
- Add background services equivalent
- Test and refine