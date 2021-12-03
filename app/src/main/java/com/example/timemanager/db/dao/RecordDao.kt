package com.example.timemanager.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.timemanager.db.entity.RecordEntity
import com.example.timemanager.db.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface RecordDao {
    @Query("DELETE FROM record WHERE id = :id")
    fun deleteRecord(id: Int)

    @Query("SELECT COUNT(*) FROM record WHERE date = :date")
    fun getTimesByDate(date: Date): Int

    @Query("SELECT COUNT(*) FROM record WHERE date BETWEEN :date1 AND :date2")
    fun getTimesBetweenDate(date1: Date, date2: Date): Int

    @Query("SELECT SUM(duration) FROM record WHERE date = :date")
    fun getDurationByDate(date: Date): Long

    @Query("SELECT SUM(duration) FROM record WHERE date BETWEEN :date1 AND :date2")
    fun getDurationBetweenDate(date1: Date, date2: Date): Long

    @Query("SELECT * FROM record WHERE id = :id")
    fun getRecordById(id: Int): Flow<RecordEntity>

    @Query("SELECT * FROM record")
    fun getAllRecord(): Flow<RecordEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRecord(record: RecordEntity): Long
}