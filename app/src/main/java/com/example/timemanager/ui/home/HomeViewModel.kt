package com.example.timemanager.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

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