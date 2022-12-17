package com.ssu.gardenmaker.retrofit.login

import com.google.gson.annotations.SerializedName

data class ErrorLogin(
    @SerializedName("errorMessage") var errorMessage: String,
    @SerializedName("errors") var errors: ErrorContent
)

data class ErrorContent(
    @SerializedName("email") val email : String
)
