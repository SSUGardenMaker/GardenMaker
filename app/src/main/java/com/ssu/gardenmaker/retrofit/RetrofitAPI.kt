package com.ssu.gardenmaker.retrofit

import com.ssu.gardenmaker.retrofit.garden.RequestGardenCreateEdit
import com.ssu.gardenmaker.retrofit.garden.ResponseGarden
import com.ssu.gardenmaker.retrofit.garden.ResponseGardenCreateEditDelete
import com.ssu.gardenmaker.retrofit.login.RequestLogin
import com.ssu.gardenmaker.retrofit.login.ResponseLogin
import com.ssu.gardenmaker.retrofit.password.RequestChangePassword
import com.ssu.gardenmaker.retrofit.password.ResponseChangePassword
import com.ssu.gardenmaker.retrofit.password.ResponseFindPassword
import com.ssu.gardenmaker.retrofit.plant.RequestPlantCreate
import com.ssu.gardenmaker.retrofit.plant.RequestPlantEdit
import com.ssu.gardenmaker.retrofit.plant.ResponsePlant
import com.ssu.gardenmaker.retrofit.plant.ResponsePlantCheck
import com.ssu.gardenmaker.retrofit.signup.RequestSignup
import com.ssu.gardenmaker.retrofit.signup.ResponseSignup
import retrofit2.Call
import retrofit2.http.*

interface RetrofitAPI {
    // 회원가입
    @Headers("Content-Type: application/json")
    @POST("/users/garden")
    fun signupRequest(@Body body: RequestSignup): Call<ResponseSignup>

    // 로그인
    @Headers("Content-Type: application/json")
    @POST("/users/login")
    fun loginRequest(@Body body: RequestLogin): Call<ResponseLogin>

    // 비밀번호 찾기
    @Headers("Content-Type: application/json")
    @GET("/users/password")
    fun findPasswordRequest(@Query("email") email: String): Call<ResponseFindPassword>

    // 비밀번호 수정
    @Headers("Content-Type: application/json")
    @POST("/users/password")
    fun changePasswordRequest(@Body body: RequestChangePassword): Call<ResponseChangePassword>

    // 화단 보기
    @Headers("Content-Type: application/json")
    @GET("/garden")
    fun gardenCheckRequest(): Call<ResponseGarden>

    // 화단 생성
    @Headers("Content-Type: application/json")
    @POST("/garden")
    fun gardenCreateRequest(@Body body: RequestGardenCreateEdit): Call<ResponseGardenCreateEditDelete>

    // 화단 수정
    @Headers("Content-Type: application/json")
    @PUT("/garden/{gardenId}")
    fun gardenEditRequest(@Path("gardenId") gardenId: Int, @Body body: RequestGardenCreateEdit): Call<ResponseGardenCreateEditDelete>

    // 화단 삭제
    @Headers("Content-Type: application/json")
    @DELETE("/garden/{gardenId}")
    fun gardenDeleteRequest(@Path("gardenId") gardenId: Int): Call<ResponseGardenCreateEditDelete>

    // 전체 식물 보기
    @Headers("Content-Type: application/json")
    @GET("/plant")
    fun plantAllCheckRequest(): Call<ResponsePlantCheck>

    // 화단별 식물 보기
    @Headers("Content-Type: application/json")
    @GET("/plant/{gardenId}")
    fun plantGardenCheckRequest(@Path("gardenId") gardenId: Int): Call<ResponsePlantCheck>

    // 환료한 날짜 순으로 완료된 식물 보기
    @Headers("Content-Type: application/json")
    @GET("/plant/completed")
    fun plantDoneCheckRequest(): Call<ResponsePlantCheck>

    // 식물 생성
    @Headers("Content-Type: application/json")
    @POST("/plant")
    fun plantCreateRequest(@Body body: RequestPlantCreate): Call<ResponsePlant>

    // 식물 수정
    @Headers("Content-Type: application/json")
    @PUT("/plant/{plantId}")
    fun plantEditRequest(@Path("plantId") plantId: Int, @Body body: RequestPlantEdit): Call<ResponsePlant>

    // 식물 삭제
    @Headers("Content-Type: application/json")
    @DELETE("/plant/{plantId}")
    fun plantDeleteRequest(@Path("plantId") plantId: Int): Call<ResponsePlant>

    // 식물 물 주기 (만보기, 횟수, 반복 타이머)
    @Headers("Content-Type: application/json")
    @POST("/plant/{plantId}/water")
    fun plantWateringRequest(@Path("plantId") plantId: Int): Call<ResponsePlant>

    // 식물 물 주기 (누적 타이머)
    @Headers("Content-Type: application/json")
    @POST("/plant/{plantId}/waterAcc")
    fun plantWateringAccRequest(@Path("plantId") plantId: Int, @Query("min") min: Int): Call<ResponsePlant>
}