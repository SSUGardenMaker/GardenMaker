package com.ssu.gardenmaker.retrofit.callback

import com.ssu.gardenmaker.retrofit.garden.DataContent

interface RetrofitGardenCallback {
    fun onError(t: Throwable)

    fun onSuccess(message: String, data: DataContent)

    fun onFailure(errorMessage: String, errorCode: Int)
}