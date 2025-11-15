package mohit.voice.twinmind.gemini

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mohit.voice.twinmind.viewmodel.notes.SpeechToTextManager
import javax.inject.Inject

class SpeechToTextManagerImpl @Inject constructor(
    private val apiKey: String
) : SpeechToTextManager {

    override suspend fun convertAudioToText(audioPath: String): String = withContext(Dispatchers.IO) {
        // Use a real STT library here, e.g., Vosk or Google STT
        val sttText = fakeAudioToText(audioPath)
        return@withContext sttText
    }

    private fun fakeAudioToText(audioPath: String): String {
        // placeholder for testing
        return "Transcribed text from $audioPath"
    }
}
