package com.ssu.gardenmaker.retrofit.password

import com.google.gson.annotations.SerializedName

data class ResponseFindPassword(
    @SerializedName("message") var message: String
)
