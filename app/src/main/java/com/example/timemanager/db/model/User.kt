package com.example.timemanager.db.model

data class User(
    var id: Int = 0, //user的id
    var name: String, //用户昵称
    var date: Long?, //日期
    var timesFocus: Int = 0, //次数
    var totalDuration: Long?, //时长，但是显示问题仍未解决
)

fun createUser(name: String, date: Long?,timesFocus: Int,totalDuration: Long?) =
    User(
        name = name,
        date = date,
        timesFocus = timesFocus,
        totalDuration = totalDuration
    )

fun createUpdateUser(id: Int, name: String, date: Long?,timesFocus: Int,totalDuration: Long?) =
    User(
        id = id,
        name = name,
        date = date,
        timesFocus = timesFocus,
        totalDuration = totalDuration
    )