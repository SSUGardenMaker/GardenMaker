package com.ssu.gardenmaker.retrofit

import android.util.Log
import com.ssu.gardenmaker.ApplicationClass
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    private var retrofitClient: Retrofit? = null

    fun getClient(baseURL: String): Retrofit? {

        val client = OkHttpClient.Builder()
        client.addInterceptor(Interceptor { chain ->
            val token: String = ApplicationClass.mSharedPreferences.getString("accessToken", "")!!
            val newRequest = chain.request().newBuilder().addHeader("Authorization", token).build()
            chain.proceed(newRequest)
        })

        val loggingInterceptor = HttpLoggingInterceptor { message: String ->
            Log.d("RetrofitClient", "message : $message")
        }
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        client.addInterceptor(loggingInterceptor)
        client.connectTimeout(10, TimeUnit.SECONDS)
        client.readTimeout(10, TimeUnit.SECONDS)
        client.writeTimeout(10, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)

        if (retrofitClient == null) {
            retrofitClient = Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build()
        }

        return retrofitClient
    }
}