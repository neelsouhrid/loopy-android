# Loopy - Advanced MIDI Looper for Android

Loopy is a professional-grade multitrack MIDI looper and audio playback workstation designed specifically for Android. It bridges direct hardware USB connections from external MIDI instruments (like Casio, Yamaha keyboards) to offer a lightning-fast, highly accurate looping experience with robust track management, non-destructive editing, and zero-latency internal piano processing.

---

## 🚀 Core Features

### Multitrack MIDI Grid
- **10 Independent Loop Tracks**: Record MIDI data independently across 10 dynamically scaling track blocks.
- **Microsecond Synchronization Engine**: Completely fluid, unquantized loop durations. Once Track 1 is recorded, its exact elapsed time locks in the master session boundary. Overdubbed tracks sync perfectly to this underlying global duration without drift or stutter.
- **Multitrack Tone Isolation**: Tracks individually record and isolate your specific `Program Change` (e.g. `0xC0`) MIDI keys. Playing guitar on Track 1 and Piano on Track 2 actually isolates and splits those channels via mirroring back out to your synthesizer dynamically.

### Android SoundPool Native Synthesis 
- **Zero-Latency Audio**: Unlike basic software synthesizers or delayed MidiPlayer approaches, Loopy uses Android's raw `SoundPool` hardware mapping connected to 88 genuine acoustic grand piano `.mp3` samples locally bundled within the app layout.
- When you use your hardware keyboard, Loopy concurrently triggers the native acoustic files directly out of the phone speaker without lag, meaning **live feedback without requiring connection back to a speaker.**

### Advanced Transport Controls
- **10-Step Non-Destructive Undo & Redo History**: Confidently create complex `OVERDUB` layers! The app silently tracks and captures exact session block snapshots into a sliding RAM ring-buffer, allowing up to **10 layers** of un-doable safety.
- **Live Resume & Reset Contexts**: Dedicated playhead pausing enables precise transport freezing. Entering any fresh record mode forces strict timeline drops directly back to `0.0s`.

### Live User Interface
- **Micro-Playhead Canvas Visualization**: Every active track block dynamically renders a swept visual arc directly onto the button conveying the exact fractional position `loopPosition / scaledDuration` of the current timeline block.
- **Persistent Note Monitoring**: Pressing keys on the keyboard physically reflects onto the top control bar UI natively across all app states (e.g., `A#4 • C#4 • F4`).

---

## 🎨 Interactive Track Layout Options

- **Two-Finger Hardware Muting**
   Easily toggle tracks out of the live playback loop by simply simultaneously tapping exactly **two fingers** on a track boundary! The track immediately mutes, the icon grays out, and a **crossed volume icon** displays. Tap again with two fingers to resume audio dynamically.
- **Track Metadata Editors**
   Performing a **long press** on an active track opens the specialized Track Configuration Overlay, empowering you to adjust:
   - **Rename**: Assign a physical name to the block (e.g., "Bassline 2")
   - **Emoji Marker**: Choose a clear visual identifier Emoji placed on the top left block overlay.
   - **Custom Color Swatches**: Swap the boring dynamic loop colors to your distinct coded Palette (ranging from Blood Red, Royal Blue, Green, Yellow, Purple to Orange).
   - **Destructive Deletion**: Accessible instantly via a clear `Delete` override option next to `Save`.

---

## ⚙️ Operating Modes

Loopy uses three exclusive operational modes controlled via the top UI chips:

1. **RECORD Mode**
   Your standard initial loop-builder state. Wait for the exact moment you're ready; tap a target `Empty Track` block to start capturing the global master duration limit. 
2. **OVERDUB Mode**
   A specialized transparent recording pipeline that merges incoming live MIDI data against the *existing* sequence. Overdub bypasses channel overrides and allows complex stacking using the internal Undo-Redo RAM arrays safely! 
3. **PLAY Mode**
   The locked listen-only master loop mode. No notes are written regardless of MIDI input, and it acts exclusively for continuous mix testing, editing colors/emojis, and muting channels silently.

---

## 🔄 Session Management Architecture 
All your creations persist flawlessly. The app utilizes `Preferences DataStore` mapped JSON objects to save/export all raw multi-track grids safely. 
- You can create infinite multiple sessions. 
- You can set Session-Wide **Master Tempos**.
- Swap freely among "Idea 1" and "Chorus Loop" utilizing the `Sessions` top folder button without any buffer loss.
