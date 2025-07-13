# 🌿 Smart Irrigation Monitor

<div align="center">
  <img src="img.png" alt="Smart Irrigation App Screenshot" width="300"/>
  <img src="img_1.png" alt="Smart Irrigation App Screenshot 2" width="300"/>
</div>

---

## 📱 About the App

The **Smart Irrigation Monitor** is a modern Android application built with Jetpack Compose that provides real-time monitoring and control of irrigation systems. The app connects to Firebase Realtime Database to display live data from two irrigation sectors, offering farmers and agricultural professionals a comprehensive view of their irrigation infrastructure.

### ✨ Key Features

- 🌡️ **Real-time Moisture Monitoring** - Track soil moisture levels in both sectors with animated progress bars
- 💧 **Valve Status Tracking** - Monitor valve on/off status for each sector
- 🔄 **Sector Working Status** - View which sectors are currently active
- ✅ **Sensor Acknowledgments** - Real-time sensor connectivity status with visual indicators
- 🚜 **Motor Status Display** - Monitor motor running/stopped status with clear visual feedback
- 🌊 **Dynamic Animations** - Beautiful water wave animations that change based on motor status
- 🔄 **Pull-to-Refresh** - Swipe down to manually refresh data
- 📶 **Connectivity Aware** - Built-in network connectivity checking
- 🎨 **Material 3 Design** - Modern UI following Material Design 3 principles

---

## 🚀 Screenshots

| Main Dashboard | Sector Details |
|----------------|----------------|
| ![Dashboard](img.png) | ![Sectors](img_1.png) | 

---

## 🛠️ Tech Stack

### **Core Technologies**
- **Kotlin** - Modern Android development language
- **Jetpack Compose** - Declarative UI toolkit for native Android
- **Material 3** - Latest Material Design components and theming

### **Backend & Database**
- **Firebase Realtime Database** - Real-time data synchronization
- **Firebase Analytics** - User engagement tracking
- **Google Services** - Firebase integration

### **UI & Animations**
- **Accompanist SwipeRefresh** - Pull-to-refresh functionality
- **Lottie Compose** - High-quality animations
- **Custom Animations** - Water wave effects and bubbles based on motor state

### **Architecture & Tools**
- **Gradle Version Catalogs** - Centralized dependency management
- **Compose BOM** - Consistent Compose library versions
- **Kotlin Coroutines** - Asynchronous programming

---

## 📋 Prerequisites

- Android Studio Hedgehog | 2023.1.1 or later
- Android SDK API 24 (Android 7.0) or higher
- Target SDK API 35 (Android 15)
- Kotlin 1.9.0 or later
- Firebase project with Realtime Database enabled

---

## 🔧 Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/smart-irrigation-monitor.git
cd smart-irrigation-monitor
```

### 2. Firebase Configuration
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select an existing one
3. Add an Android app to your project
4. Use package name: `com.example.smartirrigationonlyread`
5. Download the `google-services.json` file
6. Place it in the `app/` directory

### 3. Firebase Database Structure
Set up your Firebase Realtime Database with the following structure:

```json
{
  "motor": {
    "status": false
  },
  "sectors": {
    "sector1": {
      "avgMoisture": 45.5,
      "valveStatus": true,
      "working": 1,
      "sensor1Ack": true,
      "sensor2Ack": false
    },
    "sector2": {
      "avgMoisture": 62.3,
      "valveStatus": false,
      "working": 0,
      "sensor1Ack": true,
      "sensor2Ack": true
    }
  }
}
```

### 4. Build and Run
1. Open the project in Android Studio
2. Sync the project with Gradle files
3. Connect your Android device or start an emulator
4. Run the app using `Ctrl+R` (Windows/Linux) or `Cmd+R` (Mac)

---

## 🏗️ Project Structure

```
app/
├── src/main/java/com/example/smartirrigationonlyread/
│   ├── MainActivity.kt              # Main activity and UI components
│   ├── WaterWave.kt                # Water wave animation component
│   ├── WaterWaveWithBubbles.kt     # Enhanced wave animation with bubbles
│   ├── MotorStatusObserver.kt      # Motor status monitoring logic
│   └── ui/theme/                   # Material 3 theming
│       ├── Color.kt
│       ├── Theme.kt
│       ├── Type.kt
│       └── SmartIrrigationonlyreadTheme.kt
├── res/                            # App resources
└── google-services.json           # Firebase configuration
```

---

## 🎨 UI Components

### SectorCard
- Displays moisture level with animated progress bar
- Shows valve status, working status, and sensor acknowledgments
- Color-coded for easy identification (Sector 1: Green, Sector 2: Blue)

### MotorStatusCard
- Real-time motor status display
- Visual indicators for running/stopped states

### Water Animations
- **WaterWave**: Calm wave animation when motor is running
- **WaterWaveWithBubbles**: Active bubbling animation when motor is stopped

---

## 📱 App Features in Detail

### Real-time Data Monitoring
The app uses Firebase Realtime Database listeners to provide instant updates when any irrigation system parameter changes.

### Connectivity Awareness
Built-in network connectivity checking ensures the app gracefully handles offline scenarios.

### Pull-to-Refresh
Users can manually refresh data by pulling down on the main screen.

### Responsive Design
The UI adapts to different screen sizes and orientations while maintaining usability.

---

## 🔒 Permissions

The app requires the following permissions:
- `INTERNET` - For Firebase connectivity
- `ACCESS_NETWORK_STATE` - For network connectivity checking

---

## 🛡️ Security Considerations

- Firebase Security Rules should be configured to restrict database access
- Consider implementing user authentication for production use
- Validate all incoming data from Firebase

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Your Name**
- GitHub: [Kaustubh Deshpande](https://github.com/deshkaustubh)
- Email: contact.deshkaustubh@gmail.com

---

<div align="center">
  Made with ❤️ for sustainable agriculture
</div>
