package com.ssu.gardenmaker.retrofit.callback

interface RetrofitCallback {
    fun onError(t: Throwable)

    fun onSuccess(message: String, data: String)

    fun onFailure(errorMessage: String, errorCode: Int)
}