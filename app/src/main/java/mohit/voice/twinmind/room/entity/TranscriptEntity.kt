package mohit.voice.twinmind.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transcripts")
data class TranscriptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val meetingId: Long,
    val transcriptText: String,
    val createdAt: Long = System.currentTimeMillis()
)
