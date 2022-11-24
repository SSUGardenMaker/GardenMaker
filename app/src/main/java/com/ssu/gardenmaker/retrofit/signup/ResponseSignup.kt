package com.ssu.gardenmaker.retrofit.signup

import com.google.gson.annotations.SerializedName

data class ResponseSignup(
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : DataContent
)

data class DataContent(
    @SerializedName("email") val email : String
)