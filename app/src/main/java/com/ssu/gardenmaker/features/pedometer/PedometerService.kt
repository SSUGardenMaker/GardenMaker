package com.ssu.gardenmaker.features.pedometer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ssu.gardenmaker.R
import okhttp3.internal.notify
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class PedometerService :Service(),SensorEventListener{
    val TAG="PedometerSevice"
    var INIT_ACTIVITY_INTENT_FLAG:Boolean=false
    var plant_id=0
    var walkStep=0

    //Sensor
    val sensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager}
    val stepCountSensor by lazy{sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) }


    //Notification
    lateinit var builder: NotificationCompat.Builder; lateinit var manager: NotificationManager

    //Timer Thread
    lateinit var timer: Timer; lateinit var timerTask: TimerTask

    //PendingIntent정의
    lateinit var intent1: Intent; lateinit var pendingIntent1: PendingIntent; lateinit var action1: NotificationCompat.Action



    //걸음수
    var pedometer_count=0

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        sensorManager.registerListener(this,stepCountSensor,SensorManager.SENSOR_DELAY_FASTEST)

        init_PendingIntent()
        init_noti(action1)

        manager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            manager.createNotificationChannel(NotificationChannel("pedometer", "pedometer", NotificationManager.IMPORTANCE_LOW))
        startForeground(37,builder.build())

        timerTask=object : TimerTask() {
            override fun run() {
                Log.d("만보기","스레드")
                builder.setContentTitle(pedometer_count.toString()+" 걸음")
                manager.notify(37,builder.build())
            }
        }
        timer= Timer()
        timer.schedule(timerTask,0,5000)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("만보기","onStartCommand")

       /* if(!INIT_ACTIVITY_INTENT_FLAG){
            walkStep=intent?.getIntExtra("walkStep",0)!!
            plant_id=intent?.getIntExtra("plantId",-1)!!
            INIT_ACTIVITY_INTENT_FLAG=true
        }*/

        Log.d("만보기","id:${plant_id} 목표걸음:${walkStep}")
        Log.d("만보기","${intent?.getIntExtra("pedometer_Notisignal",0)}")

        when(intent?.getIntExtra("timer_Notisignal",0)){
            101-> { Log.d(TAG,"취소")
                stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("만보기","onDestroy")
        timer.cancel()
    }

    fun init_PendingIntent(){
        intent1= Intent(this,PedometerService::class.java)
        intent1.putExtra("pedometer_Notisignal",101)
        pendingIntent1= PendingIntent.getService(this,11,intent1, PendingIntent.FLAG_MUTABLE)
        action1= NotificationCompat.Action.Builder(0,"취소",pendingIntent1).build()
    }

    fun init_noti(P_action1: NotificationCompat.Action){
        builder= NotificationCompat.Builder(this,"default")
        builder.setSmallIcon(R.drawable.ic_launcher_foreground) //없으면 에러 발생
        builder.setContentTitle("0걸음")
        builder.setContentText("목표 걸음 수는 12,000입니다.")
        builder.setAutoCancel(true)
        builder.setAutoCancel(true)
        builder.addAction(P_action1)
    }


    override fun onSensorChanged(p0: SensorEvent?) {    //실행 메커니즘 노이해..
      Log.d("만보기","실행3")
        if(p0?.sensor?.type==Sensor.TYPE_STEP_COUNTER){
            Log.d("만보기","실행4")
            pedometer_count+=2
         if(p0?.values!![0]==1.0f){
             Log.d("만보기","실행5")
            // pedometer_count+=2
         }
      }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}