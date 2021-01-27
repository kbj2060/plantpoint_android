package com.example.plantpoint.ui.nation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Nation Fragment"
    }
    val text: LiveData<String> = _text
}