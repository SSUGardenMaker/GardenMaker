package com.ssu.gardenmaker.features.accumulateTimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.ui.GardenActivity
import java.util.*
//plant/{plantId}/waterAcc API는 인자로 넣어준 시간을 그대롤 저장하는 것이 아니라, 기존에 저장되어 있는 timerCurrentMin과 합쳐준다. 그러니까 더해줄 값을 인자로 주기.
//버그 1. 5번 클릭해야 그제야 실행되는 경우 발생. 1~4번은 바로 ondestroy()함수가 호출됨
class accumulateTimerService: Service() {
    init {
        ApplicationClass.mSharedPreferences.edit().putBoolean("startTimer_FLAG",false).commit()
    }
    val TAG="accumulateTimerService"

    //Plant API관련
    var INIT_ACTIVITY_INTENT_FLAG:Boolean=false
    var plant_name:String=""; var plant_id:Int=0                         //plant관련 정보
    var goal_second:Int=0                                                //목표 시간
    var cur_second:Int=0; var cur_accumulate_millisecond:Long=0          //타이머를 통해 저장할 시간
    var server_timerCurrentMin:Int=0                                     //서버에 현재 저장된 시간

    //Notification 정보들
    lateinit var builder: NotificationCompat.Builder; lateinit var manager: NotificationManager

    //Timer Thread
    lateinit var timer: Timer; lateinit var timerTask: TimerTask

    //PendingIntent정의
    lateinit var intent1: Intent; lateinit var pendingIntent1: PendingIntent; lateinit var action1: NotificationCompat.Action
    lateinit var intent2: Intent; lateinit var pendingIntent2: PendingIntent; lateinit var action2: NotificationCompat.Action
    lateinit var intent3: Intent; lateinit var pendingIntent3: PendingIntent; lateinit var action3: NotificationCompat.Action


    override fun onCreate() {  Log.d(TAG,"onCreate")
        init_PendingIntent()
        init_noti(action1,action2)
        //Noti Channel 등록
        manager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            manager.createNotificationChannel(NotificationChannel("default", "gardenmaker_timer_name", NotificationManager.IMPORTANCE_LOW))

        startForeground(1,builder.build())
        timerTask=createTimerTask()
        timer= Timer()
        timer.schedule(timerTask,0,1000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStratCommand,intent:${intent?.getIntExtra("timer_Notisignal",0)}")
        if(!INIT_ACTIVITY_INTENT_FLAG){
            plant_name=intent?.getStringExtra("plantName")!!
            plant_id=intent?.getIntExtra("plantId",-1)!!
            goal_second=intent?.getIntExtra("timerTotalMin",-1)!!*60
            server_timerCurrentMin=intent?.getIntExtra("timerCurMin",-1)!!
            cur_second=server_timerCurrentMin*60
            cur_accumulate_millisecond=cur_second.toLong()*1000

            INIT_ACTIVITY_INTENT_FLAG=true
        }

        when(intent?.getIntExtra("timer_Notisignal",0)){
            0 -> { Log.d(TAG,"Intent값 없음")
            }
            101-> { Log.d(TAG,"취소")
                store_timerCurMin()
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
                timer.schedule(timerTask,0,1000)
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
                cur_accumulate_millisecond+=1000
                cur_second= (cur_accumulate_millisecond/1000).toInt()

                if(cur_second<goal_second){ //목표시간 못채웠을 경우: 계속 진행
                    builder.setContentText("${"%02d".format(cur_second/3600)}:${"%02d".format(cur_second/60-cur_second/3600*60)}:${"%02d".format(cur_second%60)}(${plant_name})")
                    manager.notify(1,builder.build())
                }else{  //목표시간 채웠을 경우: 꽃 완료로 바꾸고, Service종료하기
                    store_timerCurMin()
                    stopSelf()
                }
            }
        }
    }

    fun init_PendingIntent(){
        //PendingIntent 정의
        intent1= Intent(this,accumulateTimerService::class.java)
        intent1.putExtra("timer_Notisignal",101)
        pendingIntent1= PendingIntent.getService(this,11,intent1, PendingIntent.FLAG_MUTABLE)
        action1= NotificationCompat.Action.Builder(0,"취소",pendingIntent1).build()

        intent2= Intent(this,accumulateTimerService::class.java)
        intent2.putExtra("timer_Notisignal",102)
        pendingIntent2= PendingIntent.getService(this,21,intent2, PendingIntent.FLAG_MUTABLE)
        action2= NotificationCompat.Action.Builder(0,"일시정지",pendingIntent2).build()

        intent3= Intent(this,accumulateTimerService::class.java)
        intent3.putExtra("timer_Notisignal",103)
        pendingIntent3= PendingIntent.getService(this,31,intent3, PendingIntent.FLAG_MUTABLE)
        action3= NotificationCompat.Action.Builder(0,"재시작",pendingIntent3).build()
    }

    fun init_noti(P_action1: NotificationCompat.Action, P_action2: NotificationCompat.Action){
        builder= NotificationCompat.Builder(this,"default")
        builder.setSmallIcon(R.drawable.ic_launcher_foreground) //없으면 에러 발생
        builder.setContentTitle("누적 타이머")
        builder.setAutoCancel(true)
        builder.setContentText("${"%02d".format(cur_second/3600)}:${"%02d".format(cur_second/60-cur_second/3600*60)}:${"%02d".format(cur_second%60)}(${plant_name})")
        builder.setAutoCancel(true)
        builder.addAction(P_action1)
        builder.addAction(P_action2)
    }

    fun store_timerCurMin(){
        Log.d(TAG,"저장시간:${cur_second/60-server_timerCurrentMin}")
        ApplicationClass.retrofitManager.plantWateringAcc(plant_id, cur_second/60-server_timerCurrentMin, object : RetrofitCallback {
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