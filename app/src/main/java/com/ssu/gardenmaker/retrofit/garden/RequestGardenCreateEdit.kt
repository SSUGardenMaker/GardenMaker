package com.ssu.gardenmaker.retrofit.garden

import com.google.gson.annotations.SerializedName

data class RequestGardenCreateEdit(
    @SerializedName("Category") var category: String,
    @SerializedName("Name") var name: String
)
