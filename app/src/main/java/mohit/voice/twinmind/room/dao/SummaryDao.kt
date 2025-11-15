package mohit.voice.twinmind.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import mohit.voice.twinmind.room.entity.SummaryEntity

@Dao
interface SummaryDao {
    @Insert
    suspend fun insertSummary(summary: SummaryEntity)

    @Query("SELECT * FROM summaries WHERE meetingId = :meetingId")
    suspend fun getSummary(meetingId: Int): SummaryEntity?
}
