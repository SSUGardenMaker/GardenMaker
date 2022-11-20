package com.ssu.gardenmaker.plant

data class PlantData(
    var plantName: String,
    var plantType: String,
    var startDate: String,
    var endDate: String,
    var goalStepPedometer: String,      // 만보기
    var goalCountPedometer: String,     // 만보기
    var goalCountCounter: String,       // 횟수
    var goalTimerAccumulate: String,    // 누적 타이머
    var goalTimerRecursive: String,     // 반복 타이머
    var goalCountTimerRecursive: String // 반복 타이머
)
