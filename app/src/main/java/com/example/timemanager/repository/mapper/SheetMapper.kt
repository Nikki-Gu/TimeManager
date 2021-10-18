package com.example.timemanager.repository.mapper

import com.example.timemanager.db.entity.SheetEntity
import com.example.timemanager.db.entity.SheetTasksRelation
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.repository.mapper.TaskMapper.toDomain

object SheetMapper {
    fun SheetEntity?.toDomain(): Sheet? = this?.let {
        Sheet(
            id = it.id,
            name = it.name,
            description = it.description,
            sheetClass = it.sheetClass,
            rate = it.rate
        )
    }

    fun SheetTasksRelation?.toDomain(): Sheet? = this?.let {
        Sheet(
            id = it.sheet.id,
            name = it.sheet.name,
            description = it.sheet.description,
            sheetClass = it.sheet.sheetClass,
            rate = it.sheet.rate,
            tasks = it.tasks.map { task -> task.toDomain() }.sortedBy { task -> task?.state }
        )
    }

    fun Sheet?.toEntity(): SheetEntity? = this?.let {
        SheetEntity(
            id = it.id,
            name = it.name,
            description = it.description,
            sheetClass = it.sheetClass,
            rate = it.rate
        )
    }
}
