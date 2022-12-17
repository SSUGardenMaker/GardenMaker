package com.ssu.gardenmaker.retrofit.signup

import com.google.gson.annotations.SerializedName

data class RequestSignup(
    @SerializedName("email") var email: String,
    @SerializedName("password") var password: String,
    @SerializedName("nickname") var nickname: String
)
