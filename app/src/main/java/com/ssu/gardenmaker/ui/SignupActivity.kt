package com.ssu.gardenmaker.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.ssu.gardenmaker.databinding.ActivitySignupBinding
import com.ssu.gardenmaker.runnable.SignupRunnable


class SignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding

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
        val dialogBuilder = AlertDialog.Builder(this)
        val dialog = dialogBuilder.create()
        dialogBuilder.setTitle("알림")
        dialogBuilder.setPositiveButton("확인") { p0, p1 -> dialog.dismiss() }

        if (binding.etSignupEmail.text.toString().trim().isEmpty()
            || binding.etSignupPassword.text.toString().trim().isEmpty()
            || binding.etSignupPasswordCheck.text.toString().trim().isEmpty()
            || binding.etSignupNickname.text.toString().trim().isEmpty()) {
            dialogBuilder.setMessage("빈 칸을 전부 채워주세요.")
            dialogBuilder.show()
        }
        else if (binding.etSignupPassword.text.toString() != binding.etSignupPasswordCheck.text.toString()) {
            dialogBuilder.setMessage("두 비밀번호가 서로 다릅니다.")
            dialogBuilder.show()
        }
        else {
            val progressDialog = Dialog(this)
            progressDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경을 투명하게
            progressDialog.setContentView(ProgressBar(this)) // ProgressBar 위젯 생성
            progressDialog.setCanceledOnTouchOutside(false) // 외부 터치 막음
            progressDialog.setOnCancelListener { this.finish() } // 뒤로가기시 현재 액티비티 종료
            progressDialog.show()

            val handlerSignup = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)

                    if (msg.arg1 == 1) {
                        finish()
                        startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    }

                    progressDialog.dismiss()
                }
            }

            val thread = Thread(SignupRunnable(handlerSignup, this, binding.etSignupEmail.text.toString(), binding.etSignupPassword.text.toString(), binding.etSignupNickname.text.toString()))
            thread.start()
        }
    }
}