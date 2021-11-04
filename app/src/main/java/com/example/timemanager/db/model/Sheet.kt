package com.example.timemanager.db.model

data class Sheet(
    val id: Int = 0,
    val name: String,
    var description: String? = null,
    var sheetClass: String? = null, //清单类别
    var rate: Double = 0.0, //已完成任务比例 或许可以删除？
    val tasks: List<Task?> = emptyList()
)

fun createSheet(name: String, description: String? = null) =
    Sheet(
        name = name,
        description  = description
    )

fun createUpdateSheet(id : Int, name: String, description: String? = null) =
    Sheet(
        id = id,
        name = name,
        description  = description
    )