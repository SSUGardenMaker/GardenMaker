package com.ssu.gardenmaker.retrofit.password

import com.google.gson.annotations.SerializedName

data class ErrorFindPassword(
    @SerializedName("errorMessage") var errorMessage: String
)
