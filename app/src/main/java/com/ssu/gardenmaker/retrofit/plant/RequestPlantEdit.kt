package com.ssu.gardenmaker.retrofit.plant

import com.google.gson.annotations.SerializedName

data class RequestPlantEdit(
    @SerializedName("context1") var context1 : String,
    @SerializedName("context2") val context2 : String,
    @SerializedName("gardenId") val gardenId : Int,
    @SerializedName("name") val name : String
)
