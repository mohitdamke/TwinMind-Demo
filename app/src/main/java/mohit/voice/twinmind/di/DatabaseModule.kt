package mohit.voice.twinmind.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mohit.voice.twinmind.room.AppDatabase
import mohit.voice.twinmind.room.dao.MeetingDao
import mohit.voice.twinmind.room.dao.SummaryDao
import mohit.voice.twinmind.room.dao.TranscriptDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "voice_notes_db"
        ).build()

    @Provides
    @Singleton
    fun provideMeetingDao(db: AppDatabase): MeetingDao = db.meetingDao()

    @Provides
    @Singleton
    fun provideTranscriptDao(db: AppDatabase): TranscriptDao = db.transcriptDao()

    @Provides
    @Singleton
    fun provideSummaryDao(db: AppDatabase): SummaryDao = db.summaryDao()
}
