package com.plantpoint.ui.nation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NationViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "온라인 마켓은 준비 중입니다.."
    }
    val text: LiveData<String> = _text
}