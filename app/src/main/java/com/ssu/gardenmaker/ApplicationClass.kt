package com.ssu.gardenmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import com.ssu.gardenmaker.db.MyDBHelper
import retrofit2.Retrofit

class ApplicationClass : Application() {

    companion object {
        const val TAG: String = "GardenMaker"
        lateinit var db: SQLiteDatabase
        lateinit var dbHelper : MyDBHelper
        lateinit var retrofit: Retrofit
        lateinit var mSharedPreferences: SharedPreferences
        lateinit var categoryLists : MutableList<String>
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            dbHelper = MyDBHelper(applicationContext)
            db = dbHelper.readableDatabase
            db = dbHelper.writableDatabase
        }
        mSharedPreferences = applicationContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        categoryLists = mutableListOf()
    }
}