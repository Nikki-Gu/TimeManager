package com.example.timemanager.repository.mapper

import com.example.timemanager.db.entity.RecordEntity
import com.example.timemanager.db.model.Record



object RecordMapper {
    fun RecordEntity?.toDomain(): Record? = this?.let{
        Record(
            id = it.id,
            taskid = it.taskid,
            date = it.date,
            isInterrupt = it.isInterrupt,
            duration = it.duration
        )
    }

    fun Record?.toEntity(): RecordEntity? = this?.let{
        RecordEntity(
            id = it.id,
            taskid = it.taskid,
            date = it.date,
            isInterrupt = it.isInterrupt,
            duration = it.duration
        )
    }
}