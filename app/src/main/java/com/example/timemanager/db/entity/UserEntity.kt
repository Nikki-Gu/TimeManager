package com.example.timemanager.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.timemanager.db.Converters
import java.util.*

@TypeConverters(Converters::class)
@Entity(tableName = "User")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0, //user的id
    var name: String, //用户昵称
    var date: Date?, //日期
    var timesFocus: Int = 0, //次数
    var totalDuration: Long? //时长，但是显示问题仍未解决
) {

    override fun toString(): String {
        return name
    }
}
