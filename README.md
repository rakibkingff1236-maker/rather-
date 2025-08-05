# QR Code Android App

A complete Android application built with Java that can generate and scan QR codes using the ZXing library.

## Features

### QR Code Generator
- Enter any text to generate a QR code
- Display the generated QR code as an image
- Save QR codes to device gallery
- Share QR codes with other apps
- Modern Material Design UI

### QR Code Scanner
- Scan QR codes using device camera
- Real-time camera preview with scanning overlay
- Display decoded results
- Copy scanned text to clipboard
- Share scan results
- Proper camera permission handling

## Technical Stack

- **Language**: Java
- **UI Framework**: Android SDK with Material Design Components
- **QR Code Library**: ZXing (Zebra Crossing)
- **Camera Integration**: ZXing Android Embedded
- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: API 34 (Android 14)

## Project Structure

```
app/
├── src/main/
│   ├── java/com/example/qrcodeapp/
│   │   ├── MainActivity.java           # Main screen with navigation
│   │   ├── GenerateQRActivity.java     # QR code generation
│   │   └── ScanQRActivity.java         # QR code scanning
│   ├── res/
│   │   ├── layout/                     # XML layout files
│   │   ├── drawable/                   # Icons and drawables
│   │   ├── values/                     # Colors, strings, themes
│   │   └── xml/                        # Configuration files
│   └── AndroidManifest.xml             # App permissions and activities
├── build.gradle                        # App-level dependencies
└── proguard-rules.pro                  # Code obfuscation rules
```

## Setup Instructions

### Prerequisites
- Android Studio (latest version recommended)
- Android SDK with API level 21 or higher
- Device or emulator with camera support

### Installation Steps

1. **Clone or Download the Project**
   ```bash
   git clone <repository-url>
   cd QRCodeApp
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the project folder and open it

3. **Sync Project**
   - Android Studio will automatically detect the Gradle files
   - Click "Sync Now" when prompted
   - Wait for the sync to complete

4. **Build the Project**
   - Go to `Build` > `Make Project` or press `Ctrl+F9`
   - Ensure no build errors occur

5. **Run the App**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift+F10`
   - Grant camera permission when prompted

## Dependencies

The following dependencies are automatically managed by Gradle:

```gradle
// Core Android libraries
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.10.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

// ZXing library for QR code functionality
implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
implementation 'com.google.zxing:core:3.5.2'
```

## Permissions

The app requires the following permissions:

- **Camera**: Required for QR code scanning
- **Write External Storage**: Required for saving QR codes (Android 9 and below)

Permissions are handled properly with runtime permission requests and user-friendly explanations.

## Key Features Implementation

### QR Code Generation
- Uses ZXing's `QRCodeWriter` class
- Generates 512x512 pixel QR codes
- Converts BitMatrix to Android Bitmap
- Saves to MediaStore for Android 10+ compatibility

### QR Code Scanning
- Utilizes `DecoratedBarcodeView` for camera preview
- Continuous scanning with `BarcodeCallback`
- Automatic pause/resume handling
- Proper camera lifecycle management

### Modern UI Design
- Material Design 3 components
- Consistent color scheme and typography
- Responsive layouts with ConstraintLayout
- Proper navigation with back button support

## Troubleshooting

### Common Issues

1. **Camera not working**
   - Ensure camera permission is granted
   - Check if device has a camera
   - Restart the app if needed

2. **Build errors**
   - Clean and rebuild project: `Build` > `Clean Project`
   - Check internet connection for dependency downloads
   - Update Android Studio and SDK tools

3. **QR codes not saving**
   - Check storage permissions on older Android versions
   - Ensure sufficient storage space
   - Try saving to a different location

### Testing Tips

- Test on both real devices and emulators
- Try generating various types of text (URLs, plain text, numbers)
- Test camera functionality in different lighting conditions
- Verify QR codes work with other scanners

## Future Enhancements

Potential improvements for the app:

- Support for other barcode formats (Code 128, EAN, etc.)
- Batch QR code generation
- Custom QR code styling (colors, logos)
- History of scanned/generated codes
- Export options (PDF, multiple formats)
- Dark mode theme
- Multi-language support

## License

This project is open source and available under the MIT License.