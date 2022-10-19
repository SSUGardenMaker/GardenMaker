package com.ssu.gardenmaker.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.ssu.gardenmaker.category.CategoryListAdapter

class MainViewModel : ViewModel() {
    private val categoryLists = mutableListOf<String>()

    init {
        categoryLists.add("건강")
        categoryLists.add("학업")
    }

    fun showCategory() : MutableList<String> {
        return categoryLists
    }

    fun addCategory(name : String, adapter : CategoryListAdapter) {
        categoryLists.add(name)
        adapter.notifyDataSetChanged()
    }

    fun editCategory(old_name : String, new_name : String) {

    }
}