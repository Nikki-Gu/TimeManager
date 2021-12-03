package com.example.timemanager.db.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import java.util.*

data class Record (
    val id: Int,
    val taskid: Int,
    var duration: Long?,
    var isInterrupt: Boolean,
    var date: Date?
)

fun createRecord(id: Int, taskid: Int, duration: Long?, isInterrupt: Boolean, date: Date? = null) =
    Record(
        id = id,
        taskid = taskid,
        duration = duration,
        isInterrupt = isInterrupt,
        date = date
    )