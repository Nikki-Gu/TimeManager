package com.example.timemanager.repository

import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.repository.mapper.SheetMapper.toDomain
import com.example.timemanager.repository.mapper.SheetMapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//class SheetRepository(private val sheetDao: SheetDao) {
//
//    fun getSheets(): Flow<List<Sheet?>> = sheetDao.getSheets().map { list ->
//        list.map { it.toDomain() }
//    }
//
//    fun getSheet(id: Int): Flow<Sheet?> = sheetDao.getSheet(id).map { it.toDomain() }
//
//    suspend fun deleteSheet(id: Int) = sheetDao.deleteSheet(id)
//
//    suspend fun insert(sheet: Sheet): Long? =
//        sheet.toEntity()?.let { sheetDao.insertSheet(it) }
//
//    suspend fun updateProject(sheet: Sheet) = sheet.toEntity()?.let {
//        sheetDao.updateSheet(
//            it
//        )
//    }
//}
