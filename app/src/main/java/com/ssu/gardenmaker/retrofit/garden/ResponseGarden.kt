package com.ssu.gardenmaker.retrofit.garden

import com.google.gson.annotations.SerializedName

data class ResponseGarden(
    @SerializedName("message") val message : String,
    @SerializedName("data") val data : List<GardenDataContent>
)

data class GardenDataContent(
    @SerializedName("category") var category : String,
    @SerializedName("id") val id : Int,
    @SerializedName("name") var name : String
)