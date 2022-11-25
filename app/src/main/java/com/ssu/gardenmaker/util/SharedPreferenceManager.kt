package com.ssu.gardenmaker.util

import com.ssu.gardenmaker.ApplicationClass

class SharedPreferenceManager {
    fun setInt(key: String, data: Int) {
        val editor = ApplicationClass.mSharedPreferences.edit()
        editor.putInt(key, data)
        editor.apply()
    }

    fun getInt(key : String) : Int {
        return ApplicationClass.mSharedPreferences.getInt(key, -1)
    }

    fun setString(key: String, data: String) {
        val editor = ApplicationClass.mSharedPreferences.edit()
        editor.putString(key, data)
        editor.apply()
    }

    fun getString(key : String) : String? {
        return ApplicationClass.mSharedPreferences.getString(key, null)
    }

    fun deleteData(key : String) {
        ApplicationClass.mSharedPreferences.edit().remove(key).apply()
    }
}