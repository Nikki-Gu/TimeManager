package com.example.timemanager.ui.analysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.timemanager.db.model.Record
import com.example.timemanager.db.model.Sheet
import com.example.timemanager.db.model.Task
import com.example.timemanager.repository.RecordRepository
import com.example.timemanager.repository.SheetRepository
import com.example.timemanager.repository.TaskRepository
import com.example.timemanager.repository.UserPreferencesRepository
import com.example.timemanager.repository.mapper.RecordMapper.toDomain
import com.example.timemanager.repository.mapper.RecordMapper.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val recordRepository: RecordRepository
): ViewModel() {

    fun timesOfDate(date: Date): LiveData<Int> = recordRepository.getTimesByDate(date).asLiveData()

    fun timesTillNow(): LiveData<Int> = recordRepository.getTimesTillNow().asLiveData()

    fun durationOfDate(date: Date): LiveData<Long> = recordRepository.getDurationByDate(date).asLiveData()

    fun durationTillNow(): LiveData<Long> = recordRepository.getDurationTillNow().asLiveData()

    fun recordOfDate(date: Date): LiveData<Record?> = recordRepository.getRecordByDate(date).asLiveData()
}