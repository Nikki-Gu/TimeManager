package com.example.timemanager.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SheetTasksRelation(
    @Embedded val sheet: SheetEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "sheet_id"
    )
    val tasks: List<TaskEntity>
)
