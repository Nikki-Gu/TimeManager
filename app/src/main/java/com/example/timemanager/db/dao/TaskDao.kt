package com.example.timemanager.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.timemanager.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM task WHERE startDate= :date and endDate= :date")
    fun getAllbyDate(date: Date)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun deleteTask(id: Int)

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: Int): Flow<TaskEntity>

    @Query("SELECT * FROM task ORDER BY id ASC")
    fun getTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: TaskEntity): Long

    @Query("UPDATE task SET state = 'DOING' WHERE id = :id")
    suspend fun setTaskDoing(id: Int)

    @Query("UPDATE task SET state = 'DONE' WHERE id = :id")
    suspend fun setTaskDone(id: Int)

    @Update
    suspend fun updateTask(task: TaskEntity)
}