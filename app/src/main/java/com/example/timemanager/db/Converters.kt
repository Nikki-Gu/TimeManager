/*
 * Copyright 2021 Sergio Belda Galbis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.timemanager.db

import androidx.room.TypeConverter
import com.example.timemanager.db.model.Tag
import com.example.timemanager.db.model.TaskState
import java.util.*


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

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return when (timestamp) {
            null -> null
            else -> Date(timestamp)
        }
    }

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }
}
