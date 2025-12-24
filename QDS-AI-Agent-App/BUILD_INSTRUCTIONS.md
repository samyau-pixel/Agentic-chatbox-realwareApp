# Complete Build Instructions for QDS AI Agent APK

## Prerequisites Checklist

Before building, ensure you have:

- [ ] **Android Studio** installed (https://developer.android.com/studio)
- [ ] **Java JDK 8 or higher** (usually comes with Android Studio)
- [ ] **Android SDK 34** (installed via Android Studio SDK Manager)
- [ ] **Gradle 8.2+** (included with Android Studio)
- [ ] **At least 5GB free disk space**

## Installation Steps

### 1. Install Android Studio

**Windows:**
1. Download Android Studio from https://developer.android.com/studio
2. Run the installer (.exe file)
3. Follow the setup wizard
4. During setup, install:
   - Android SDK
   - Android SDK Platform
   - Android Virtual Device (optional)
5. Accept licenses when prompted

**Verification:**
```bash
# In Command Prompt, verify Android SDK is installed:
echo %ANDROID_HOME%
# Should show something like: C:\Users\YourName\AppData\Local\Android\sdk
```

### 2. Project Setup

1. Open **Android Studio**
2. Click `File` → `Open`
3. Navigate to: `C:\Users\Sam\OneDrive\Desktop\Agentic-chat-realware\QDS-AI-Agent-App`
4. Select the folder and click `Open`
5. Wait for Gradle to sync (watch the bottom-right status bar)
   - If you see errors, click `Sync Now` or `Try Again`
   - First sync may take 5-10 minutes

### 3. Configure Build Variants (Optional)

For **Debug Build** (default, for testing):
- No configuration needed, this is the default

For **Release Build** (for production):
1. Create keystore file:
   ```bash
   # In Android Studio Terminal:
   cd app
   keytool -genkey -v -keystore release.keystore -keyalg RSA -keysize 2048 -validity 10000
   # Follow prompts
   ```
2. Update `app/build.gradle.kts` with keystore details

### 4. Building the APK

#### Method A: Using Android Studio GUI (Recommended)

1. In Android Studio, click **Build** menu
2. Select **Build Bundle(s) / APK(s)** → **Build APK(s)**
3. Wait for the build to complete (1-3 minutes)
4. A notification appears: "Build Successful"
5. Click "Locate" to open the folder with the APK

#### Method B: Using Command Line

**For Debug APK:**
```batch
cd C:\Users\Sam\OneDrive\Desktop\Agentic-chat-realware\QDS-AI-Agent-App
gradlew.bat assembleDebug
```

**For Release APK:**
```batch
gradlew.bat assembleRelease
```

#### Method C: Android Studio Terminal

1. In Android Studio, click **Terminal** tab (bottom)
2. Type one of the commands from Method B

### 5. Verify APK was Created

The APK should be at:
```
C:\Users\Sam\OneDrive\Desktop\Agentic-chat-realware\QDS-AI-Agent-App\app\build\outputs\apk\debug\app-debug.apk
```

**Check file size:**
- Should be approximately 5-8 MB
- If smaller (< 1 MB), something went wrong
- If larger (> 20 MB), includes too much data

## Installation on Android Device

### Option 1: Install via USB (Recommended)

**Requirements:**
- Android phone/tablet (Android 7.0 or higher)
- USB cable
- Phone with USB Debugging enabled

**Steps:**
1. Connect phone to computer via USB
2. On phone: Enable USB Debugging
   - Settings → About Phone → Tap Build Number 7 times
   - Settings → Developer Options → Enable USB Debugging
3. In Android Studio:
   - Click **Run** → **Run 'app'** (or press **Shift+F10**)
   - Select your device from the list
   - Click **OK**
4. Wait for installation (1-2 minutes)
5. App appears on your home screen: "QDS AI Agent"

### Option 2: Manual APK Transfer

1. Connect phone via USB
2. Copy APK to phone:
   ```bash
   adb push "app\build\outputs\apk\debug\app-debug.apk" /sdcard/Download/
   ```
   OR copy manually via USB file transfer

3. On phone:
   - Open Files/File Manager
   - Navigate to Downloads folder
   - Tap `app-debug.apk`
   - Tap "Install"
   - Grant all requested permissions

### Option 3: Email or Cloud

1. Copy APK file
2. Email or upload to Google Drive
3. Download on phone
4. Tap to install
5. Grant permissions

## Post-Installation

### First Launch

1. Open "QDS AI Agent" from home screen
2. Grant permissions when prompted:
   - INTERNET access
   - Network access
3. You should see:
   - Chat message area (empty)
   - Message input box
   - Send button
   - Settings button (gear icon)

### Configure Server

1. Click settings button (gear icon, top-right)
2. Enter your server IP and port
   - Format: `192.168.1.65:8080`
   - Default is already filled in
3. Click "Save"

### Test Connection

1. Type a test message: "Hello"
2. Click "Send"
3. App will:
   - Show your message
   - Send health check to `/api/health`
   - Display connection status

## Troubleshooting

### Build Issues

| Problem | Solution |
|---------|----------|
| "Gradle sync failed" | Click `Sync Now`, or use `File → Sync Project with Gradle Files` |
| "SDK not found" | Tools → SDK Manager → Check Android SDK 34 is installed |
| "Build timeout" | Close Android Studio, delete `build` folder, reopen |
| "Out of memory" | Increase Gradle memory in `gradle.properties`: `org.gradle.jvmargs=-Xmx4g` |

### Installation Issues

| Problem | Solution |
|---------|----------|
| "Device not recognized" | Install USB drivers, enable USB Debugging on phone |
| "Installation failed" | Uninstall previous version: `adb uninstall com.qds.aiagent` |
| "Permission denied" | Grant USB access permission in phone notification |
| "Android version too old" | Device needs Android 7.0+ (API 24+) |

### Runtime Issues

| Problem | Solution |
|---------|----------|
| "Can't connect to server" | Check IP/Port format, ensure server is running, check firewall |
| "App crashes on start" | Uninstall and reinstall, clear app cache |
| "Settings not saving" | Uninstall, clear data, reinstall |

## Advanced Build Options

### Build Variants

Edit `app/build.gradle.kts` to add build variants:

```kotlin
flavorDimensions += "version"
productFlavors {
    create("dev") {
        dimension = "version"
        applicationIdSuffix = ".dev"
        versionNameSuffix = "-dev"
    }
    create("prod") {
        dimension = "version"
    }
}
```

### Code Shrinking (Release)

For release builds, ProGuard is configured:
- Rules in: `app/proguard-rules.pro`
- Reduces APK size by ~30%

### Debug Symbols

To include debug symbols for better crash reports:
```kotlin
buildTypes {
    debug {
        debuggable = true
        includeDebugSymbols = true
    }
}
```

## Clean Build

If you encounter persistent issues:

```batch
cd C:\Users\Sam\OneDrive\Desktop\Agentic-chat-realware\QDS-AI-Agent-App
gradlew.bat clean build
```

This:
1. Removes all previous builds
2. Downloads dependencies fresh
3. Rebuilds from scratch
4. Takes 5-10 minutes

## Version Management

Current version: **1.0.0**

To update version for app store release, edit `app/build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        versionCode = 2          // Must increase for updates
        versionName = "1.0.1"    // User-visible version
    }
}
```

## APK Signing

### Debug Keystore (automatic)
- Created automatically
- Used for testing
- NOT suitable for Play Store

### Release Keystore
To sign for Play Store:

1. Generate key:
```bash
keytool -genkey -v -keystore qds-release.keystore -keyalg RSA -keysize 2048 -validity 10000
```

2. Update `app/build.gradle.kts`:
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("path/to/qds-release.keystore")
            storePassword = "your-password"
            keyAlias = "your-alias"
            keyPassword = "your-password"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

## Performance Tips

- **Reduce APK size**: Enable minification in release builds
- **Faster builds**: Disable Instant Run if experiencing issues
- **Better experience**: Install on actual device, not emulator
- **Debug efficiently**: Use logcat to view debug messages

## Next Steps

1. ✅ Complete prerequisites
2. ✅ Follow build instructions
3. ✅ Install on device
4. ✅ Test with backend server
5. ✅ Customize server IP/Port in settings
6. ✅ Deploy to Play Store (if desired)

## Support Resources

- **Android Developers**: https://developer.android.com
- **Gradle Docs**: https://docs.gradle.org
- **Kotlin Docs**: https://kotlinlang.org/docs
- **Material Design**: https://material.io/design

## File Locations Summary

| File | Location |
|------|----------|
| **APK (Debug)** | `app\build\outputs\apk\debug\app-debug.apk` |
| **APK (Release)** | `app\build\outputs\apk\release\app-release.apk` |
| **Main Code** | `app\src\main\java\com\qds\aiagent\` |
| **Layouts** | `app\src\main\res\layout\` |
| **Resources** | `app\src\main\res\values\` |
| **Manifest** | `app\src\main\AndroidManifest.xml` |
| **Build Config** | `app\build.gradle.kts` |

---

**You're all set!** Follow these steps and you'll have a working QDS AI Agent APK ready for your Android device.
