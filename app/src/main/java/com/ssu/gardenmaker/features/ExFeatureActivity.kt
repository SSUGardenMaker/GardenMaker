package com.ssu.gardenmaker.features

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.features.accumulateTimer.accumulateTimerService
import com.ssu.gardenmaker.features.counter.counter
import com.ssu.gardenmaker.features.recursiveTimer.recursiveTimerService
import java.util.*


class ExFeatureActivity : AppCompatActivity() {
    val btn1 by lazy { findViewById<Button>(R.id.btn22) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ex_feature)

        findViewById<Button>(R.id.btn11).setOnClickListener {

        }
        findViewById<Button>(R.id.btn22).setOnClickListener {
            btn1.isEnabled=false
            var timer= Timer()
            timer.schedule(counter(btn1,timer).createTimerTask(), 0, 100)
        }
        findViewById<Button>(R.id.btn33).setOnClickListener {
            startForegroundService(Intent(applicationContext, recursiveTimerService::class.java))
        }
        findViewById<Button>(R.id.btn44).setOnClickListener {
            startForegroundService(Intent(applicationContext, accumulateTimerService::class.java))
        }
    }
}