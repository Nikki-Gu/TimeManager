package com.example.timemanager.ui.pet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PetViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is pet Fragment"
    }
    val text: LiveData<String> = _text
}