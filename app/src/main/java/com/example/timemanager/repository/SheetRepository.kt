package com.example.timemanager.repository

import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.repository.mapper.SheetMapper.toDomain
import com.example.timemanager.repository.mapper.SheetMapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SheetRepository(private val sheetDao: SheetDao) {

    fun getSheets(): List<Unit> = sheetDao.getSheets().map { it.toDomain() }

    fun getSheet(id: Int): Sheet? = sheetDao.getSheet(id).toDomain()

    suspend fun deleteSheet(id: Int) = sheetDao.deleteSheet(id)

    suspend fun insert(sheet: Sheet): Long? =
        sheet.toEntity()?.let { sheetDao.insertSheet(it) }

    suspend fun updateProject(sheet: Sheet) = sheet.toEntity()?.let {
        sheetDao.updateSheet(
            it
        )
    }
}