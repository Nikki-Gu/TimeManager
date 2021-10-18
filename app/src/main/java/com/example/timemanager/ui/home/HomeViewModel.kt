package com.example.timemanager.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.timemanager.db.dao.TaskDao
import kotlinx.coroutines.launch
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.repository.SheetRepository
import com.example.timemanager.repository.TaskRepository
import com.example.timemanager.repository.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class HomeViewModel constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val sheetRepository: SheetRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    //val sheetSelected: LiveData<Sheet?> = MutableLiveData<Sheet?>()
    @ExperimentalCoroutinesApi
    private val tmp: Flow<Sheet?> =
        userPreferencesRepository.projectSelected().flatMapLatest { sheetId ->
            sheetRepository.getSheet(sheetId)
        }
    @ExperimentalCoroutinesApi
    val sheetSelected: LiveData<Sheet?> = tmp.asLiveData()

    fun setTaskDone(id: Int) = viewModelScope.launch {
        taskRepository.setTaskDone(id)
    }

    fun setTaskDoing(id: Int) = viewModelScope.launch {
        taskRepository.setTaskDoing(id)
    }

    fun deleteTask(id: Int) = viewModelScope.launch {
        taskRepository.deleteTask(id)
    }
}