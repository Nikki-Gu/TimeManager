package com.example.timemanager.ui.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.timemanager.repository.RecordRepository

class AnalysisViewModelFactory(private val recordRepository: RecordRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnalysisViewModel(recordRepository) as T
    }
}
