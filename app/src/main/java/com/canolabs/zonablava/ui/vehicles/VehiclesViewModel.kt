package com.canolabs.zonablava.ui.vehicles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VehiclesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is vehicles Fragment"
    }
    val text: LiveData<String> = _text
}