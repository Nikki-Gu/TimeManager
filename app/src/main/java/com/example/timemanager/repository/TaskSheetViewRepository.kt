package com.example.timemanager.repository

import com.example.timemanager.db.dao.TaskSheetViewDao
import com.example.timemanager.db.view.TaskSheetView
import kotlinx.coroutines.flow.Flow

class TaskSheetViewRepository(private val taskSheetViewDao: TaskSheetViewDao) {

    fun getTaskSheetList(): Flow<List<TaskSheetView>> =
        taskSheetViewDao.getTaskSheetList()
}
