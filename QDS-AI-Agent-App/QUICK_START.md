# QDS AI Agent Android App - Quick Start Guide

## What is This?

This is a complete, ready-to-build Android application that creates an APK file for the **QDS AI Agent** app. When installed on an Android device, it provides a chat interface to communicate with your backend server.

## Quick Start - 3 Steps

### Step 1: Open the Project
- Open **Android Studio**
- Click: `File` → `Open` → Navigate to `QDS-AI-Agent-App` folder → Select the folder
- Wait for Gradle to sync (bottom right status)

### Step 2: Build the APK
- Once synced, click: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
- A notification will appear when complete
- The APK file is generated in: `app/build/outputs/apk/debug/app-debug.apk`

### Step 3: Install on Device
**Option A - Using USB:**
1. Connect Android phone via USB cable
2. Enable USB Debugging (Settings → Developer Options → USB Debugging)
3. In Android Studio, click: `Run` → `Run 'app'` (or Shift+F10)
4. Select your device and click OK

**Option B - Direct APK Transfer:**
1. Find the APK at: `app\build\outputs\apk\debug\app-debug.apk`
2. Copy to your phone via USB or email
3. Open file manager on phone
4. Tap the APK file to install
5. Grant all permissions

## Features

✅ **Chat Interface** - Send messages to your backend
✅ **Settings** - Configure IP and port (default: 192.168.1.65:8080)
✅ **Health Check** - Automatic server connectivity verification
✅ **Material Design** - Modern, clean UI

## How the App Works

1. **User enters message** and clicks Send
2. **App validates server** connection via: `http://[IP]:[PORT]/api/health`
3. **Shows response** - Success ✓ or Failed ✗
4. **User can change settings** - Tap gear icon → Enter new IP:Port → Save

## Default Configuration

- **Server IP**: 192.168.1.65
- **Server Port**: 8080
- **Health Check Endpoint**: /api/health

Change these in the Settings screen (gear icon) anytime.

## File Structure

```
QDS-AI-Agent-App/
├── app/src/main/
│   ├── java/com/qds/aiagent/        ← Kotlin code
│   ├── res/                          ← UI layouts & resources
│   └── AndroidManifest.xml           ← App configuration
├── build.gradle.kts                  ← Gradle build config
└── README.md                         ← Full documentation
```

## Requirements

- **Android Studio** - Download from https://developer.android.com/studio
- **Java JDK 8+** - Usually installed with Android Studio
- **Android Phone** - API 24 or higher (Android 7.0+)

## Troubleshooting

| Issue | Solution |
|-------|----------|
| "Gradle sync failed" | Click `Sync Now` button, or `File → Sync Project with Gradle Files` |
| "SDK not installed" | Android Studio → Tools → SDK Manager → Install required SDK |
| "Can't connect to server" | Check IP/Port in Settings, ensure backend is running |
| "Build failed" | Right-click project → `Gradle → Clean`, then rebuild |

## Development Notes

The app is built with:
- **Kotlin** - Modern Android language
- **OkHttp3** - Efficient HTTP requests
- **Material Design 3** - Beautiful UI components
- **Coroutines** - Non-blocking network calls

## Next Steps

1. ✅ Build APK (see Step 2 above)
2. ✅ Install on device (see Step 3 above)
3. ✅ Test with your backend server
4. ✅ Customize IP/Port in Settings
5. ✅ Start chatting!

## Support

For detailed information, see [README.md](README.md) in the project root.

---

**Ready to build?** Open Android Studio and follow the Quick Start steps above!
