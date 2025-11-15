package mohit.voice.twinmind.viewmodel.notes

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
import mohit.voice.twinmind.room.entity.MeetingEntity
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

    private var currentMeetingId: Long? = null

    /** STEP 1 — Save Recorded Audio to Room */
    fun saveRecording(
        meetingId: Long,
        audioPath: String,
        duration: String,
        title: String
    ) {
        viewModelScope.launch {
            try {
                _state.value = NotesState.SavingAudio

                // Optional: update MeetingEntity if needed
                // val meeting = MeetingEntity(id = meetingId, title = title, audioPath = audioPath, duration = duration, createdAt = System.currentTimeMillis())
                // meetingDao.updateMeeting(meeting) // only if you want to update

                convertAudioToText(meetingId, audioPath)

            } catch (e: Exception) {
                _state.value = NotesState.Error(e.message ?: "Error saving audio")
            }
        }
    }

    private fun convertAudioToText(meetingId: Long, audioPath: String) {
        viewModelScope.launch {
            try {
                _state.value = NotesState.ConvertingToText
                val text = speechToTextManager.convertAudioToText(audioPath)

                val transcript = TranscriptEntity(
                    meetingId = meetingId,   // important
                    transcriptText = text,
                    createdAt = System.currentTimeMillis()
                )
                transcriptDao.insertTranscript(transcript)

                generateSummary(meetingId, text)
            } catch (e: Exception) {
                _state.value = NotesState.Error(e.message ?: "STT error")
            }
        }
    }


    /** STEP 3 — Send Transcript to Gemini API → Summary & Save */
    private fun generateSummary(meetingId: Long, transcriptText: String) {
        viewModelScope.launch {
            try {
                _state.value = NotesState.GeneratingSummary
                val summaryText = geminiApi.generateSummary(transcriptText)

                val summary = SummaryEntity(
                    meetingId = meetingId,   // important
                    summaryText = summaryText,
                    createdAt = System.currentTimeMillis()
                )
                summaryDao.insertSummary(summary)

                _state.value = NotesState.Completed
            } catch (e: Exception) {
                _state.value = NotesState.Error(e.message ?: "Summary error")
            }
        }
    }


}
