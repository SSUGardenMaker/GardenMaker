package com.ssu.gardenmaker.runnable

import android.content.Context
import android.os.Handler
import android.widget.Toast
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback

class SignupRunnable(handler: Handler, context: Context, email: String, password: String, nickname: String) : Runnable {

    private var mHandler: Handler = handler
    private var mContext: Context = context
    private var mEmail: String = email
    private var mPassword: String = password
    private var mNickname: String = nickname

    override fun run() {
        val hMessage = mHandler.obtainMessage()
        ApplicationClass.retrofitManager.signup(mEmail, mPassword, mNickname, object : RetrofitCallback {
            override fun onError(t: Throwable) {
                hMessage.arg1 = -1
                mHandler.sendMessage(hMessage)
            }

            override fun onSuccess(message: String, data: String) {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                hMessage.arg1 = 1
                mHandler.sendMessage(hMessage)
            }

            override fun onFailure(errorMessage: String, errorCode: Int) {
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()
                hMessage.arg1 = 0
                mHandler.sendMessage(hMessage)
            }
        })
    }
}