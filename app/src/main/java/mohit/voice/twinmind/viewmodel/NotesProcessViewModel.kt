package mohit.voice.twinmind.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mohit.voice.twinmind.room.dao.MeetingDao
import mohit.voice.twinmind.room.dao.SummaryDao
import mohit.voice.twinmind.room.dao.TranscriptDao
import mohit.voice.twinmind.room.entity.MeetingEntity
import mohit.voice.twinmind.room.entity.SummaryEntity
import mohit.voice.twinmind.room.entity.TranscriptEntity
import mohit.voice.twinmind.viewmodel.notes.GeminiApi
import mohit.voice.twinmind.viewmodel.notes.SpeechToTextManager
import javax.inject.Inject

@HiltViewModel
class NotesProcessViewModel @Inject constructor(
    val meetingDao: MeetingDao,
    val transcriptDao: TranscriptDao,
    val summaryDao: SummaryDao,
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
    private val _currentRecordingId = MutableStateFlow(0L)
    val currentRecordingId: StateFlow<Long> = _currentRecordingId.asStateFlow()

    fun updateCurrentRecordingId(id: Long) {
        _currentRecordingId.value = id
    }

    fun getTranscriptFlow(meetingId: Long) = transcriptDao.getTranscriptFlow(meetingId)
    fun getSummaryFlow(meetingId: Long) = summaryDao.getSummaryFlow(meetingId)

    private val _state = MutableStateFlow<NotesState>(NotesState.Idle)
    val state = _state.asStateFlow()

    private val _meetings = MutableStateFlow<List<MeetingEntity>>(emptyList())
    val meetings = _meetings.asStateFlow()

    fun getAllMeetings() {
        viewModelScope.launch {
            _meetings.value = meetingDao.getAllMeetings()
        }
    }

    /** STEP 1 — Save Recorded Audio to Room */
    fun saveRecording(
        meetingId: Long,         // <-- Changed to Long
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

    private suspend fun convertAudioToText(meetingId: Long, audioPath: String) {   // <-- Long
        viewModelScope.launch {
            try {
                Log.d(TAG, "Converting audio to text for: $audioPath")
                _state.value = NotesState.ConvertingToText

                val text = speechToTextManager.convertAudioToText(audioPath)
                Log.d(TAG, "Converted text: $text")

                val transcript = TranscriptEntity(
                    meetingId = meetingId,        // <-- Long
                    transcriptText = text,
                    createdAt = System.currentTimeMillis()
                )
                transcriptDao.insertTranscript(transcript)
                Log.d(TAG, "Transcript saved in DB for meetingId: $meetingId")
                Log.d("ROOM_DEBUG", "Saved transcript: ${transcript?.transcriptText}")

                generateSummary(meetingId, text)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting audio to text", e)
                _state.value = NotesState.Error(e.message ?: "STT error")
            }
        }
    }

    /** STEP 3 — Send Transcript to Gemini API → Summary & Save */
    private suspend fun generateSummary(meetingId: Long, transcriptText: String) {  // <-- Long
        viewModelScope.launch {
            try {
                Log.d(TAG, "Generating summary for meetingId: $meetingId")
                _state.value = NotesState.GeneratingSummary

                val summaryText = geminiApi.generateSummary(transcriptText)
                Log.d(TAG, "Summary received: $summaryText")

                val summary = SummaryEntity(
                    meetingId = meetingId,       // <-- Long
                    summaryText = summaryText,
                    createdAt = System.currentTimeMillis()
                )
                summaryDao.insertSummary(summary)
                Log.d(TAG, "Summary saved in DB for meetingId: $meetingId")
                Log.d("ROOM_DEBUG", "Saved summary: ${summary?.summaryText}")

                _state.value = NotesState.Completed
                Log.d(TAG, "Processing completed for meetingId: $meetingId")
            } catch (e: Exception) {
                Log.e(TAG, "Error generating summary", e)
                _state.value = NotesState.Error(e.message ?: "Summary error")
            }
        }
    }
    fun processRecording(meetingId: Long, audioPath: String) {
        viewModelScope.launch {
            try {
                _state.value = NotesState.ConvertingToText

                // Convert audio to text (direct suspend call)
                val transcriptText = speechToTextManager.convertAudioToText(audioPath)
                val transcript = TranscriptEntity(
                    meetingId = meetingId,
                    transcriptText = transcriptText,
                    createdAt = System.currentTimeMillis()
                )
                transcriptDao.insertTranscript(transcript)
                Log.d(TAG, "Transcript saved for meetingId=$meetingId")

                _state.value = NotesState.GeneratingSummary

                // Generate summary (direct suspend call)
                val summaryText = geminiApi.generateSummary(transcriptText)
                val summary = SummaryEntity(
                    meetingId = meetingId,
                    summaryText = summaryText,
                    createdAt = System.currentTimeMillis()
                )
                summaryDao.insertSummary(summary)
                Log.d(TAG, "Summary saved for meetingId=$meetingId")

                _state.value = NotesState.Completed
                Log.d(TAG, "Processing completed for meetingId=$meetingId")

            } catch (e: Exception) {
                Log.e(TAG, "Error processing recording", e)
                _state.value = NotesState.Error(e.message ?: "Error processing recording")
            }
        }
    }

}
