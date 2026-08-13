# Smart Edge Panel (Android 16 / HiOS Compatible)

Smart Edge Panel is a customizable side-panel overlay and quick launcher for Android built in Kotlin with Jetpack Compose and Android's official WindowManager API.

## Technical Architecture & Security Model
- **No Injected Frame Buffers / Fake Embedding**: Standard Android security prevents drawing third-party app UIs inside another app's View hierarchy without root.
- **Intent Launch Model**: Apps are launched using standard Android Activity launch flags (`Intent.FLAG_ACTIVITY_NEW_TASK`). Multi-window or PiP mode is delegated to supported system components.
- **Service Integration**: Uses Android 14+ `FOREGROUND_SERVICE_TYPE_SPECIAL_USE` to keep the edge overlay handle running persistently.

## How to Build & Install
1. Open Android Studio (Ladybug or newer recommended).
2. Select **Open an Existing Project** and choose the `SmartEdgePanel` folder.
3. Synchronize Gradle files (`build.gradle.kts`).
4. Connect your Android device or TECNO SPARK 50 5G target device via USB debugging.
5. Click **Run 'app'** or execute `./gradlew assembleDebug` in terminal to build the APK.

## Device Configuration & Permissions
1. Launch the application.
2. Go to the **Permissions** tab.
3. Grant **Display over other apps** (System Alert Window permission).
4. (Recommended for TECNO/HiOS) Tap **Battery Settings** and set Smart Edge Panel to "Unrestricted" to avoid aggressive OS background process termination.
