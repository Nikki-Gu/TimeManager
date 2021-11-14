package com.example.timemanager.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.timemanager.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("DELETE FROM user WHERE id = :id")
    suspend fun deleteUser(id: Int)

    @Query("SELECT * FROM user WHERE id = :id")
    fun getUser(id: Int): Flow<UserEntity>

    @Query("SELECT * FROM user ORDER BY id ASC")
    fun getUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(task: UserEntity): Long

    @Query("UPDATE user SET timesFocus = :times WHERE id = :id")
    suspend fun setUserTimes(id: Int, times: Int)

    @Query("UPDATE user SET totalDuration = :duration WHERE id = :id")
    suspend fun setUserDuration(id: Int, duration: Int)
}