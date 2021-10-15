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

package com.example.timemanager.db.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded
import com.example.timemanager.db.entity.TaskEntity

@DatabaseView(
    "SELECT " +
        " t.*," +
        " p.name as sheet_name" +
        " FROM Task t LEFT JOIN Sheet p ON t.sheet_id = p.id" +
        " ORDER BY sheet_id"
)
data class TaskSheetView(
    @Embedded val task: TaskEntity,
    @ColumnInfo(name = "sheet_name") val sheetName: String?
)
