package com.ssu.gardenmaker.features

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.features.recursiveTimer.recursiveTimerService
import java.util.*


class ExFeatureActivity : AppCompatActivity() {
    val btn1 by lazy { findViewById<Button>(R.id.btn22) }
    lateinit var timer: Timer
    lateinit var mhandler:TimerHandler

    var Goal_minutes=1
    var cur_millisecond:Long=0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //횟수
        mhandler=TimerHandler()
        cur_millisecond=MinToMillisecond(Goal_minutes)

        setContentView(R.layout.activity_ex_feature)

        findViewById<Button>(R.id.btn11).setOnClickListener {

        }
        findViewById<Button>(R.id.btn22).setOnClickListener {
            btn1.isEnabled=false
            timer= Timer()
            timer.schedule(createTimerTask1(), 0, 100)
        }
        findViewById<Button>(R.id.btn33).setOnClickListener {
            startForegroundService(Intent(applicationContext, recursiveTimerService::class.java))
        }
        findViewById<Button>(R.id.btn44).setOnClickListener {

        }
    }
    //횟수
    fun MinToMillisecond(min:Int):Long{
        return min*60*1000L
    }
    fun createTimerTask1():TimerTask{
        return object : TimerTask() {
            override fun run() {
                cur_millisecond-=1000
                mhandler.sendEmptyMessage(1)
            }
        }
    }

    inner class TimerHandler: Handler(){
        override fun handleMessage(msg: Message) {
            if(cur_millisecond<=0){
                timer.cancel()
                btn1.isEnabled=true
                btn1.text="횟수(5분간격)"
                cur_millisecond=MinToMillisecond(Goal_minutes) //재시작을 위해 목표시간 다시 부여
            }else{
                btn1.text="${"%02d".format(cur_millisecond/1000/60)}:${"%02d".format(cur_millisecond/1000%60)}"
            }
        }
    }
}