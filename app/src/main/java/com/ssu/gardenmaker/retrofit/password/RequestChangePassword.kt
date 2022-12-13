package com.ssu.gardenmaker.retrofit.password

import com.google.gson.annotations.SerializedName

data class RequestChangePassword(
    @SerializedName("currentPassword") var currentPassword: String,
    @SerializedName("newPassword") var newPassword: String
)
