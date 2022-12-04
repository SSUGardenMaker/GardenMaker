package com.ssu.gardenmaker.retrofit.plant

import com.google.gson.annotations.SerializedName

data class ResponsePlantCreateEditDelete(
    @SerializedName("message") val message : String,
    @SerializedName("data") var data: Int
)
