package com.example.timemanager.repository.mapper

import com.example.timemanager.db.entity.TaskEntity
import com.example.timemanager.db.model.Task

object TaskMapper {
    fun TaskEntity?.toDomain(): Task? = this?.let {
        Task(
            id = it.id,
            name = it.name,
            description = it.description,
            state = it.state,
            tag = it.tag,
            sheetId = it.sheetId,
            startDate = it.startDate,
            endDate = it.endDate,
            rank = it.rank,
            repeatTimes = it.repeatTimes,
            frequency = it.frequency
        )
    }

    fun Task?.toEntity(): TaskEntity? = this?.let {
        TaskEntity(
            id = it.id,
            name = it.name,
            description = it.description,
            state = it.state,
            tag = it.tag,
            sheetId = it.sheetId,
            startDate = it.startDate,
            endDate = it.endDate,
            rank = it.rank,
            repeatTimes = it.repeatTimes,
            frequency = it.frequency
        )
    }
}
