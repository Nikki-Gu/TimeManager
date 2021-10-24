package com.example.timemanager.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.timemanager.db.entity.SheetEntity
import com.example.timemanager.db.entity.SheetTasksRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface SheetDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSheet(sheet: SheetEntity): Long

    @Transaction
    @Query("SELECT * FROM Sheet WHERE id = :id")
    fun getSheet(id: Int): SheetTasksRelation

    @Query("SELECT * FROM Sheet WHERE name = :name")
    fun getSheetByName(name: String): SheetTasksRelation

    @Query("SELECT * FROM Sheet ORDER BY id ASC")
    fun getSheets(): List<SheetTasksRelation>

    @Query("DELETE FROM Sheet WHERE id = :id")
    fun deleteSheet(id: Int): Int

    @Update
    suspend fun updateSheet(sheet: SheetEntity)
}