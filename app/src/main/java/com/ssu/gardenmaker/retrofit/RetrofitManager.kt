package com.ssu.gardenmaker.retrofit

import android.util.Log
import com.google.gson.Gson
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.retrofit.callback.RetrofitGardenCallback
import com.ssu.gardenmaker.retrofit.garden.RequestGardenCreateEdit
import com.ssu.gardenmaker.retrofit.garden.ResponseGarden
import com.ssu.gardenmaker.retrofit.garden.ResponseGardenCreateEditDelete
import com.ssu.gardenmaker.retrofit.login.ErrorLogin
import com.ssu.gardenmaker.retrofit.login.RequestLogin
import com.ssu.gardenmaker.retrofit.login.ResponseLogin
import com.ssu.gardenmaker.retrofit.password.ErrorFindPassword
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
                        val errorSignup = gson.fromJson(responseBody!!.string(), ErrorSignup::class.java)
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
                        val errorLogin = gson.fromJson(responseBody!!.string(), ErrorLogin::class.java)
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
                    val responseBody = response.errorBody()
                    val gson = Gson()
                    try {
                        val errorFindPassword = gson.fromJson(responseBody!!.string(), ErrorFindPassword::class.java)
                        Log.d("RetrofitManager_findPassword", "onResponse : 실패, error message : " + errorFindPassword.errorMessage)
                        Log.d("RetrofitManager_findPassword", "onResponse : 실패, error code : " + response.code())
                        callback.onFailure(errorFindPassword.errorMessage, response.code())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
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

    // 화단 보기
    fun gardenCheck(callback: RetrofitGardenCallback) {
        val call: Call<ResponseGarden> = ApplicationClass.retrofitAPI.gardenCheckRequest()

        call.enqueue(object : Callback<ResponseGarden> {
            override fun onResponse(call: Call<ResponseGarden>, response: Response<ResponseGarden>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("RetrofitManager_gardenCheck", "onResponse : 성공, message : " + body!!.message)
                    Log.d("RetrofitManager_gardenCheck", "onResponse : status code is " + response.code())
                    callback.onSuccess(body.message, body.data)
                }
                else {
                    Log.d("RetrofitManager_gardenCheck", "onResponse : 실패, error code : " + response.code())
                    callback.onFailure("", response.code())
                }
            }

            override fun onFailure(call: Call<ResponseGarden>, t: Throwable) {
                Log.e("RetrofitManager_gardenCheck", "onFailure : " + t.localizedMessage)
                callback.onError(t)
            }
        })
    }

    // 화단 생성
    fun gardenCreate(category: String, name: String, callback: RetrofitCallback) {
        val requestGardenCreate = RequestGardenCreateEdit(category, name)
        val call: Call<ResponseGardenCreateEditDelete> = ApplicationClass.retrofitAPI.gardenCreateRequest(requestGardenCreate)

        call.enqueue(object : Callback<ResponseGardenCreateEditDelete> {
            override fun onResponse (call: Call<ResponseGardenCreateEditDelete>, response: Response<ResponseGardenCreateEditDelete>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("RetrofitManager_gardenCreate", "onResponse : 성공, message : " + body!!.message)
                    Log.d("RetrofitManager_gardenCreate", "onResponse : status code is " + response.code())
                    callback.onSuccess(body.message, body.data.toString())
                }
                else {
                    Log.d("RetrofitManager_gardenCreate", "onResponse : 실패, error code : " + response.code())
                    callback.onFailure("", response.code())
                }
            }

            override fun onFailure(call: Call<ResponseGardenCreateEditDelete>, t: Throwable) {
                Log.d("RetrofitManager_gardenCreate", "onFailure : " + t.localizedMessage)
                callback.onError(t)
            }
        })
    }

    // 화단 수정
    fun gardenEdit(category: String, name: String, callback: RetrofitCallback) {
        val requestGardenEdit = RequestGardenCreateEdit(category, name)
        val call: Call<ResponseGardenCreateEditDelete> = ApplicationClass.retrofitAPI.gardenEditRequest(requestGardenEdit)

        call.enqueue(object : Callback<ResponseGardenCreateEditDelete> {
            override fun onResponse (call: Call<ResponseGardenCreateEditDelete>, response: Response<ResponseGardenCreateEditDelete>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("RetrofitManager_gardenEdit", "onResponse : 성공, message : " + body!!.message)
                    Log.d("RetrofitManager_gardenEdit", "onResponse : status code is " + response.code())
                    callback.onSuccess(body.message, body.data.toString())
                }
                else {
                    Log.d("RetrofitManager_gardenEdit", "onResponse : 실패, error code : " + response.code())
                    callback.onFailure("", response.code())
                }
            }

            override fun onFailure(call: Call<ResponseGardenCreateEditDelete>, t: Throwable) {
                Log.d("RetrofitManager_gardenEdit", "onFailure : " + t.localizedMessage)
                callback.onError(t)
            }
        })
    }

    // 화단 삭제
    fun gardenDelete(gardenId: Int, callback: RetrofitCallback) {
        val call: Call<ResponseGardenCreateEditDelete> = ApplicationClass.retrofitAPI.gardenDeleteRequest(gardenId)

        call.enqueue(object : Callback<ResponseGardenCreateEditDelete> {
            override fun onResponse (call: Call<ResponseGardenCreateEditDelete>, response: Response<ResponseGardenCreateEditDelete>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("RetrofitManager_gardenDelete", "onResponse : 성공, message : " + body!!.message)
                    Log.d("RetrofitManager_gardenDelete", "onResponse : status code is " + response.code())
                    callback.onSuccess(body.message, body.data.toString())
                }
                else {
                    Log.d("RetrofitManager_gardenDelete", "onResponse : 실패, error code : " + response.code())
                    callback.onFailure("", response.code())
                }
            }

            override fun onFailure(call: Call<ResponseGardenCreateEditDelete>, t: Throwable) {
                Log.d("RetrofitManager_gardenDelete", "onFailure : " + t.localizedMessage)
                callback.onError(t)
            }
        })
    }
}