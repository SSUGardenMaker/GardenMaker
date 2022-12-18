package com.ssu.gardenmaker.features.recursiveTimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.ui.GardenActivity
import java.util.*
//버그 1. 5번 클릭해야 그제야 실행되는 경우 발생. 1~4번은 바로 ondestroy()함수가 호출됨
class recursiveTimerService :Service() {
    val TAG="recursiveTimerService"
    init {
       ApplicationClass.mSharedPreferences.edit().putBoolean("startTimer_FLAG",false).commit()
    }
    //Plant API관련
    var INIT_ACTIVITY_INTENT_FLAG:Boolean=false
    var plant_name:String=""; var plant_id:Int=0                            //plant관련 정보
    var recursive_second:Int=0; var cur_accumulate_millisecond:Long=0      //타이머를 통해 저장할 시간


    //noti 변수
    lateinit var builder: NotificationCompat.Builder
    lateinit var manager: NotificationManager

    //TimerThread 변수
    lateinit var timer: Timer
    lateinit var timerTask: TimerTask

    //PendingIntent 변수
    lateinit var intent1: Intent
    lateinit var pendingIntent1: PendingIntent
    lateinit var action1: NotificationCompat.Action
    lateinit var intent2: Intent
    lateinit var pendingIntent2: PendingIntent
    lateinit var action2: NotificationCompat.Action
    lateinit var intent3: Intent
    lateinit var pendingIntent3: PendingIntent
    lateinit var action3: NotificationCompat.Action

    override fun onCreate() {  Log.d(TAG,"onCreate")
        init_PendingIntent()
        init_noti(action1,action2)

        //Noti Channel 등록
        manager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            manager.createNotificationChannel(NotificationChannel("default", "gardenmaker_timer_name", NotificationManager.IMPORTANCE_LOW))

        //Service시작
        startForeground(1,builder.build())
        timerTask=createTimerTask()
        timer= Timer()
        timer.schedule(timerTask,0,500)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(!INIT_ACTIVITY_INTENT_FLAG){
            plant_name=intent?.getStringExtra("plantName")!!
            plant_id=intent?.getIntExtra("plantId",-1)!!
            recursive_second=intent?.getIntExtra("timerRecurMin",-1)!!*60
            cur_accumulate_millisecond=recursive_second.toLong()*1000
            Log.d(TAG,"${recursive_second}  ${cur_accumulate_millisecond}")
            INIT_ACTIVITY_INTENT_FLAG=true
        }

        when(intent?.getIntExtra("timer_Notisignal",0)){
            0 -> { Log.d(TAG,"Intent값 없음")
            }
            101-> { Log.d(TAG,"취소")
                timer.cancel()
                stopSelf()
            }
            102 -> { Log.d(TAG,"일시정지")
                timer.cancel()  //일시정지
                init_noti(action1,action3)
                manager.notify(1,builder.build())
            }
            103->{ Log.d(TAG,"재시작")
                init_noti(action1,action2)
                timerTask=createTimerTask()
                timer= Timer()
                timer.schedule(timerTask,0,500)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() { Log.d(TAG,"onDestroy")
        timer.cancel()
        ApplicationClass.mSharedPreferences.edit().putBoolean("startTimer_FLAG",true).commit()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? { Log.d(TAG,"onBind")
        return null
    }
    fun createTimerTask(): TimerTask {
        return object : TimerTask() {
            override fun run() {
                cur_accumulate_millisecond-=1000
                recursive_second= (cur_accumulate_millisecond/1000).toInt()
                if(recursive_second>0){
                    builder.setContentText("${"%02d".format(recursive_second/3600)}:${"%02d".format(recursive_second/60-recursive_second/3600*60)}:${"%02d".format(recursive_second%60)}(${plant_name})")
                    manager.notify(1,builder.build())
                }else{
                    add_Count()
                    stopSelf()
                }
            }
        }
    }

    fun init_PendingIntent(){
        //PendingIntent 정의
        intent1= Intent(this,recursiveTimerService::class.java)
        intent1.putExtra("timer_Notisignal",101)
        pendingIntent1= PendingIntent.getService(this,10,intent1, PendingIntent.FLAG_MUTABLE)
        action1= NotificationCompat.Action.Builder(0,"취소",pendingIntent1).build()

        intent2= Intent(this,recursiveTimerService::class.java)
        intent2.putExtra("timer_Notisignal",102)
        pendingIntent2= PendingIntent.getService(this,20,intent2, PendingIntent.FLAG_MUTABLE)
        action2= NotificationCompat.Action.Builder(0,"일시정지",pendingIntent2).build()

        intent3= Intent(this,recursiveTimerService::class.java)
        intent3.putExtra("timer_Notisignal",103)
        pendingIntent3= PendingIntent.getService(this,30,intent3, PendingIntent.FLAG_MUTABLE)
        action3= NotificationCompat.Action.Builder(0,"재시작",pendingIntent3).build()
    }

    fun init_noti(P_action1: NotificationCompat.Action, P_action2: NotificationCompat.Action){
        builder= NotificationCompat.Builder(this,"default")
        builder.setSmallIcon(R.drawable.ic_launcher_foreground) //없으면 에러 발생
        builder.setContentTitle("반복 타이머")
        builder.setAutoCancel(true)
        builder.setContentText("${"%02d".format(recursive_second/3600)}:${"%02d".format(recursive_second/60-recursive_second/3600*60)}:${"%02d".format(recursive_second%60)}(${plant_name})")
        builder.setAutoCancel(true)
        builder.addAction(P_action1)
        builder.addAction(P_action2)
    }
    fun add_Count(){
        Log.d(TAG,"add_count호출")
        ApplicationClass.retrofitManager.plantWatering(plant_id,object :
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
    }
}