package com.ssu.gardenmaker.retrofit.signup

import com.google.gson.annotations.SerializedName

data class ErrorSignup(
    @SerializedName("errorMessage") var errorMessage: String
)