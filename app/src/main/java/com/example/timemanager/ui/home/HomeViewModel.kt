package com.example.timemanager.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    companion object {
        const val DEFAULT_SELECTED_SHEET_ID = 1
    }

    val projectSelectedId: MutableLiveData<Int> = MutableLiveData(DEFAULT_SELECTED_SHEET_ID)
    fun getProjectSelectedId() : Int {
        return projectSelectedId.value ?: DEFAULT_SELECTED_SHEET_ID
    }
    fun setProjectSelectedId(id: Int) {
        projectSelectedId.value = id
    }

    private var taskId = MutableLiveData<Int>()
    fun getTaskId() = taskId.value
    fun setTaskId(value: Int) {
        taskId.value = value
    }

    private var isEdit = MutableLiveData<Boolean>(false)
    fun getIsEdit() = isEdit.value
    fun setIdEdit() {
        isEdit.value = true
    }
}