package mohit.voice.twinmind.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meetings")
data class MeetingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String? = null,                // user can rename later
    val audioPath: String,                   // audio file location
    val duration: String,                    // "02:14" readable format
    val isTranscribed: Boolean = false,
    val isSummarized: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
