package com.example.timemanager.db.model


data class TaskSheet(
    val id: Int = 0,
    val name: String?,
    val description: String?,
    val taskState: TaskState?,
    val sheetId: Int?,
    val sheetName: String?,
    val tag: Tag?
)
