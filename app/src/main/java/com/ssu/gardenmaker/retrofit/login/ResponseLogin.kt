package com.ssu.gardenmaker.retrofit.login

import com.google.gson.annotations.SerializedName

data class ResponseLogin (
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : LoginDataContent
)

data class LoginDataContent(
    @SerializedName("accessToken") val accessToken : String
)