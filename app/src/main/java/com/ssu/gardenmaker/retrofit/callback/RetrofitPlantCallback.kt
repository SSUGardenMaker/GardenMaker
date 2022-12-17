package com.ssu.gardenmaker.retrofit.callback

import com.ssu.gardenmaker.retrofit.plant.PlantDataContent

interface RetrofitPlantCallback {
    fun onError(t: Throwable)

    fun onSuccess(message: String, data: List<PlantDataContent>)

    fun onFailure(errorMessage: String, errorCode: Int)
}