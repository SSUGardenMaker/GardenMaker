package com.ssu.gardenmaker.features.pedometer

import android.app.NotificationChannel
import android.app.NotificationManager
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


@RequiresApi(Build.VERSION_CODES.O)
class PedometerService :Service(),SensorEventListener{
    val TAG="PedometerSevice"
    //Sensor
    val sensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager}
    val stepCountSensor by lazy{sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) }
    //Notification
    val mNotificationManager by lazy{getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager}
    val channel by lazy{NotificationChannel("pedometer_channel", "만보기", NotificationManager.IMPORTANCE_DEFAULT)}
    val notification by lazy { NotificationCompat.Builder(applicationContext,"pedometer_channel")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("0걸음")
        .setContentText("목표 걸음 수는 12,000입니다.") }
    //Thread
     val t1 by lazy { Thread{
         try{
             while(!Thread.currentThread().isInterrupted){
                 Thread.sleep(10000)
                 notification.setContentTitle(pedometer_count.toString()+" 걸음")
                 mNotificationManager.notify(1,notification.build())
             }
         }catch (e:java.lang.Exception){

         }
     } }
    //걸음수
    var pedometer_count=0

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        sensorManager.registerListener(this,stepCountSensor,SensorManager.SENSOR_DELAY_FASTEST)

        mNotificationManager.createNotificationChannel(channel)
        mNotificationManager.notify(1,notification.build())
        startForeground(1,notification.build())

        t1.start()
    }



    override fun onDestroy() {
       if(t1!=null &&t1.isAlive){
           t1.interrupt()
       }
    }

    override fun onSensorChanged(p0: SensorEvent?) {    //실행 메커니즘 노이해..
      Log.d(TAG,"실행3")
        if(p0?.sensor?.type==Sensor.TYPE_STEP_COUNTER){
            Log.d(TAG,"실행4")
            pedometer_count+=2
         if(p0?.values!![0]==1.0f){
             Log.d(TAG,"실행5")
            // pedometer_count+=2
         }
      }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}