package com.ssu.gardenmaker.retrofit.garden

import com.google.gson.annotations.SerializedName

data class RequestGardenCreateEdit(
    @SerializedName("Category") var category: String,
    @SerializedName("name") var name: String
)
