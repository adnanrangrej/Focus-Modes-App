package com.example.focusmodes.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.focusmodes.data.model.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Query("""
        SELECT * FROM sessions 
        WHERE datetime(startTime/1000, 'unixepoch', 'localtime') >= datetime('now', '-7 days', 'localtime')
        AND completed = 1
        ORDER BY startTime DESC
    """)
    fun getRecentSessions(): Flow<List<Session>>

    @Query("""
        SELECT COUNT(*) FROM sessions 
        WHERE date(startTime/1000, 'unixepoch', 'localtime') = date('now', 'localtime')
        AND completed = 1
    """)
    fun getTodayCompletedSessions(): Flow<Int>

    @Query("""
        SELECT COALESCE(SUM(duration), 0) FROM sessions 
        WHERE datetime(startTime/1000, 'unixepoch', 'localtime') >= datetime('now', '-7 days', 'localtime')
        AND completed = 1
    """)
    fun getWeeklyFocusTime(): Flow<Int>

    @Query("""
        SELECT COUNT(DISTINCT date(startTime/1000, 'unixepoch', 'localtime')) 
        FROM sessions 
        WHERE completed = 1 
        AND datetime(startTime/1000, 'unixepoch', 'localtime') >= datetime('now', '-30 days', 'localtime')
    """)
    fun getStreakDays(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session)

    @Update
    suspend fun updateSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

    @Query("SELECT * FROM sessions WHERE completed = 1")
    fun getCompletedSessions(): Flow<List<Session>>
} 