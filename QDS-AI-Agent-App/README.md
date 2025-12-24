# QDS AI Agent Android App

A native Android chat application that communicates with a QDS AI Agent backend server.

## Features

- 💬 Real-time chat interface
- ⚙️ Configurable server IP and port
- 🔌 Health check endpoint verification
- 📱 Material Design UI
- 🔄 Asynchronous HTTP requests

## Requirements

- Android SDK 24 (API 24) or higher
- Java 8 or higher
- Gradle 8.2+

## Project Structure

```
QDS-AI-Agent-App/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/qds/aiagent/
│   │   │   │   ├── MainActivity.kt          # Chat screen
│   │   │   │   ├── SettingsActivity.kt      # Settings screen
│   │   │   │   ├── ApiService.kt            # HTTP client
│   │   │   │   ├── ChatMessage.kt           # Data model
│   │   │   │   └── ChatAdapter.kt           # List adapter
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_settings.xml
│   │   │   │   │   ├── item_user_message.xml
│   │   │   │   │   └── item_bot_message.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── drawable/
│   │   │   │       ├── edit_text_background.xml
│   │   │   │       └── button_background_secondary.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/                            # Unit tests
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## Building the APK

### Prerequisites

1. **Install Android Studio** - Download from https://developer.android.com/studio
2. **Install Java Development Kit (JDK)** - Java 8 or higher
3. **Set ANDROID_HOME** environment variable to your Android SDK location

### Build Steps

#### Using Android Studio:
1. Open Android Studio
2. Click "File" → "Open" and select the `QDS-AI-Agent-App` folder
3. Wait for Gradle to sync
4. Click "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"
5. The APK will be generated in `app/build/outputs/apk/debug/`

#### Using Command Line (Windows):
```batch
cd C:\Users\Sam\OneDrive\Desktop\Agentic-chat-realware\QDS-AI-Agent-App
gradlew.bat build
```

#### For Release APK (command line):
```batch
gradlew.bat assembleRelease
```

The release APK will be in `app/build/outputs/apk/release/`

## Installation on Android Device

### Via Android Studio:
1. Connect your Android device via USB
2. Enable USB debugging on the device (Settings → Developer Options)
3. In Android Studio, click "Run" → "Run 'app'" (or press Shift+F10)
4. Select your device from the list

### Via ADB (Command Line):
```batch
# Connect device and run:
adb install app\build\outputs\apk\debug\app-debug.apk
```

### Via APK File:
1. Transfer the APK file to your Android device
2. Open a file manager on the device
3. Navigate to the APK and tap to install
4. Grant necessary permissions

## Application Usage

### Main Chat Screen
- **Message Input**: Type your message in the input field
- **Send Button**: Press Enter or tap the Send button to send a message
- **Settings Button**: Tap the gear icon in the top-right to configure server settings

### Settings Screen
- **IP and Port**: Enter the server address in the format `IP:Port`
  - Example: `192.168.1.65:8080`
  - Default: `192.168.1.65:8080`
- **Save**: Saves the settings to device local storage
- **Cancel**: Discards changes and returns to chat

### How It Works

1. **On Message Send**: When you type a message and press Send:
   - The message appears in the chat immediately
   - The app sends an HTTP GET request to `http://{ip}:{port}/api/health`
   - If the server responds successfully, a confirmation message appears
   - If the connection fails, an error message is displayed

2. **Health Check**: The app verifies connectivity to the backend server by calling:
   ```
   GET http://{ip}:{port}/api/health
   ```

## Configuration

### Default Settings
- Server IP: `192.168.1.65`
- Server Port: `8080`
- API Endpoint: `/api/health`

### Modifying Settings
Settings are stored in Android SharedPreferences and can be changed via the Settings screen in the app.

## API Integration

The app communicates with the backend using OkHttp3 library:

```kotlin
// Health check request
GET http://{configured-ip}:{configured-port}/api/health
```

### Example Backend Implementation (Node.js):
```javascript
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok' });
});
```

## Permissions

The app requires the following Android permissions:
- `INTERNET` - For making HTTP requests
- `ACCESS_NETWORK_STATE` - For checking network status

These are declared in `AndroidManifest.xml` and should be automatically granted on Android 6.0+.

## Dependencies

- **AndroidX Core**: Core Android functionality
- **AndroidX AppCompat**: Backward compatibility
- **Material Components**: Material Design UI
- **OkHttp3**: HTTP client library
- **Retrofit2**: REST client (configured but optional for expansion)
- **Gson**: JSON serialization
- **Kotlin Coroutines**: Asynchronous programming

## Building for Different Configurations

### Debug APK (for development):
```batch
gradlew.bat assembleDebug
```

### Release APK (for production):
```batch
gradlew.bat assembleRelease
```

### Clean Build:
```batch
gradlew.bat clean build
```

## Troubleshooting

### "Failed to connect to server"
- Verify the IP address and port are correct
- Ensure the backend server is running
- Check firewall settings
- Verify both devices are on the same network

### "Android SDK not found"
- Install Android SDK via Android Studio
- Set ANDROID_HOME environment variable
- Add Android SDK tools to PATH

### "Build Failed"
- Run `gradlew.bat clean`
- Delete `.gradle` and `build` directories
- Sync Gradle again in Android Studio

## APK File Information

- **Name**: `app-debug.apk` (debug) or `app-release.apk` (release)
- **Location**: `app/build/outputs/apk/{buildType}/`
- **Size**: Approximately 5-8 MB
- **Minimum Android**: API 24 (Android 7.0)
- **Target Android**: API 34 (Android 14)

## License

This project is provided as-is for educational and commercial use.

## Support

For issues or feature requests, please refer to the project documentation or contact the development team.
