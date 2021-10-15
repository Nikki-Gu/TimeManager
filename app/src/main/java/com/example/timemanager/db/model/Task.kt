package com.example.timemanager.db.model

data class Task(
    val id: Int = 0,
    val name: String,
    val description: String?,
    val state: TaskState,
    val sheetId: Int,
    val tag: Tag?
)
