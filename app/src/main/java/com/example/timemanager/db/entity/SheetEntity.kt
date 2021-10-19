package com.example.timemanager.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Sheet")
data class SheetEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0, //清单id
    var name: String, //清单名称
    var description: String?,
    var sheetClass: String, //清单类别
    var rate: Double = 0.0 //已完成任务比例
) {

    override fun toString(): String {
        return name
    }
}
