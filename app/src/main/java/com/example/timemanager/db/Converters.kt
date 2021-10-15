package com.example.timemanager.db

import androidx.room.TypeConverter
import com.example.timemanager.db.model.Tag
import com.example.timemanager.db.model.TaskState

// NOTE: each conversion must have two functions to convert A to B and B to A
// i.e. Tag to String and String to Tag
class Converters {
    @TypeConverter
    fun toString(tag: Tag?): String? = tag?.name

    @TypeConverter
    fun toTag(name: String?): Tag = name?.let { enumValueOf<Tag>(it) } ?: Tag.GRAY

    @TypeConverter
    fun toString(taskState: TaskState?): String? = taskState?.name

    @TypeConverter
    fun toTaskState(name: String?): TaskState =
        name?.let { enumValueOf<TaskState>(it) } ?: TaskState.DOING
}
