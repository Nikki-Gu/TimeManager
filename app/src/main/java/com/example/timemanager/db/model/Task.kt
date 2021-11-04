package com.example.timemanager.db.model

data class Task(
    val id: Int = 0,
    val name: String,
    val description: String? = null,
    val state: TaskState,
    val sheetId: Int,
    var startDate: Long? = null,//时间类型暂时用long存储
    var endDate: Long? = null,
    var rank: Int? = null,//等级，重要紧急
    var repeatTimes: Int = 1,//重复次数
    var frequency: String? = null,//任务频率
    val tag: Tag? = null
)



fun createTask(name : String, sheetId : Int, description: String? = null, rank: Int? = null) =
    Task(
        name = name,
        state = TaskState.DOING,
        sheetId = sheetId,
        description = description,
        rank = rank
    )

fun createUpdateTask(id : Int, name : String, sheetId : Int, description: String? = null, rank: Int? = null) =
    Task(
        id = id,
        name = name,
        state = TaskState.DOING,
        sheetId = sheetId,
        description = description,
        rank = rank
    )