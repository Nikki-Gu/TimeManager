package com.example.timemanager.db.dao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.timemanager.db.entity.RecordEntity
import com.example.timemanager.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface RecordDao {
    @Query("SELECT * FROM user WHERE id = :id")
    fun getRecordById(id: Int): Flow<RecordEntity>

    @Query("SELECT * FROM user")
    fun getAllRecord(): Flow<RecordEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecord(record: RecordEntity): Long
}