package com.example.timemanager.di

import android.app.Application
import androidx.room.Room
import com.example.timemanager.db.TimeManagerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideTimeManagerDatabase(application: Application) =
        Room.databaseBuilder(application, TimeManagerDatabase::class.java, "TimeManager.db")
            .build()

    @Provides
    fun provideSheetDao(timeManagerDatabase: TimeManagerDatabase) = timeManagerDatabase.sheetDao()

    @Provides
    fun provideTaskDao(timeManagerDatabase: TimeManagerDatabase) = timeManagerDatabase.taskDao()

    @Provides
    fun provideRecordDao(timeManagerDatabase: TimeManagerDatabase) = timeManagerDatabase.recordDao()
}
