package com.example.timemanager.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.example.timemanager.db.model.Tag
import com.example.timemanager.db.model.TaskState

@Entity(
    tableName = "Task",
    foreignKeys = [
        ForeignKey(
            entity = SheetEntity::class,
            parentColumns = ["id"],
            childColumns = ["sheet_id"],
            onDelete = CASCADE,
            onUpdate = CASCADE
        )
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var description: String?,
    var state: TaskState,
    @ColumnInfo(name = "sheet_id") var sheetId: Int,
    var startDate: Long,//时间类型暂时用long存储
    var endDate: Long,
    var rank: String,//等级，重要紧急
    var repeatTimes: Int = 1,//重复次数
    var frequency: String = "默认值",//任务频率
    var tag: Tag?
)