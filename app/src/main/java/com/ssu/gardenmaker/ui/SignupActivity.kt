package com.ssu.gardenmaker.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.databinding.ActivitySignupBinding
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback

class SignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding
    private val TAG = "SignupActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignup.setOnClickListener {
            signup()
        }

        binding.tvSignupGoLogin.setOnClickListener {
            finish()
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
        }
    }

    // 화면 터치 시 키보드 내려감
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val focusView = currentFocus
        if (focusView != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 회원가입 기능
    private fun signup() {
        ApplicationClass.retrofitManager.signup(binding.etSignupEmail.text.toString(), binding.etSignupPassword.text.toString(), binding.etSignupNickname.text.toString(), object : RetrofitCallback {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError : " + t.localizedMessage)
            }

            override fun onSuccess(message: String, data: String) {
                Log.d(TAG, "onSuccess : message -> $message")
                Log.d(TAG, "onSuccess : email -> $data")
                Toast.makeText(this@SignupActivity, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show()

                finish()
                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            }

            override fun onFailure(errorMessage: String, errorCode: Int) {
                Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                Log.d(TAG, "onFailure : errorCode -> $errorCode")
            }
        })
    }
}