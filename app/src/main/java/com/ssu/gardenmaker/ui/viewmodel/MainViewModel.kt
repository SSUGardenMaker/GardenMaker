package com.ssu.gardenmaker.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ssu.gardenmaker.category.CategoryExpandableListAdapter

class MainViewModel : ViewModel() {
    private val categoryLists = mutableListOf<String>()

    init {
        categoryLists.add("건강")
        categoryLists.add("학업")
    }

    fun showCategory() : MutableList<String> {
        return categoryLists
    }

    fun addCategory(name : String, adapter : CategoryExpandableListAdapter) {
        categoryLists.add(name)
        adapter.notifyDataSetChanged()
    }
}