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
    fun insertSheet(sheet: SheetEntity): Long // suspend

    @Transaction
    @Query("SELECT * FROM Sheet WHERE id = :id")
    fun getSheet(id: Int): Flow<SheetTasksRelation>

    @Transaction
    @Query("SELECT * FROM Sheet WHERE id = :id")
    fun getSheetNotFlow(id: Int): SheetTasksRelation

    @Query("SELECT * FROM Sheet ORDER BY id ASC")
    fun getSheets(): Flow<List<SheetEntity>>

    @Query("DELETE FROM Sheet WHERE id = :id")
    suspend fun deleteSheet(id: Int)

    @Update
    suspend fun updateSheet(sheet: SheetEntity)
}