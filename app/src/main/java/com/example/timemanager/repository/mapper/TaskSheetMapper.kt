package com.example.timemanager.repository.mapper

import com.example.timemanager.db.model.TaskSheet
import com.example.timemanager.db.view.TaskSheetView

object TaskSheetMapper {

    fun TaskSheetView?.toDomain(): TaskSheet? = this?.let {
        TaskSheet(
            id = it.task.id,
            name = it.task.name,
            description = it.task.description,
            taskState = it.task.state,
            sheetId = it.task.sheetId,
            sheetName = it.sheetName,
            tag = it.task.tag
        )
    }
}
