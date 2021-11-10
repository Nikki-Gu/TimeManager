package com.example.timemanager.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.timemanager.repository.SheetRepository
import com.example.timemanager.repository.TaskRepository
import com.example.timemanager.repository.UserPreferencesRepository

class HomeViewModelFactory(
    private val taskRepository: TaskRepository,
    private val sheetRepository: SheetRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(taskRepository, sheetRepository, userPreferencesRepository) as T
    }
}