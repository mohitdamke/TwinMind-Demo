package mohit.voice.twinmind.viewmodel.notes

interface SpeechToTextManager {
    suspend fun convertAudioToText(audioPath: String): String
}
