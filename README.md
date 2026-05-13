# Protecta - Women Safety Android App (Java)

Protecta is a beginner-friendly Android application (Java + XML) designed for a college project on women safety.

## Step 1 - Architecture (High-Level)

Protecta uses a simple layered architecture:

1. **UI Layer (Activities + XML Layouts)**
   - `MainActivity`: Home dashboard + SOS triggers
   - `EmergencyContactsActivity`: Add/Edit/Delete emergency contacts
   - `LiveTrackingActivity`: Real-time GPS updates
   - `SettingsActivity`: Feature toggles
   - `FakeCallActivity`: Simulated incoming call screen
   - `SafetySuggestionsActivity`: Dummy SAFE/UNSAFE route suggestions

2. **Data Layer (SQLite)**
   - `ContactsDatabaseHelper`: Creates and manages contact table
   - `ContactsRepository`: CRUD operations for emergency contacts

3. **Utility + Service Layer**
   - `SOSManager`: Sends SMS with location to all saved contacts
   - `ShakeDetector` + `MultiTapDetector`: SOS trigger helpers
   - `PanicKeywordDetector`: Voice keyword matching
   - `AppSettings`: SharedPreferences-based feature toggles
   - `SafetyMonitoringService`: Foreground motion monitoring + auto SOS

This architecture keeps code modular, easy to read, and easy to extend.

---

## Core Features Implemented

1. **SOS Alert**
   - Trigger methods:
     - Phone shake
     - Multi-tap SOS button
     - Manual long-press on SOS
   - Sends SMS to all emergency contacts from SQLite
   - Includes GPS location link (Google Maps URL) when available

2. **Live Location Tracking**
   - Uses Fused Location Provider (`play-services-location`)
   - Shows latitude, longitude, speed, accuracy, and update time

3. **Fake Call Feature**
   - Realistic incoming call style screen
   - Ringtone + vibration
   - Accept/Reject actions

4. **Voice Detection (Simplified)**
   - Uses Android Speech Recognizer intent
   - Detects panic keywords like `help`, `save me`, `emergency`
   - Triggers SOS when keyword is detected

5. **Motion Detection**
   - Foreground service listens accelerometer values
   - Detects:
     - running pattern
     - sudden stop
     - abnormal motion spike

6. **Auto SOS Trigger**
   - If abnormal motion is detected and Auto SOS is ON, SOS is sent automatically
   - Includes cooldown to avoid repeated spam

7. **Smart Safety Suggestions (Simulated)**
   - Dummy route list
   - Marks each route as SAFE/UNSAFE with risk notes

---

## Tech Stack

- **Language:** Java
- **UI:** XML (Material components)
- **Database:** SQLite
- **Location:** FusedLocationProviderClient
- **Sensors:** Accelerometer (SensorManager)
- **SMS:** `SmsManager`

---

## Project Structure

```text
Protecta/
├── README.md
├── build.gradle
├── settings.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── gradle/wrapper/
└── app/
    ├── build.gradle
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/example/safeher/
        │   ├── activities/
        │   ├── database/
        │   ├── models/
        │   ├── services/
        │   └── utils/
        └── res/
            ├── layout/
            ├── drawable/
            └── values/
```

---

## Required Permissions

- `SEND_SMS`
- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `RECORD_AUDIO`
- `FOREGROUND_SERVICE`
- `FOREGROUND_SERVICE_LOCATION`
- `VIBRATE`
- `POST_NOTIFICATIONS` (Android 13+)

---

## Setup & Run (Android Studio)

1. Open Android Studio.
2. Select **Open** and choose the `Protecta` folder.
3. Let Gradle sync complete.
4. Connect Android device / start emulator.
5. Run app.
6. On first launch, allow requested permissions.

---

## How to Demo in Presentation

1. Open app home screen and explain status cards.
2. Add 2 emergency contacts in **Emergency Contacts**.
3. Return to home and trigger SOS by tapping SOS button quickly 4 times.
4. Show that SMS sends location link.
5. Open **Live Tracking** and show real-time coordinates.
6. Open **Fake Call** and simulate incoming call.
7. Use **Voice Panic Detection** and speak "help" or "save me".
8. Open **Settings**, enable monitoring service and auto SOS.
9. Shake/move phone to simulate motion detection and auto SOS behavior.
10. Show **Smart Safety Suggestions** SAFE/UNSAFE routes.

---

## Test Cases

1. **Contact CRUD Test**
   - Add contact -> visible in list
   - Edit contact -> updated details shown
   - Delete contact -> removed from list

2. **SOS Trigger Test (Multi-Tap)**
   - Tap SOS 4 times quickly
   - Expected: SOS SMS sent to all contacts

3. **SOS Trigger Test (Shake)**
   - Shake phone strongly
   - Expected: SOS SMS sent

4. **Location in SOS Test**
   - Ensure GPS ON
   - Trigger SOS
   - Expected: message includes Google Maps location URL

5. **Voice Detection Test**
   - Speak "help"
   - Expected: auto SOS triggered

6. **Motion Detection Test**
   - Enable monitoring service + auto SOS
   - Perform sudden motion pattern
   - Expected: auto SOS from service

7. **Fake Call UI Test**
   - Start fake call screen
   - Expected: ringtone/vibration + accept/reject actions

8. **Tracking Test**
   - Start live tracking
   - Expected: latitude/longitude/speed updates every few seconds

---

## Innovative Points to Highlight

- Multi-modal SOS triggers (shake + multi-tap + voice + motion)
- Offline emergency contact management with SQLite
- Foreground motion monitoring for auto SOS
- Practical fallback design: complex AI features are simulated safely for project scope
- Presentation-friendly fake call and route risk simulation

---

## Future Enhancements

- Real map integration (Google Maps SDK)
- Trusted-area geofencing
- Cloud sync for emergency contacts
- Background speech service for continuous keyword listening
- ML-based anomaly detection for motion behavior
