package com.ssu.gardenmaker.retrofit.login

import com.google.gson.annotations.SerializedName

data class ResponseLogin (
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : DataContent
)

data class DataContent(
    @SerializedName("accessToken") val accessToken : String
)