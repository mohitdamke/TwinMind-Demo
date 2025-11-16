package mohit.voice.twinmind.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "summaries")
data class SummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val meetingId: Long,
    val summaryText: String,
    val createdAt: Long = System.currentTimeMillis()
)

