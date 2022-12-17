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
import com.ssu.gardenmaker.databinding.ActivityFindBinding
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback

class FindActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFindBinding
    private val TAG = "FindActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFindEmail.setOnClickListener {
            findEmail()
        }

        binding.tvFindGoLogin.setOnClickListener {
            finish()
            startActivity(Intent(this@FindActivity, LoginActivity::class.java))
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

    // 비밀번호 찾기 기능
    private fun findEmail() {
        if (binding.etFindEmail.text.toString().trim().isEmpty()) {
            val dialogBuilder = AlertDialog.Builder(this)
            val dialog = dialogBuilder.create()

            dialogBuilder.setTitle("알림")
            dialogBuilder.setMessage("비밀번호를 찾을 이메일을 입력해주세요.")
            dialogBuilder.setPositiveButton("확인", null)
            dialogBuilder.show()
            dialog.dismiss()
        }
        else {
            ApplicationClass.retrofitManager.findPassword(binding.etFindEmail.text.toString(), object : RetrofitCallback {
                override fun onError(t: Throwable) {
                    Log.d(TAG, "onError : " + t.localizedMessage)
                }

                override fun onSuccess(message: String, data: String) {
                    Log.d(TAG, "onSuccess : $message $data")
                    Toast.makeText(this@FindActivity, "입력하신 이메일로 전송된 인증 메일을 확인해주세요", Toast.LENGTH_SHORT).show()

                    finish()
                    startActivity(Intent(this@FindActivity, LoginActivity::class.java))
                }

                override fun onFailure(errorMessage: String, errorCode: Int) {
                    Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                    Log.d(TAG, "onFailure : errorCode -> $errorCode")
                    Toast.makeText(this@FindActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}