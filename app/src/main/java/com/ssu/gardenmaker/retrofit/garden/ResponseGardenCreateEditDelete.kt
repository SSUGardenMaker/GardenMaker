package com.ssu.gardenmaker.retrofit.garden

import com.google.gson.annotations.SerializedName

data class ResponseGardenCreateEditDelete(
    @SerializedName("message") val message : String,
    @SerializedName("data") var data: Int
)
