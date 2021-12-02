package com.example.timemanager.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.timemanager.db.entity.RecordEntity
import com.example.timemanager.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * FROM record WHERE id = :id")
    fun getRecordById(id: Int): Flow<RecordEntity>

    @Query("SELECT * FROM record")
    fun getAllRecord(): Flow<RecordEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecord(record: RecordEntity): Long
}