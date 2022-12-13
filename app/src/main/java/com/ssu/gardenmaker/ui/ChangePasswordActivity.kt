package com.ssu.gardenmaker.ui

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.databinding.ActivityChangePasswordBinding
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.util.SharedPreferenceManager

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private val TAG = "ChangePasswordActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvCurrentEmail.text = SharedPreferenceManager().getString("email")

        binding.ibChangePasswordBack.setOnClickListener {
            finish()
        }

        binding.btnChangePassword.setOnClickListener {
            changePassword()
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

    // 비밀번호 변경
    private fun changePassword() {
        if (binding.etCurrentPassword.text.toString() == SharedPreferenceManager().getString("password")) {
            if (binding.etNewPassword.text.toString().length in 6..20) {
                if (binding.etNewPassword.text.toString() == binding.etNewPasswordCheck.text.toString()) {
                    ApplicationClass.retrofitManager.changePassword(binding.etCurrentPassword.text.toString(), binding.etNewPassword.text.toString(), object : RetrofitCallback {
                        override fun onError(t: Throwable) {
                            Log.d(TAG, "onError : " + t.localizedMessage)
                        }

                        override fun onSuccess(message: String, data: String) {
                            Log.d(TAG, "onSuccess : message -> $message")
                            Log.d(TAG, "onSuccess : data -> $data")
                            Toast.makeText(this@ChangePasswordActivity, "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show()

                            SharedPreferenceManager().setString("password", binding.etNewPassword.text.toString())

                            finish()
                        }

                        override fun onFailure(errorMessage: String, errorCode: Int) {
                            Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                            Log.d(TAG, "onFailure : errorCode -> $errorCode")
                            Toast.makeText(this@ChangePasswordActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                else {
                    Toast.makeText(this@ChangePasswordActivity, "새 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this@ChangePasswordActivity, "비밀번호는 최소 6자 이상 20자 이하여야 합니다", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(this@ChangePasswordActivity, "현재 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show()
        }
    }
}