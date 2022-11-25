package com.ssu.gardenmaker.util

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging

class FcmTokenUtil {
    fun loadFcmToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(object : OnCompleteListener<String?> {
                override fun onComplete(task: Task<String?>) {
                    if (!task.isSuccessful) {
                        Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                        return
                    }

                    // Get new FCM registration token
                    SharedPreferenceManager().setString("FcmToken", task.result!!)
                }
            })
    }

    fun getFcmToken(): String? {
        return SharedPreferenceManager().getString("FcmToken")
    }
}