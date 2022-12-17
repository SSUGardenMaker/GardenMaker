package com.ssu.gardenmaker.ui

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.util.SharedPreferenceManager

class FirstActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        if (action == MotionEvent.ACTION_DOWN) {
            // 자동 로그인
            val loginEmail = ApplicationClass.mSharedPreferences.getString("email", "")
            val loginPassword = ApplicationClass.mSharedPreferences.getString("password", "")
            val loginFlag = ApplicationClass.mSharedPreferences.getString("keepLogin", "")

            if (!loginEmail.equals("") && !loginPassword.equals("") && loginFlag.equals("ON")) {
                ApplicationClass.retrofitManager.login(loginEmail!!, loginPassword!!, object : RetrofitCallback {
                    override fun onError(t: Throwable) {
                    }

                    override fun onSuccess(message: String, data: String) {
                        SharedPreferenceManager().setString("accessToken", data)

                        finish()
                        startActivity(Intent(this@FirstActivity, MainActivity::class.java))
                    }

                    override fun onFailure(errorMessage: String, errorCode: Int) {
                        Toast.makeText(this@FirstActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }

                })
            }
            else {
                finish()
                startActivity(Intent(this@FirstActivity, LoginActivity::class.java))
            }
        }
        return super.onTouchEvent(event)
    }
}