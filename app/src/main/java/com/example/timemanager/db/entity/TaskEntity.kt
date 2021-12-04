package com.example.timemanager.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.example.timemanager.db.model.Tag
import com.example.timemanager.db.model.TaskState
import java.util.*

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
    var startDate: Date?,//时间类型暂时用long存储
    var endDate: Date?,
    var rank: Int?,//等级，重要紧急
    var repeatTimes: Int = 1,//重复次数
    var frequency: String?,//任务频率
    var tag: Tag?
)