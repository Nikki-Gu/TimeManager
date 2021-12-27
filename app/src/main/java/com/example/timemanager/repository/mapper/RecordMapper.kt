package com.example.timemanager.repository.mapper

import com.example.timemanager.db.entity.RecordEntity
import com.example.timemanager.db.model.Record

object RecordMapper {
    fun List<RecordEntity>.toDomain(): List<Record?>{
        var result = mutableListOf <Record?>()
        for(value in this){
            result.add(
                Record(
                id = value.id,
                taskId = value.taskid,
                taskName = value.taskName,
                date = value.date,
                isInterrupt = value.isInterrupt,
                duration = value.duration
                )
            )
        }
        return result
    }

    fun Record?.toEntity(): RecordEntity? = this?.let{
        RecordEntity(
            id = it.id,
            taskid = it.taskId,
            taskName = it.taskName,
            date = it.date,
            isInterrupt = it.isInterrupt,
            duration = it.duration
        )
    }
}