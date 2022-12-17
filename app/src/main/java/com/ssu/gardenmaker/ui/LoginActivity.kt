package com.ssu.gardenmaker.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.databinding.ActivityLoginBinding
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.util.SharedPreferenceManager
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private val TAG = "LoginActivity"
    private var backKeyPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
        }

        binding.tvFind.setOnClickListener {
            startActivity(Intent(this@LoginActivity, FindActivity::class.java))
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

    // 2초 이내에 뒤로가기 버튼을 한번 더 클릭시 앱 종료
    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            moveTaskToBack(true) // 태스크를 백그라운드로 이동
            finishAndRemoveTask() // 액티비티 종료 + 태스크 리스트에서 지우기
            exitProcess(0)
        }
    }

    private fun login() {
        if (binding.etEmailLogin.text.toString().trim().isEmpty() || binding.etPasswordLogin.text.toString().trim().isEmpty()) {
            val dialogBuilder = AlertDialog.Builder(this)
            val dialog = dialogBuilder.create()

            dialogBuilder.setTitle("알림")
            dialogBuilder.setMessage("빈 칸을 전부 채워주세요.")
            dialogBuilder.setPositiveButton("확인", null)
            dialogBuilder.show()
            dialog.dismiss()
        }
        else {
            ApplicationClass.retrofitManager.login(binding.etEmailLogin.text.toString(), binding.etPasswordLogin.text.toString(), object : RetrofitCallback {
                override fun onError(t: Throwable) {
                    Log.d(TAG, "onError : " + t.localizedMessage)
                }

                override fun onSuccess(message: String, data: String) {
                    Log.d(TAG, "onSuccess : message -> $message")
                    Log.d(TAG, "onSuccess : accessToken -> $data")
                    Toast.makeText(this@LoginActivity, "로그인 완료", Toast.LENGTH_SHORT).show()

                    SharedPreferenceManager().setString("email", binding.etEmailLogin.text.toString())
                    SharedPreferenceManager().setString("password", binding.etPasswordLogin.text.toString())
                    SharedPreferenceManager().setString("accessToken", data)

                    if (binding.loginCheckbox.isChecked)
                        SharedPreferenceManager().setString("keepLogin", "ON")
                    else
                        SharedPreferenceManager().setString("keepLogin", "OFF")

                    finish()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }

                override fun onFailure(errorMessage: String, errorCode: Int) {
                    Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                    Log.d(TAG, "onFailure : errorCode -> $errorCode")
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}