package com.example.timemanager.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.timemanager.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface UserDao {
    @Query("DELETE FROM user WHERE id = :id")
    suspend fun deleteUser(id: Int)

    @Query("SELECT * FROM user WHERE id = :id")
    fun getUser(id: Int): Flow<UserEntity>

    @Query("SELECT timesFocus FROM user WHERE id = :id and date = :date")
    fun getTimesByDate(id: Int, date: Date): Int

    @Query("SELECT timesFocus FROM user WHERE id = :id and (date between :date1 and :date2)")
    fun getTimesBetweenDate(id: Int, date1: Date, date2: Date): Int

    @Query("SELECT totalDuration FROM user WHERE id = :id and date = :date")
    fun getDurationByDate(id: Int, date: Date): Long

    @Query("SELECT totalDuration FROM user WHERE id = :id and (date between :date1 and :date2)")
    fun getDurationBetweenDate(id: Int, date1: Date, date2: Date): Long

    @Query("SELECT * FROM user ORDER BY id ASC")
    fun getUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(task: UserEntity): Long

    @Query("UPDATE user SET timesFocus = :times WHERE id = :id")
    suspend fun setUserTimes(id: Int, times: Int)

    @Query("UPDATE user SET totalDuration = :duration WHERE id = :id")
    suspend fun setUserDuration(id: Int, duration: Long)
}