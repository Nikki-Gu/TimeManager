package com.example.timemanager.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.timemanager.db.model.Record
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.db.model.Task
import com.example.timemanager.db.model.createTask
import com.example.timemanager.db.model.createUpdateTask
import com.example.timemanager.repository.RecordRepository
import com.example.timemanager.repository.SheetRepository
import com.example.timemanager.repository.TaskRepository
import com.example.timemanager.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val sheetRepository: SheetRepository,
    private val recordRepository: RecordRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val sheets: LiveData<List<Sheet?>> = sheetRepository.getSheets().asLiveData()

    val sheetSelectedId: LiveData<Int> = userPreferencesRepository.sheetSelected().asLiveData()

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    // 如果没有flatMapLatest则会有延迟,因为是flow？
    val sheetSelected: LiveData<Sheet?> =
        userPreferencesRepository.sheetSelected().flatMapLatest { sheetId ->
            sheetRepository.getSheet(sheetId)
        }.asLiveData()

    fun getTask(id: Int): LiveData<Task?> = taskRepository.getTask(id).asLiveData()

    fun deleteTask(id: Int) = viewModelScope.launch {
        taskRepository.deleteTask(id)
    }

    fun insertTask(name: String, description: String? = null, rank: Int? = null) = liveData {
        emit(insertTaskInSelectedSheet(name, description, rank))
    }

    private suspend fun insertTaskInSelectedSheet(
        name: String,
        description: String? = null,
        rank: Int? = null
    ): Long? =
        userPreferencesRepository.sheetSelected().firstOrNull()?.let {
            taskRepository.insertTask(
                createTask(name, it, description, rank)
            )
        }

    fun setTaskDone(id: Int) = viewModelScope.launch {
        taskRepository.setTaskDone(id)
    }

    fun setTaskDoing(id: Int) = viewModelScope.launch {
        taskRepository.setTaskDoing(id)
    }

    fun updateTask(taskId: Int, name: String, description: String? = null, rank: Int? = null) =
        viewModelScope.launch {
            userPreferencesRepository.sheetSelected().firstOrNull()?.let {
                taskRepository.updateTask(
                    createUpdateTask(taskId, name, it, description, rank)
                )
            }
        }

    fun getSheet(id: Int): LiveData<Sheet?> = sheetRepository.getSheet(id).asLiveData()

    fun setSheetSelectedId(id: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setSheetSelected(id)
        }
    }

    /**
     * 删除指定id的清单，如果删除清单为目前选中的清单，设置之后选中的清单为列表中第一个清单
     */
    fun deleteSheet(id: Int) = viewModelScope.launch {
        sheetRepository.deleteSheet(id)
        if (id == sheetSelectedId.value) {
            val projects = sheetRepository.getSheets().firstOrNull()
            projects?.firstOrNull()?.let { project ->
                setSheetSelectedId(project.id)
            }
        }
    }

    fun insertSheet(sheet: Sheet) = liveData {
        emit(sheetRepository.insert(sheet))
    }

    fun updateSheet(sheet: Sheet) = viewModelScope.launch {
        sheetRepository.updateSheet(sheet)
    }

    // 正在编辑的taskId
    private var editTaskId = MutableLiveData<Int>()
    fun getEditTaskId() = editTaskId.value
    fun setEditTaskId(value: Int) {
        editTaskId.value = value
    }

    // 正在编辑的sheetId
    private var editSheetId = MutableLiveData<Int>()
    fun getEditSheetId() = editSheetId.value
    fun setEditSheetId(value: Int) {
        editSheetId.value = value
    }

    //正在计时的taskId,taskName
    private var timingTaskId = MutableLiveData<Int>()
    fun getTimingTaskId() = timingTaskId.value
    fun setTimingTaskId(value: Int) {
        timingTaskId.value = value
    }
    private var timingTaskName = MutableLiveData<String>()
    fun getTimingTaskName() = timingTaskName.value
    fun setTimingTaskName(value: String) {
        timingTaskName.value = value
    }

    // 添加和编辑页面复用
    private var isEdit = MutableLiveData(false)
    fun isEdit() = isEdit.value ?: false
    fun setEdit(value: Boolean) {
        isEdit.value = value
    }

    fun deleteRecord(id: Int) = viewModelScope.launch {
        recordRepository.deleteRecord(id)
    }

    fun getRecordById(id: Int): LiveData<List<Record?>> = recordRepository.getRecordById(id).asLiveData()

    val allRecord: LiveData<List<Record?>> = recordRepository.getAllRecord().asLiveData()

    fun insertRecord(record: Record) = liveData {
        emit(recordRepository.insertRecord(record))
    }

}