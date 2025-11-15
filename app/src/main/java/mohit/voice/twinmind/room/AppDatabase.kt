package mohit.voice.twinmind.room

import androidx.room.Database
import androidx.room.RoomDatabase
import mohit.voice.twinmind.room.dao.MeetingDao
import mohit.voice.twinmind.room.dao.SummaryDao
import mohit.voice.twinmind.room.dao.TranscriptDao
import mohit.voice.twinmind.room.entity.MeetingEntity
import mohit.voice.twinmind.room.entity.SummaryEntity
import mohit.voice.twinmind.room.entity.TranscriptEntity

@Database(
    entities = [MeetingEntity::class, TranscriptEntity::class, SummaryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun meetingDao(): MeetingDao
    abstract fun transcriptDao(): TranscriptDao
    abstract fun summaryDao(): SummaryDao
}
