package com.example.timemanager.db.model

data class Task(
    val id: Int = 0,
    val name: String,
    val description: String?,
    val state: TaskState,
    val sheetId: Int,
    var startDate: Long,//时间类型暂时用long存储
    var endDate: Long,
    var rank: String,//等级，重要紧急
    var repeatTimes: Int = 1,//重复次数
    var frequency: String = "默认值",//任务频率
    val tag: Tag?
)
