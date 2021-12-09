package com.example.timemanager.repository

import com.example.timemanager.db.dao.RecordDao
import com.example.timemanager.db.model.Record
import com.example.timemanager.repository.mapper.RecordMapper.toDomain
import com.example.timemanager.repository.mapper.RecordMapper.toEntity

import kotlinx.coroutines.flow.map
import java.util.*


val dateToday: Date = Calendar.getInstance().time

val dateStart: Date = Date(1990, 1, 1)

class RecordRepository(private val recordDao: RecordDao) {
    fun deleteRecord(id: Int) = recordDao.deleteRecord(id)

    fun getTimesByDate(date: Date) = recordDao.getTimesByDate(date)

    fun getTimesTillNow() = recordDao.getTimesBetweenDate(dateStart, dateToday)

    fun getDurationByDate(date: Date) = recordDao.getDurationByDate(date)

    fun getDurationTillNow() = recordDao.getDurationBetweenDate(dateStart, dateToday)

    fun getRecordByDate(date: Date) = recordDao.getRecordByDate(date).map { it.toDomain() }

    fun getRecordById(id: Int) = recordDao.getRecordById(id).map { it.toDomain() }

    fun getAllRecord() = recordDao.getAllRecord().map { it.toDomain() }

    suspend fun insertRecord(record: Record) = record.toEntity()?.let { recordDao.insertRecord(it) }

}
