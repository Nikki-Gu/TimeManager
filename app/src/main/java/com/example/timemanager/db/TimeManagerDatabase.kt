package com.example.timemanager.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.timemanager.db.dao.RecordDao
import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.dao.TaskDao
import com.example.timemanager.db.dao.UserDao
import com.example.timemanager.db.entity.RecordEntity
import com.example.timemanager.db.entity.SheetEntity
import com.example.timemanager.db.entity.TaskEntity
import com.example.timemanager.db.entity.UserEntity
import com.example.timemanager.db.view.TaskSheetView

@Database(
    entities = [SheetEntity::class, TaskEntity::class, UserEntity::class, RecordEntity::class],
    views = [TaskSheetView::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TimeManagerDatabase : RoomDatabase() {

    abstract fun sheetDao(): SheetDao

    abstract fun taskDao(): TaskDao

    abstract fun userDao(): UserDao

    abstract fun recordDao(): RecordDao

    companion object {
        @Volatile
        private var INSTANCE: TimeManagerDatabase? = null
        fun getInstance(context: Context): TimeManagerDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, TimeManagerDatabase::class.java, "TimeManager.db")
                .build()
    }
}
