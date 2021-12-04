package com.example.timemanager.db.model

import java.util.*

data class Record (
    val id: Int,
    val taskId: Int,
    val taskName: String,
    var duration: Long?,
    var isInterrupt: Boolean,
    var date: Date?
)

fun createRecord(id: Int, taskId: Int, taskName: String, duration: Long?, isInterrupt: Boolean, date: Date? = null) =
    Record(
        id = id,
        taskId = taskId,
        taskName = taskName,
        duration = duration,
        isInterrupt = isInterrupt,
        date = date
    )