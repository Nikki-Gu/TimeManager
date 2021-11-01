package com.example.timemanager

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class App : Application() {

    private val appCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        const val PACKAGE = "com.example.timemanager"
    }

    override fun onCreate() {
        super.onCreate()
    }
}
