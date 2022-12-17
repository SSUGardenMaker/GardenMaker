package com.ssu.gardenmaker.runnable

import android.os.Handler
import android.os.Message
import android.util.Log
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.retrofit.callback.RetrofitPlantCallback
import com.ssu.gardenmaker.retrofit.plant.PlantDataContent

class CalendarRunnable(garden_id:Int,plants:ArrayList<PlantDataContent>, handler: Handler):Runnable{
    var handler=handler
    var plants=plants
    var garden_id=garden_id
    var handlerMesaage=handler.obtainMessage()


    override fun run() {
       // Log.d("Handler","(전)플랜츠 사이즈(${garden_id}):${plants.size}")
        ApplicationClass.retrofitManager.plantGardenCheck(garden_id, object :
            RetrofitPlantCallback {
            override fun onError(t: Throwable) {

            }
            override fun onSuccess(message: String, data: List<PlantDataContent>) {
                for(i in 0..data.size-1){
                    if(data.get(i).isComplete==false){
                        data.get(i).context1=data.get(i).context1.replace("-","")
                        data.get(i).context2=data.get(i).context2.replace("-","")
                        plants.add(data.get(i))
                    }
                }
                handlerMesaage.arg1=100
                handler.sendMessage(handlerMesaage)
            }

            override fun onFailure(errorMessage: String, errorCode: Int) {

            }
        })

    }

}