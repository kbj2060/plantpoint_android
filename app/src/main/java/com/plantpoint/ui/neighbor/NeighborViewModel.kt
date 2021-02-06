package com.example.plantpoint.ui.neighbor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NeighborViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Neighbor Fragment"
    }
    val text: LiveData<String> = _text
}