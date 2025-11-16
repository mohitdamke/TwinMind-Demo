package mohit.voice.twinmind.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import mohit.voice.twinmind.room.entity.MeetingEntity

@Dao
interface MeetingDao {

    // Insert audio record
    @Insert
    suspend fun insertMeeting(meeting: MeetingEntity): Long

    @Update
    suspend fun updateMeeting(meeting: MeetingEntity)

    @Query("SELECT * FROM meetings ORDER BY createdAt DESC")
    suspend fun getAllMeetings(): List<MeetingEntity>

    @Query("SELECT * FROM meetings WHERE id = :id")
    suspend fun getMeetingById(id: Long): MeetingEntity?

}
