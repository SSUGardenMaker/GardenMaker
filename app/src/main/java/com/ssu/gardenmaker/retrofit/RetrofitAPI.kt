package com.ssu.gardenmaker.retrofit

import com.ssu.gardenmaker.retrofit.login.RequestLogin
import com.ssu.gardenmaker.retrofit.login.ResponseLogin
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

    // 토큰 등록
    @Headers("Content-Type: application/json")
    @POST("/users/token")
    fun tokenPostRequest(@Query("token") token: String): Call<Any>

    // 토큰 삭제
    @Headers("Content-Type: application/json")
    @DELETE("/users/token")
    fun tokenDeleteRequest(): Call<Any>
}