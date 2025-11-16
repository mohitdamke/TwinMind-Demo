TwinMind – AI-Powered Voice Notes App

<img width="240" height="240" alt="TwinMind" src="https://github.com/user-attachments/assets/ce06cce1-818a-40f7-b3ef-73ae272a3617" />
https://github.com/user-attachments/assets/e24327b7-eaaa-4d97-8a75-e6de0e52df07

TwinMind is an Android application that records your meetings or voice notes and automatically generates transcriptions and summaries using AI. It helps you focus on discussions while keeping organized notes for later review.

Features

Voice Recording

Start/Stop recording with a single button.

Recording time display.

Save recordings automatically in the app’s storage.

Background Processing

AI-powered speech-to-text conversion.

Automatic summary generation using Gemini AI API.

Works even if you navigate away from the recording screen.

Notes & Transcript

View generated transcript and summary in separate tabs.

Copy or share summaries directly from the app.

Supports multiple meetings with metadata like title and date.

Meeting Management

Store recordings and their summaries in a local Room database.

Fetch and display previous recordings and their content.

User-Friendly UI

Material3 design with responsive tabs.

Progress indicators while processing audio.

Prevent accidental navigation during recording or processing.

Screenshots

(Add some screenshots here if available)

Installation

Clone the repository:

git clone https://github.com/yourusername/twinmind.git


Open the project in Android Studio.

Ensure Kotlin, Jetpack Compose, and Hilt are properly set up.

Add your Gemini API key in strings.xml or your preferred config.

Build and run on an Android device (min SDK 31 required).

Usage

Start a new recording by tapping the Start Recording button.

Stop recording to automatically save the audio.

The app will convert audio to text and generate a summary in the background.

Navigate to Notes or Transcript tabs to view results.

Use Copy or Share buttons to export your notes.

⚠ Users cannot navigate back while recording or processing to avoid data loss.

Dependencies

Jetpack Compose – UI framework

Room Database – Local storage for meetings, transcripts, summaries

Hilt – Dependency injection

Gemini API – AI-powered summary generation

MediaRecorder – Audio recording

Project Structure
mohit.voice.twinmind/
├── presentation/       # Compose UI screens
│   ├── record/         # RecordScreen and recording UI
│   └── detail/         # NotesScreen, TranscriptScreen, QuestionsScreen
├── room/               # Room database, DAO, and entities
├── ui/                 # Theme and styling
├── viewmodel/          # NotesProcessViewModel, background processing

Future Improvements

Multiple file export (PDF, DOCX) for summaries.

Offline speech-to-text processing.

Customizable AI summary settings.

Dark mode support.

Contributing

Contributions are welcome! Feel free to submit issues or pull requests for bug fixes and new features.

License

This project is licensed under the MIT License. See the LICENSE file for details.
