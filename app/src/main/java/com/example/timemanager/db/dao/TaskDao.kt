package com.example.timemanager.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.timemanager.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("DELETE FROM task WHERE id = :id")
    fun deleteTask(id: Int): Int

    @Query("DELETE FROM task")
    fun deleteAll()

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: Int): TaskEntity

    @Query("SELECT * FROM task ORDER BY id ASC")
    fun getTasks(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTask(task: TaskEntity): Long

    @Query("UPDATE task SET state = 'DOING' WHERE id = :id")
    suspend fun setTaskDoing(id: Int)

    @Query("UPDATE task SET state = 'DONE' WHERE id = :id")
    suspend fun setTaskDone(id: Int)

    @Update
    suspend fun updateTask(task: TaskEntity)
}