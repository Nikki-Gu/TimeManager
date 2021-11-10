package com.example.timemanager.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Sheet")
data class SheetEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0, //清单id，自动生成不用传入
    var name: String, //清单名称
    var description: String?, //清单描述（后续功能描述）
    var sheetClass: String?, //清单类别（后续清单模版）
    var rate: Double = 0.0 //已完成任务比例
) {

    override fun toString(): String {
        return name
    }
}
