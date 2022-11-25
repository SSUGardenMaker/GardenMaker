package com.ssu.gardenmaker.retrofit

import android.util.Log
import com.google.gson.Gson
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.retrofit.login.ErrorLogin
import com.ssu.gardenmaker.retrofit.login.RequestLogin
import com.ssu.gardenmaker.retrofit.login.ResponseLogin
import com.ssu.gardenmaker.retrofit.password.ResponseFindPassword
import com.ssu.gardenmaker.retrofit.signup.ErrorSignup
import com.ssu.gardenmaker.retrofit.signup.RequestSignup
import com.ssu.gardenmaker.retrofit.signup.ResponseSignup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RetrofitManager {
    // 회원가입
    fun signup(email: String, password: String, nickname: String, callback: RetrofitCallback) {
        val requestSignup = RequestSignup(email, password, nickname)
        val call: Call<ResponseSignup> = ApplicationClass.retrofitAPI.signupRequest(requestSignup)

        call.enqueue(object : Callback<ResponseSignup> {
            override fun onResponse (call: Call<ResponseSignup>, response: Response<ResponseSignup>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("RetrofitManager_signup", "onResponse : 성공, message : " + body!!.message)
                    Log.d("RetrofitManager_signup", "onResponse : status code is " + response.code())
                    callback.onSuccess(body.message, body.data.email)
                }
                else {
                    val responseBody = response.errorBody()
                    val gson = Gson()
                    try {
                        val errorSignup: ErrorSignup = gson.fromJson(responseBody!!.string(), ErrorSignup::class.java)
                        Log.d("RetrofitManager_signup", "onResponse : 실패, error message : " + errorSignup.errorMessage)
                        Log.d("RetrofitManager_signup", "onResponse : 실패, error code : " + response.code())
                        callback.onFailure(errorSignup.errorMessage, response.code())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseSignup>, t: Throwable) {
                Log.d("RetrofitManager_signup", "onFailure : " + t.localizedMessage)
                callback.onError(t)
            }
        })
    }

    // 로그인
    fun login(email: String, password: String, callback: RetrofitCallback) {
        val requestLogin = RequestLogin(email, password)
        val call: Call<ResponseLogin> = ApplicationClass.retrofitAPI.loginRequest(requestLogin)

        call.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse (call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("RetrofitManager_login", "onResponse : 성공, message : " + body!!.message)
                    Log.d("RetrofitManager_login", "onResponse : status code is " + response.code())
                    callback.onSuccess(body.message, body.data.accessToken)
                }
                else {
                    val responseBody = response.errorBody()
                    val gson = Gson()
                    try {
                        val errorLogin: ErrorLogin = gson.fromJson(responseBody!!.string(), ErrorLogin::class.java)
                        Log.d("RetrofitManager_signup", "onResponse : 실패, error message : " + errorLogin.errorMessage)
                        Log.d("RetrofitManager_signup", "onResponse : 실패, error code : " + response.code())
                        callback.onFailure(errorLogin.errorMessage, response.code())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                Log.d("RetrofitManager_login", "onFailure : " + t.localizedMessage)
                callback.onError(t)
            }
        })
    }

    // 비밀번호 찾기
    fun findPassword(email: String, callback: RetrofitCallback) {
        val call: Call<ResponseFindPassword> = ApplicationClass.retrofitAPI.findPasswordRequest(email)

        call.enqueue(object : Callback<ResponseFindPassword> {
            override fun onResponse(call: Call<ResponseFindPassword>, response: Response<ResponseFindPassword>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("RetrofitManager_findPassword", "onResponse : 성공, message : " + body!!.message)
                    Log.d("RetrofitManager_findPassword", "onResponse : status code is " + response.code())
                    callback.onSuccess("findPassword 호출 : ", body.message)
                }
                else {
                    val body = response.body()
                    Log.d("RetrofitManager_findPassword", "onResponse : 실패, error message : " + body!!.errorMessage)
                    Log.d("RetrofitManager_findPassword", "onResponse : 실패, error code : " + response.code())
                    callback.onFailure(body.errorMessage, response.code())
                }
            }

            override fun onFailure(call: Call<ResponseFindPassword>, t: Throwable) {
                Log.d("RetrofitManager_findPassword", "onFailure : " + t.localizedMessage)
                callback.onError(t)
            }
        })
    }

    // 토큰 등록
    fun postToken(token: String, callback: RetrofitCallback) {
        val call: Call<Any> = ApplicationClass.retrofitAPI.tokenPostRequest(token)
        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("RetrofitManager_postToken", "onResponse : 성공, message : " + body.toString())
                    Log.d("RetrofitManager_postToken", "onResponse : status code is " + response.code())
                    callback.onSuccess("postToken() 호출 : ", body.toString())
                }
                else {
                    Log.d("RetrofitManager_postToken", "onResponse : 실패, error code : " + response.code())
                    callback.onFailure("", response.code())
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.e("RetrofitManager_postToken", "onFailure : " + t.localizedMessage)
                callback.onError(t)
            }
        })
    }

    // 토큰 삭제
    fun deleteToken(callback: RetrofitCallback) {
        val call: Call<Any> = ApplicationClass.retrofitAPI.tokenDeleteRequest()
        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("RetrofitManager_deleteToken", "onResponse : 성공, message : " + body.toString())
                    Log.d("RetrofitManager_deleteToken", "onResponse : status code is " + response.code())
                    callback.onSuccess("deleteToken() 호출 : ", body.toString())
                }
                else {
                    Log.d("RetrofitManager_deleteToken", "onResponse : 실패, error code : " + response.code())
                    callback.onFailure("", response.code())
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.e("RetrofitManager_deleteToken", "onFailure : " + t.localizedMessage)
                callback.onError(t)
            }
        })
    }
}