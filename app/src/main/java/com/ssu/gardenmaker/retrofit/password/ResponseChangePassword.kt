package com.ssu.gardenmaker.retrofit.password

import com.google.gson.annotations.SerializedName

data class ResponseChangePassword(
    @SerializedName("message") var message: String
)
