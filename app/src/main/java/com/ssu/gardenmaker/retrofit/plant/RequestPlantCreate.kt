package com.ssu.gardenmaker.retrofit.plant

import com.google.gson.annotations.SerializedName

data class RequestPlantCreate(
    @SerializedName("context1") var context1 : String,
    @SerializedName("context2") val context2 : String,
    @SerializedName("gardenId") val gardenId : Int, // 화단 ID
    @SerializedName("plantType") val plantType : String,    // 식물 종류 (CHECKBOX, WALK_COUNTER, COUNTER, TIMER)
    @SerializedName("name") val name : String,  // 식물 이름
    @SerializedName("requiredDays") val requiredDays : Int, // endDate - startDate
    @SerializedName("walkStep") val walkStep : Int, // 목표 걸음 수 (만보기)
    @SerializedName("counterGoal") val counterGoal : Int,   // 목표 달성 횟수 (만보기, 횟수, 반복 타이머)
    @SerializedName("timerTotalMin") val timerTotalMin : Int,   // 목표 누적 시간 (누적 타이머)
    @SerializedName("timerRecurMin") val timerRecurMin : Int,    // 목표 반복 시간 (반복 타이머)
    @SerializedName("counter") var counter : Int,   // 현재까지 달성한 횟수 (만보기, 횟수, 반복 타이머)
    @SerializedName("timerCurrentMin") val timerCurrentMin : Int    // 현재까지 달성한 시간 (누적 타이머)
)
