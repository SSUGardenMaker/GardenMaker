package com.ssu.gardenmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import retrofit2.Retrofit

class ApplicationClass : Application() {

    companion object {
        const val TAG: String = "GardenMaker"
        lateinit var retrofit: Retrofit
        lateinit var mSharedPreferences: SharedPreferences
        lateinit var categoryLists : MutableList<String>
    }

    override fun onCreate() {
        super.onCreate()

        mSharedPreferences = applicationContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        categoryLists = mutableListOf()
    }
}