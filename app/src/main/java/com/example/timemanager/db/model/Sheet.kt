package com.example.timemanager.db.model

data class Sheet(
    val id: Int = 0,
    val name: String,
    val description: String,
    val tasks: List<Task?> = emptyList()
)
