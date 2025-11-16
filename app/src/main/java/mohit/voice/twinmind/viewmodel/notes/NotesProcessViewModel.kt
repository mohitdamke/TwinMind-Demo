package mohit.voice.twinmind.viewmodel.notes

import android.util.Log
import mohit.voice.twinmind.room.entity.TranscriptEntity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mohit.voice.twinmind.room.dao.MeetingDao
import mohit.voice.twinmind.room.dao.SummaryDao
import mohit.voice.twinmind.room.dao.TranscriptDao
import mohit.voice.twinmind.room.entity.SummaryEntity
import javax.inject.Inject

@HiltViewModel
class NotesProcessViewModel @Inject constructor(
    val meetingDao: MeetingDao,
    private val transcriptDao: TranscriptDao,
    private val summaryDao: SummaryDao,
    private val speechToTextManager: SpeechToTextManager,
    private val geminiApi: GeminiApi
) : ViewModel() {

    companion object {
        private const val TAG = "NotesProcessViewModel"
    }

    sealed class NotesState {
        object Idle : NotesState()
        object SavingAudio : NotesState()
        object ConvertingToText : NotesState()
        object GeneratingSummary : NotesState()
        object Completed : NotesState()
        data class Error(val message: String) : NotesState()
    }

    private val _state = MutableStateFlow<NotesState>(NotesState.Idle)
    val state = _state.asStateFlow()

    /** STEP 1 — Save Recorded Audio to Room */
    fun saveRecording(
        meetingId: Long,
        audioPath: String,
        duration: String,
        title: String
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Saving audio: $audioPath, duration: $duration, meetingId: $meetingId")
                _state.value = NotesState.SavingAudio

                convertAudioToText(meetingId, audioPath)

            } catch (e: Exception) {
                Log.e(TAG, "Error saving audio", e)
                _state.value = NotesState.Error(e.message ?: "Error saving audio")
            }
        }
    }

    private fun convertAudioToText(meetingId: Long, audioPath: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Converting audio to text for: $audioPath")
                _state.value = NotesState.ConvertingToText

                val text = speechToTextManager.convertAudioToText(audioPath)
                Log.d(TAG, "Converted text: $text")

                val transcript = TranscriptEntity(
                    meetingId = meetingId,
                    transcriptText = text,
                    createdAt = System.currentTimeMillis()
                )
                transcriptDao.insertTranscript(transcript)
                Log.d(TAG, "Transcript saved in DB for meetingId: $meetingId")

                generateSummary(meetingId, text)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting audio to text", e)
                _state.value = NotesState.Error(e.message ?: "STT error")
            }
        }
    }

    /** STEP 3 — Send Transcript to Gemini API → Summary & Save */
    private fun generateSummary(meetingId: Long, transcriptText: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Generating summary for meetingId: $meetingId")
                _state.value = NotesState.GeneratingSummary

                val summaryText = geminiApi.generateSummary(transcriptText)
                Log.d(TAG, "Summary received: $summaryText")

                val summary = SummaryEntity(
                    meetingId = meetingId,
                    summaryText = summaryText,
                    createdAt = System.currentTimeMillis()
                )
                summaryDao.insertSummary(summary)
                Log.d(TAG, "Summary saved in DB for meetingId: $meetingId")

                _state.value = NotesState.Completed
                Log.d(TAG, "Processing completed for meetingId: $meetingId")
            } catch (e: Exception) {
                Log.e(TAG, "Error generating summary", e)
                _state.value = NotesState.Error(e.message ?: "Summary error")
            }
        }
    }
}
