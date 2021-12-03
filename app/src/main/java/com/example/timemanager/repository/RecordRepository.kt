package com.example.timemanager.repository

import com.example.timemanager.db.dao.RecordDao
import com.example.timemanager.db.model.Record
import com.example.timemanager.repository.mapper.RecordMapper.toDomain
import com.example.timemanager.repository.mapper.RecordMapper.toEntity

import kotlinx.coroutines.flow.map
import java.util.*


val dateToday: Date = Calendar.getInstance().time;

val dateStart: Date = Date(1990, 1, 1)

class RecordRepository(private val recordDao: RecordDao) {
    fun deleteRecord(id : Int) = recordDao.deleteRecord(id)

    fun getTimesByDate(date: Date): Int = recordDao.getTimesByDate(date)

    fun getTimesTillNow(): Int = recordDao.getTimesBetweenDate(dateStart, dateToday)

    fun getDurationByDate(date: Date): Long = recordDao.getDurationByDate(date)

    fun getDurationTillNow(): Long = recordDao.getDurationBetweenDate(dateStart, dateToday)

    fun getRecordById(id: Int) = recordDao.getRecordById(id).map{ it.toDomain() }

    fun getAllRecord() = recordDao.getAllRecord().map{ it.toDomain() }

    fun insertRecord(record: Record) = record.toEntity()?.let { recordDao.insertRecord(it) }



}
