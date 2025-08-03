# QuickReader

QuickReader is an Android application that uses your device's camera to capture text from images and displays it using speed reading techniques. Perfect for quickly reading documents, books, signs, or any printed text through your phone's camera.

## Features

- **Camera Text Recognition**: Capture text from images using your device's camera
- **Speed Reading**: Display extracted text one word at a time at adjustable speeds
- **Adjustable Reading Speed**: Control reading speed from 50 to 650+ words per minute (WPM)
- **Reading Controls**: Play, pause, stop, and navigate through text
- **Progress Tracking**: See your reading progress with word count and position
- **Real-time Speed Adjustment**: Change reading speed while reading without interruption

## How It Works

1. **Capture**: Point your camera at text and tap the capture button
2. **Process**: The app uses Google ML Kit to extract text from the image
3. **Read**: Text is displayed one word at a time at your preferred speed
4. **Control**: Use playback controls to pause, resume, or jump to specific positions

## Requirements

- Android 7.0 (API level 24) or higher
- Camera permission
- Device with rear-facing camera

## Technology Stack

- **Language**: Kotlin
- **UI**: Android View Binding
- **Camera**: CameraX library
- **Text Recognition**: Google ML Kit Text Recognition API
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## Installation

1. Clone this repository
2. Open the project in Android Studio
3. Build and run the application on your Android device

```bash
git clone https://github.com/your-username/quickreader.git
cd quickreader
```

## Usage

### Main Screen
- Grant camera permission when prompted
- Adjust the reading speed using the speed control slider (50-650+ WPM)
- Point your camera at text and tap "Take Photo" to capture

### Speed Reader Screen
- **Play/Pause Button**: Start or pause reading
- **Stop Button**: Stop reading and return to the beginning
- **Speed Slider**: Adjust reading speed in real-time
- **Progress Bar**: Jump to any position in the text
- **Next Page Button**: Return to the main screen for new capture

## Dependencies

- AndroidX Core KTX
- AppCompat
- Material Design Components
- ConstraintLayout
- CameraX libraries (Core, Camera2, Lifecycle, View)
- Google ML Kit Text Recognition
- Activity KTX for permissions

## Permissions

- `CAMERA`: Required to capture images for text extraction

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the GNU General Public License v3.0 License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Google ML Kit for text recognition capabilities
- CameraX for modern camera implementation
- Material Design for UI components
