package com.example.timemanager.repository

import com.example.timemanager.db.dao.TaskDao
import com.example.timemanager.db.model.Task
import com.example.timemanager.repository.mapper.TaskMapper.toDomain
import com.example.timemanager.repository.mapper.TaskMapper.toEntity
import kotlinx.coroutines.flow.map

//class TaskRepository(private val taskDao: TaskDao) {
//
//    fun getTask(id: Int) = taskDao.getTask(id).map { it.toDomain() }
//
//    suspend fun deleteTask(id: Int) = taskDao.deleteTask(id)
//
//    suspend fun insert(task: Task): Long? = task.toEntity()?.let { taskDao.insertTask(it) }
//
//    suspend fun setTaskDone(id: Int) = taskDao.setTaskDone(id)
//
//    suspend fun setTaskDoing(id: Int) = taskDao.setTaskDoing(id)
//
//    suspend fun updateTask(task: Task) = task.toEntity()?.let { taskDao.updateTask(it) }
//}
