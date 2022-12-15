package com.ssu.gardenmaker.features.counter

import android.opengl.Visibility
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback

import java.util.*
/*버그: 메인 스레드에서 파생된 스레드라 액티비티 나가면 시간을 재주는 스레드도 같이 종료. 즉, 가든->메인화면->시간재주지 않음*/
class counter(plantId:Int,remaining_time:Long,var tv: TextView,timer: Timer) {
    var TAG="counter"
    var cur_millisecond:Long=remaining_time
    var timer=timer
    init{
        ApplicationClass.retrofitManager.plantWatering(plantId,object :
            RetrofitCallback {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError : " + t.localizedMessage)
            }

            override fun onSuccess(message: String, data: String) {
                Log.d(TAG, "onSuccess : message -> $message")
                Log.d(TAG, "onSuccess : data -> $data")

            }

            override fun onFailure(errorMessage: String, errorCode: Int) {
                Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                Log.d(TAG, "onFailure : errorCode -> $errorCode")
            }
        })
       tv.visibility=View.VISIBLE
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
                tv.visibility= View.GONE
            }else{
                tv.text="${"%02d".format(cur_millisecond/1000/60)}:${"%02d".format(cur_millisecond/1000%60)}"
            }
        }
    }
}