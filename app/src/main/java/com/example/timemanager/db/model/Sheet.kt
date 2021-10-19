package com.example.timemanager.db.model

data class Sheet(
    val id: Int = 0,
    val name: String,
    var description: String?,
    var sheetClass: String, //清单类别
    var rate: Double = 0.0, //已完成任务比例
    val tasks: List<Task?> = emptyList()
)
