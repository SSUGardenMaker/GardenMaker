package com.ssu.gardenmaker.retrofit.callback

import com.ssu.gardenmaker.retrofit.garden.GardenDataContent

interface RetrofitGardenCallback {
    fun onError(t: Throwable)

    fun onSuccess(message: String, data: List<GardenDataContent>)

    fun onFailure(errorMessage: String, errorCode: Int)
}