package mohit.voice.twinmind.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import mohit.voice.twinmind.room.entity.TranscriptEntity

@Dao
interface TranscriptDao {
    @Insert
    suspend fun insertTranscript(transcript: TranscriptEntity)

    @Query("SELECT * FROM transcripts WHERE meetingId = :meetingId")
    suspend fun getTranscript(meetingId: Int): TranscriptEntity?
}
