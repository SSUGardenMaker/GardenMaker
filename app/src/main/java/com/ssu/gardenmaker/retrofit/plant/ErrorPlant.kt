package com.ssu.gardenmaker.retrofit.plant

import com.google.gson.annotations.SerializedName

data class ErrorPlant(
    @SerializedName("errorMessage") val errorMessage : String
)
