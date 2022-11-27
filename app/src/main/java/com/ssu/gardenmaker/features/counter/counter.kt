package com.ssu.gardenmaker.features.counter

import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Button

import java.util.*

class counter(var btn: Button,timer: Timer) {
    var TAG="counter"
    var Goal_minutes=1
    var cur_millisecond:Long=Goal_minutes*60* 1000L
    var timer=timer
    init{
        Log.d(TAG,"1:"+cur_millisecond)
    }
   var mhandler=TimerHandler()


    fun createTimerTask():TimerTask{
        return object : TimerTask() {
            override fun run() {
                cur_millisecond-=1000
                Log.d(TAG,"2:"+cur_millisecond)
                mhandler.sendEmptyMessage(1)
            }
        }
    }

    //횟수
    fun MinToMillisecond(min:Int):Long{
        return min*60*1000L
    }

    inner class TimerHandler: Handler(){
        override fun handleMessage(msg: Message) {
            if(cur_millisecond<=0){     Log.d(TAG,"3:"+cur_millisecond)
                timer.cancel()
                btn.isEnabled=true
                btn.text="횟수(5분간격)"
                cur_millisecond=MinToMillisecond(Goal_minutes) //재시작을 위해 목표시간 다시 부여
            }else{
                btn.text="${"%02d".format(cur_millisecond/1000/60)}:${"%02d".format(cur_millisecond/1000%60)}"
            }
        }
    }
}