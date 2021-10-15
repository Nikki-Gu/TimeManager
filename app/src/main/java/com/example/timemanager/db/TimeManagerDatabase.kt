package com.example.timemanager.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.timemanager.db.dao.SheetDao
import com.example.timemanager.db.dao.TaskDao
import com.example.timemanager.db.dao.TaskSheetViewDao
import com.example.timemanager.db.entity.SheetEntity
import com.example.timemanager.db.entity.TaskEntity
import com.example.timemanager.db.view.TaskSheetView

@Database(
    entities = [SheetEntity::class, TaskEntity::class],
    views = [TaskSheetView::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TimeManagerDatabase : RoomDatabase() {

    abstract fun sheetDao(): SheetDao

    abstract fun taskDao(): TaskDao

    abstract fun taskSheetViewDao(): TaskSheetViewDao

    companion object {
        @Volatile
        private var INSTANCE: TimeManagerDatabase?= null
        fun getInstance(context: Context): TimeManagerDatabase =
            INSTANCE?: synchronized(this){
                INSTANCE?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, TimeManagerDatabase::class.java, "TimeManager.db")
                .allowMainThreadQueries()
                .build()
    }
}

// https://zhuanlan.zhihu.com/p/377761286 数据库升级
// val MIGRATION_1_2 = object : Migration(1, 2) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//    }
// }
