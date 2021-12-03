package com.example.timemanager.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.timemanager.db.Converters
import java.util.*

@TypeConverters(Converters::class)
@Entity(
    tableName = "Record",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "task_id") var taskid: Int,
    var duration: Long?,
    var isInterrupt: Boolean,
    var date: Date?

)
