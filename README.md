# FocusModes - An Android Productivity App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0-blue?logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.8.3-blue?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![MIT License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

FocusModes is a modern, offline-first Android application designed to help users minimize distractions and improve their productivity using proven techniques like the Pomodoro timer, combined with a powerful app-blocking feature.

This project was built from the ground up using 100% Kotlin and the latest Jetpack libraries, showcasing a clean, reactive, and robust architecture.

## 📸 Screenshots & Demo

*[**IMPORTANT:** Replace this section with a screen recording GIF or a few high-quality screenshots of your app in action.]*

<p align="center">
  <img src="URL_TO_YOUR_GIF_OR_SCREENSHOT_1" width="250" />
  <img src="URL_TO_YOUR_GIF_OR_SCREENSHOT_2" width="250" />
  <img src="URL_TO_YOUR_GIF_OR_SCREENSHOT_3" width="250" />
</p>

## ✨ Features

* **Pomodoro Timer:** A fully background-capable timer with configurable work and break intervals, running inside a `ForegroundService`.
* **Focus Modes:** Users can create different "modes" (e.g., "Deep Work," "Study"), each with its own timer settings and a unique list of apps to block.
* **App Blocker:** A persistent `AccessibilityService` that detects when a user tries to open a distracting app and presents a beautiful, non-intrusive overlay to block access.
* **User Statistics:** A dashboard that visualizes user productivity with charts and historical data.
* **Permissions Onboarding:** A user-friendly screen that guides the user through granting all necessary permissions, including special manufacturer-specific settings.
* **Modern UI:** A beautiful, animated, and responsive UI built entirely with Jetpack Compose and Material 3.
* **Offline First:** All user data is stored locally in a Room database. The app requires no internet permission.

## 🛠️ Tech Stack & Architecture

This project is built as a showcase of modern Android development best practices.

* **Tech Stack:**
    * **UI:** Jetpack Compose, Material 3
    * **Language:** Kotlin
    * **Async:** Kotlin Coroutines & Flow (`StateFlow`, `flatMapLatest`)
    * **Architecture:** Clean Architecture (UI → ViewModel → UseCase → Repository → DataSource)
    * **Navigation:** Jetpack Navigation 3
    * **Dependency Injection:** Hilt
    * **Database:** Room
    * **State Persistence:** Jetpack DataStore
    * **Permissions:** Accompanist Permissions

* **Architecture:** The app follows a strict Clean Architecture pattern, separating the UI, Domain, and Data layers to ensure the codebase is decoupled, scalable, and highly testable.

## 🧠 Challenges & Key Learnings

Building a robust background utility on Android comes with unique challenges. Here are some of the key problems I solved:

### 1. Ensuring the `AccessibilityService` Survives

The biggest challenge was making the app blocker work reliably on real-world devices with aggressive battery optimization (like Xiaomi's MIUI).

* **Problem:** On a stock Android emulator, the `AppBlockerService` worked perfectly. On my MI 10T, after I enabled the service and then swiped the app away from recents, the service would be killed by the OS. Since an `AccessibilityService` cannot be restarted programmatically by the app for security reasons, the app blocking feature would simply stop working. The system settings would show a cryptic "service is malfunctioning" error.
* **Solution:** I learned that standard Android permissions are not enough. The only reliable solution is to detect the device manufacturer and build a UI that guides the user to enable non-standard, OEM-specific settings like **"Autostart"** and setting the app's **"Battery Saver"** to **"No restrictions."** This was a deep dive into the real-world fragmentation of the Android ecosystem.

### 2. Displaying Compose UI from a Service

To create the app-blocking overlay, I needed to draw a Compose UI from a background `AccessibilityService`, which has no UI lifecycle.

* **Problem:** Simply adding a `ComposeView` to the `WindowManager` resulted in a fatal `IllegalStateException: ViewTreeLifecycleOwner not found`.
* **Solution:** I created a custom `LifecycleOwner` class that manually manages a `LifecycleRegistry`. This custom owner is attached to the `ComposeView` before it's added to the window, providing the necessary lifecycle for the Compose runtime to function correctly outside of an `Activity`.

### 3. Modern, State-Driven Navigation

This project was built entirely with the new **Jetpack Navigation 3**.
* **Challenge:** I implemented a multi-tab interface with a `BottomNavBar`. I learned how to preserve the navigation state and `ViewModel` state of each tab independently by managing multiple back stacks within a central `ViewModel`, a core pattern in the new library.

## 🚀 Setup & Build

1.  Clone the repository: `git clone https://github.com/adnanrangrej/Focus-Modes-App.git`
2.  Open the project in the latest version of Android Studio.
3.  Let Gradle sync the dependencies.
4.  Build and run the app.

## 📄 License
