package com.example.timemanager.repository

import androidx.room.TypeConverters
import com.example.timemanager.db.dao.RecordDao
import com.example.timemanager.db.model.Record
import com.example.timemanager.repository.mapper.RecordMapper.toDomain
import com.example.timemanager.repository.mapper.RecordMapper.toEntity
import com.example.timemanager.db.Converters
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.map
import java.util.*

val dateStart: Date = Date(1500000000000)




class RecordRepository(private val recordDao: RecordDao) {
    fun deleteRecord(id: Int) = recordDao.deleteRecord(id)

    fun getTimesByDate(date: Date) = recordDao.getTimesBetweenDate(Date(date.getYear(), date.getMonth(), date.getDate(), 0, 0), Date(date.getYear(), date.getMonth(), date.getDate(), 23, 59))

    fun getTimesTillNow() = recordDao.getTimesBetweenDate(dateStart, Calendar.getInstance().time)

    fun getDurationByDate(date: Date) = recordDao.getDurationBetweenDate(Date(date.getYear(), date.getMonth(), date.getDate(), 0, 0), Date(date.getYear(), date.getMonth(), date.getDate(), 23, 59))

    fun getDurationTillNow() = recordDao.getDurationBetweenDate(dateStart, Calendar.getInstance().time)

    fun getRecordByDate(date: Date): Flow<List<Record?>> = recordDao.getRecordBetweenDate(Date(date.getYear(), date.getMonth(), date.getDate(), 0, 0), Date(date.getYear(), date.getMonth(), date.getDate(), 23, 59)).map { it.toDomain() }

    fun getRecordById(id: Int) = recordDao.getRecordById(id).map { it.toDomain() }

    fun getAllRecord(): Flow<List<Record?>> = recordDao.getAllRecord().map { it.toDomain() }

    suspend fun insertRecord(record: Record) = record.toEntity()?.let { recordDao.insertRecord(it) }

}
