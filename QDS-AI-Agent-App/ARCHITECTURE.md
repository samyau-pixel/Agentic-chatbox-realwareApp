# QDS AI Agent - Architecture & Features

## Application Architecture

```
┌─────────────────────────────────────────────────────┐
│                   QDS AI Agent App                   │
│                   (Android Device)                    │
├─────────────────────────────────────────────────────┤
│                                                       │
│  ┌──────────────────────────────────────────────┐   │
│  │        MainActivity (Chat Screen)             │   │
│  │  - Displays chat history                     │   │
│  │  - Input field for user messages             │   │
│  │  - Send button                               │   │
│  │  - Settings button                           │   │
│  └──────────────────────────────────────────────┘   │
│                         │                             │
│                         ▼                             │
│  ┌──────────────────────────────────────────────┐   │
│  │      ChatAdapter (ListView Adapter)          │   │
│  │  - Renders user messages (right-aligned)    │   │
│  │  - Renders bot messages (left-aligned)      │   │
│  │  - Shows timestamps                         │   │
│  └──────────────────────────────────────────────┘   │
│                         │                             │
│                         ▼                             │
│  ┌──────────────────────────────────────────────┐   │
│  │       ChatMessage (Data Model)               │   │
│  │  - message: String                          │   │
│  │  - isUserMessage: Boolean                   │   │
│  │  - timestamp: Long                          │   │
│  └──────────────────────────────────────────────┘   │
│                         │                             │
│                         ▼                             │
│  ┌──────────────────────────────────────────────┐   │
│  │        ApiService (Network Layer)            │   │
│  │  - Handles HTTP requests                    │   │
│  │  - Calls /api/health endpoint               │   │
│  │  - Manages connection timeout               │   │
│  │  - Returns success/failure status           │   │
│  └──────────────────────────────────────────────┘   │
│                         │                             │
│                         ▼                             │
│  ┌──────────────────────────────────────────────┐   │
│  │   OkHttp3 (HTTP Client Library)              │   │
│  │  - Connection pooling                       │   │
│  │  - Automatic retry                          │   │
│  │  - Timeout management                       │   │
│  └──────────────────────────────────────────────┘   │
│                                                       │
│  ┌──────────────────────────────────────────────┐   │
│  │    SettingsActivity (Settings Screen)        │   │
│  │  - Input field for IP:Port                  │   │
│  │  - Format validation                        │   │
│  │  - Save to SharedPreferences                │   │
│  │  - Cancel button                            │   │
│  └──────────────────────────────────────────────┘   │
│                                                       │
│  ┌──────────────────────────────────────────────┐   │
│  │  SharedPreferences (Local Storage)           │   │
│  │  - Stores IP:Port configuration            │   │
│  │  - Persists across app restarts             │   │
│  └──────────────────────────────────────────────┘   │
│                                                       │
└─────────────────────────────────────────────────────┘
                          │
                          │ HTTP GET Request
                          │ /api/health
                          ▼
        ┌──────────────────────────────────┐
        │   Backend Server                 │
        │   192.168.1.65:8080              │
        │   (or custom IP:Port)            │
        └──────────────────────────────────┘
```

## Core Components

### 1. **MainActivity.kt**
**Purpose**: Main chat interface

**Key Features**:
- Displays chat history in ListView
- Input field for user messages
- Send button to submit messages
- Settings button (gear icon)
- Loads configuration on resume
- Performs health checks asynchronously

**Lifecycle**:
```
onCreate() → initializeViews() → setupAdapter() → setupListeners()
                ↓
            onResume() → loadSettings()
                ↓
            (User interaction & messaging)
```

### 2. **SettingsActivity.kt**
**Purpose**: Configure server IP and port

**Key Features**:
- EditText for IP:Port input
- Format validation (IP.IP.IP.IP:Port)
- Save button to persist settings
- Cancel button to discard changes
- Back navigation support

**Validation Rules**:
- IP: Must be 4 octets (0-255 each)
- Port: Must be 1-65535
- Format: "192.168.1.65:8080"

### 3. **ApiService.kt**
**Purpose**: Handle HTTP communication

**Key Method**:
```kotlin
suspend fun healthCheck(): Boolean
```

**Features**:
- Uses OkHttp3 for requests
- 10-second connection timeout
- 10-second read timeout
- Returns success/failure boolean
- Handles exceptions gracefully

**Endpoint Called**:
```
GET http://{ip}:{port}/api/health
```

### 4. **ChatMessage.kt**
**Purpose**: Data model for messages

**Properties**:
```kotlin
data class ChatMessage(
    val text: String,           // Message content
    val isUserMessage: Boolean, // True if user sent it
    val timestamp: Long         // Unix timestamp
)
```

### 5. **ChatAdapter.kt**
**Purpose**: Display messages in ListView

**Features**:
- Different layouts for user/bot messages
- User messages: Right-aligned, light blue background
- Bot messages: Left-aligned, gray background
- Shows timestamps for all messages
- Proper list scrolling and recycling

### 6. **QDSApp.kt**
**Purpose**: Application singleton

**Features**:
- App-wide initialization
- Can be extended for analytics, crash reporting, etc.

## Data Flow

### User sends message:
```
User types message
        ↓
Click "Send" button
        ↓
Message added to list (local)
        ↓
ApiService.healthCheck() called (async)
        ↓
    HTTP GET /api/health
        ↓
    Server responds
        ↓
Response added to chat list
        ↓
Chat updated on screen
```

### App startup:
```
App starts
    ↓
MainActivity.onCreate()
    ↓
loadSettings() from SharedPreferences
    ↓
Get IP:Port (default or saved)
    ↓
Initialize ApiService with IP:Port
    ↓
Show chat interface
```

### Configuration changed:
```
User clicks Settings button
    ↓
SettingsActivity opens
    ↓
User enters IP:Port
    ↓
Click "Save"
    ↓
Validate format
    ↓
Save to SharedPreferences
    ↓
Return to MainActivity
    ↓
onResume() → loadSettings()
    ↓
ApiService reinitializes with new IP:Port
```

## UI Components

### Activity Layouts

#### **activity_main.xml**
- Header with app title and settings button
- ListView for chat messages (vertical, scrollable)
- Bottom input area with EditText and Send button

#### **activity_settings.xml**
- LinearLayout with vertical orientation
- Label: "IP and Port"
- EditText for input
- Format hint: "192.168.1.65:8080"
- Buttons: Save and Cancel

#### **item_user_message.xml**
- Light blue background (#E3F2FD)
- Right-aligned message text
- Gray timestamp below message
- Padding and margins for readability

#### **item_bot_message.xml**
- Light gray background (#F5F5F5)
- Left-aligned message text
- Gray timestamp below message
- Same styling as user but different color

## Dependencies

### AndroidX
- **androidx.core:core-ktx** - Core Android extensions
- **androidx.appcompat:appcompat** - Backward compatibility
- **androidx.constraintlayout** - Flexible layouts
- **androidx.lifecycle:lifecycle-runtime-ktx** - Lifecycle management

### UI/Material Design
- **com.google.android.material:material** - Material Components
  - Modern buttons
  - Floating action buttons
  - EditText styling
  - CardView

### Networking
- **com.squareup.okhttp3:okhttp** - HTTP client
  - Connection pooling
  - Automatic retries
  - Timeout handling
  - Request/response interception

### JSON Processing
- **com.google.code.gson:gson** - JSON serialization (for future expansion)
- **com.squareup.retrofit2:retrofit** - REST client (for future expansion)

### Testing
- **junit:junit** - Unit testing framework
- **androidx.test.ext:junit** - Android test extensions
- **androidx.test.espresso:espresso-core** - UI testing framework

## Resource Files

### Strings (values/strings.xml)
- UI text labels
- User-facing messages
- Hints and placeholders

### Colors (values/colors.xml)
- Material Design colors
- Primary: Blue (#2196F3)
- Secondary: Teal (#03DAC6)
- Custom: User message, Bot message backgrounds

### Themes (values/themes.xml)
- Material theme with custom colors
- ActionBar configuration
- Default font sizes

### Dimensions (values/dimens.xml)
- Icon sizes (48dp)
- Padding values (8dp, 16dp)
- Text sizes (14sp, 20sp)

### Drawables (drawable/)
- EditText background (rounded corners with border)
- Secondary button background (bordered)

## Permissions Required

### AndroidManifest.xml
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

**INTERNET**: Required for HTTP requests to backend server
**ACCESS_NETWORK_STATE**: Optional, can check if device is online

## Configuration Files

### build.gradle.kts (Project)
- Plugin versions (Android 8.2.0, Kotlin 1.9.10)
- Repository configuration

### app/build.gradle.kts
- App ID: com.qds.aiagent
- Min SDK: 24 (Android 7.0)
- Target SDK: 34 (Android 14)
- Dependencies declaration
- Build type configurations

### gradle.properties
- JVM arguments (-Xmx2048m)
- Gradle parallel execution
- AndroidX settings
- Kotlin code style

## Performance Optimizations

1. **Network Requests**: Asynchronous using Coroutines
   - Doesn't block UI thread
   - Smooth user experience

2. **List Rendering**: RecyclerView pattern
   - Efficient memory usage
   - Smooth scrolling with many messages

3. **Code Shrinking**: ProGuard rules
   - Smaller APK size
   - Removes unused code

4. **Connection Pooling**: OkHttp3
   - Reuses connections
   - Faster subsequent requests

## Security Considerations

1. **Network**: Uses plain HTTP (can be upgraded to HTTPS)
2. **Input Validation**: IP:Port format validation
3. **Permissions**: Only requests necessary permissions
4. **Storage**: Settings stored in SharedPreferences (device-specific)
5. **No Sensitive Data**: Doesn't store credentials or secrets

## Future Enhancement Opportunities

1. **HTTPS/TLS Support**: Secure communication
2. **Message Persistence**: Save chat history to database
3. **User Authentication**: Login system
4. **Message Encryption**: End-to-end encryption
5. **File Upload**: Send images/files to server
6. **Voice Messages**: Audio recording and sending
7. **Offline Support**: Queue messages when offline
8. **Push Notifications**: Server-initiated messages
9. **Message Reactions**: Emoji reactions to messages
10. **User Profiles**: Display user information

## Testing Strategy

### Unit Tests (ExampleUnitTest.kt)
- Test ApiService methods
- Test data validation
- Test message formatting

### Integration Tests (ExampleInstrumentedTest.kt)
- Test Activity lifecycle
- Test ListView rendering
- Test settings persistence

### Manual Testing
- Settings screen input validation
- Network connectivity handling
- Message sending and receiving
- UI responsiveness

---

**This architecture provides a solid foundation for a chat application with easy extensibility for future features.**
