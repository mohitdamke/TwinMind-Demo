package mohit.voice.twinmind.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mohit.voice.twinmind.room.entity.TranscriptEntity

@Dao
interface TranscriptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTranscript(transcript: TranscriptEntity)

    @Query("SELECT * FROM transcripts WHERE meetingId = :meetingId LIMIT 1")
    fun getTranscriptFlow(meetingId: Long): Flow<TranscriptEntity?>

}
