package com.example.timemanager.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.timemanager.db.model.Sheet
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    companion object {
        const val DEFAULT_SELECTED_SHEET_ID = 1
    }

    val sheetSelectedId: MutableLiveData<Int> = MutableLiveData()
    fun getSheetSelectedId(): Int {
        return sheetSelectedId.value ?: DEFAULT_SELECTED_SHEET_ID
    }

    fun setSheetSelectedId(id: Int) {
        sheetSelectedId.value = id
    }

    val sheetSelected: MutableLiveData<Sheet> = MutableLiveData()
    fun setSheetSelected(sheet: Sheet?) {
        sheetSelected.value = sheet
    }

    private var editTaskId = MutableLiveData<Int>()
    fun getEditTaskId() = editTaskId.value
    fun setEditTaskId(value: Int) {
        editTaskId.value = value
    }

    private var editSheetId = MutableLiveData<Int>()
    fun getEditSheetId() = editSheetId.value
    fun setEditSheetId(value: Int) {
        editSheetId.value = value
    }

    private var isEdit = MutableLiveData(false)
    fun isEdit() = isEdit.value ?: false
    fun setEdit(value: Boolean) {
        isEdit.value = value
    }
}