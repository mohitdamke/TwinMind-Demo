package mohit.voice.twinmind.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mohit.voice.twinmind.room.entity.SummaryEntity

@Dao
interface SummaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: SummaryEntity)

    @Query("SELECT * FROM summaries WHERE meetingId = :meetingId")
    fun getSummaryFlow(meetingId: Long): Flow<SummaryEntity?>
}
