package com.plantpoint.ui.neighborDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NeighborDetailViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Neighbor Fragment"
    }
    val text: LiveData<String> = _text
}