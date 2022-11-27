package com.ssu.gardenmaker.features.accumulateTimer

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
import com.ssu.gardenmaker.R
import java.util.*

class accumulateTimerService: Service() {
    val TAG="accumulateTimerService"

    var cur_accumulate_millisecond:Long=3540*1000L
    var plant_name:String="개나리"
    var cur_second:Int=0

    lateinit var builder: NotificationCompat.Builder
    lateinit var manager: NotificationManager

    lateinit var timer: Timer
    lateinit var timerTask: TimerTask

    //PendingIntent정의

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

        startForeground(1,builder.build())
        timerTask=createTimerTask()
        timer= Timer()
        timer.schedule(timerTask,0,1000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStratCommand,intent:${intent?.getIntExtra("timer_Notisignal",0)}")
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
                timer.schedule(timerTask,0,1000)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() { Log.d(TAG,"onDestroy")
        timer.cancel()
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
                builder.setContentText("${"%02d".format(cur_second/3600)}:${"%02d".format(cur_second/60-cur_second/3600*60)}:${"%02d".format(cur_second%60)}(${plant_name})")
                manager.notify(1,builder.build())
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
}