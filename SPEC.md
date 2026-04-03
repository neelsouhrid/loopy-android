# Loopy Android - MIDI Looper App

## Project Overview
- **Name**: Loopy Android
- **Type**: Android MIDI Looper Application
- **Core Functionality**: 10-track MIDI looper that connects with Casio CTX870IN keyboard via USB-MIDI, generates local audio, and supports multi-session management

## Technology Stack & Choices
- **Language**: Kotlin 1.9.x
- **UI Framework**: Jetpack Compose with Material 3
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM with Clean Architecture
- **DI**: Hilt
- **Audio Engine**: Soundfont-based synthesis using libGdx (orSfNet)
- **MIDI**: android.media.midi API
- **Storage**: DataStore for session persistence
- **Build System**: Gradle Kotlin DSL

## Feature List
1. **10-Track Looper** - 5×2 grid UI for track selection/recording
2. **USB-MIDI Support** - Connect Casio CTX870IN via USB OTG
3. **Audio Playback** - Generate local piano audio (muteable, Bluetooth-capable)
4. **MIDI Thru** - Send MIDI to keyboard to trigger its sounds
5. **Recording** - Capture MIDI notes with timing
6. **Playback** - Loop all tracks based on longest track
7. **Backing Track** - Play existing tracks while recording new ones
8. **Session Management** - Multiple named sessions, persisted locally
9. **Export** - MIDI (.mid merged/separate) or Audio (.mp3 with piano sound)
10. **Dark Theme UI** - Modern dark interface with hamburger menu

## UI/UX Design Direction
- **Visual Style**: Dark Material 3 theme
- **Color Scheme**: Deep dark background (#121212), accent green for recording, amber for playback
- **Layout**: Single screen with 5×2 track grid, transport controls at bottom
- **Navigation**: Hamburger menu for settings/export/session management
- **Track Display**: Each track shows status (empty/recorded/playing), tap to select, long-press for options