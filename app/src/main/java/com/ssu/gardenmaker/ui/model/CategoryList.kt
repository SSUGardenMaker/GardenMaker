package com.ssu.gardenmaker.ui.model

import android.content.Context

class CategoryList() {
    companion object {
        @Volatile
        private var instance : CategoryList? = null

        @JvmStatic
        fun getInstance(context : Context) : CategoryList = instance ?: synchronized(this) {
            instance ?: CategoryList().also {
                instance = it
            }
        }
    }

    interface CategoryListener {
        fun onChanged()
    }

    private var categoryListener : CategoryListener? = null
    private var categoryList: ArrayList<String> = ArrayList()

    init {
        categoryList.add("건강")
        categoryList.add("학업")
    }

    fun setCategoryListener(categoryListener: CategoryListener) {
        this.categoryListener = categoryListener
    }

    private fun notifyChange() {
        categoryListener?.onChanged()
    }
}