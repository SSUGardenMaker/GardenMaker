package com.ssu.gardenmaker.retrofit.garden

import com.google.gson.annotations.SerializedName

data class ErrorGarden(
    @SerializedName("errorMessage") val errorMessage : String
)
