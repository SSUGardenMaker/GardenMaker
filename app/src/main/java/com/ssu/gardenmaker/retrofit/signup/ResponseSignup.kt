package com.ssu.gardenmaker.retrofit.signup

import com.google.gson.annotations.SerializedName

data class ResponseSignup(
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : SignupDataContent
)

data class SignupDataContent(
    @SerializedName("email") val email : String
)