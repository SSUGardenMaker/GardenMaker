package com.ssu.gardenmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import com.ssu.gardenmaker.db.MyDBHelper
import com.ssu.gardenmaker.retrofit.RetrofitAPI
import com.ssu.gardenmaker.retrofit.RetrofitClient
import com.ssu.gardenmaker.retrofit.RetrofitManager
import com.ssu.gardenmaker.retrofit.garden.GardenDataContent

class ApplicationClass : Application() {

    companion object {
        const val BASE_URL: String = "http://3.39.15.41:8080/"
        const val TAG: String = "GardenMaker"
        lateinit var db: SQLiteDatabase
        lateinit var dbHelper : MyDBHelper
        lateinit var retrofitAPI: RetrofitAPI
        lateinit var retrofitManager: RetrofitManager
        lateinit var mSharedPreferences: SharedPreferences
        lateinit var categoryLists : MutableList<GardenDataContent>
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            dbHelper = MyDBHelper(applicationContext)
            db = dbHelper.readableDatabase
            db = dbHelper.writableDatabase
        }

        retrofitAPI = RetrofitClient().getClient(BASE_URL)!!.create(RetrofitAPI::class.java)
        retrofitManager = RetrofitManager()

        mSharedPreferences = applicationContext.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        categoryLists = mutableListOf()

        //FcmTokenUtil().loadFcmToken()
    }
}