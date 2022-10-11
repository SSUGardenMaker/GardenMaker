package com.ssu.gardenmaker.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssu.gardenmaker.ui.model.CategoryList

class MainViewModel : ViewModel() {
    val liveData = MutableLiveData<List<String>>()
    private val lists = arrayListOf<String>()

    init {

    }
}