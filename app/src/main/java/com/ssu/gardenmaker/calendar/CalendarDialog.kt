package com.ssu.gardenmaker.calendar

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.calendar.decorator.FontDecorator
import com.ssu.gardenmaker.calendar.decorator.SaturdayDecorator
import com.ssu.gardenmaker.calendar.decorator.SundayDecorator
import com.ssu.gardenmaker.calendar.decorator.TodayDecorator
import com.ssu.gardenmaker.databinding.DialogCalendarBinding
import com.ssu.gardenmaker.db.ContractDB
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.retrofit.callback.RetrofitPlantCallback
import com.ssu.gardenmaker.retrofit.plant.PlantDataContent
import com.ssu.gardenmaker.retrofit.plant.RequestPlantCreate
import java.text.SimpleDateFormat
import kotlin.String as String

//
@RequiresApi(Build.VERSION_CODES.P)
class CalendarDialog(context: Context, aContext: Context, layoutInflater: LayoutInflater,gardenitemList: ArrayList<Garden_item>) {
    //Components
    private val TAG="CalendarDialog"
    private val mContext=context
    private val binding = DialogCalendarBinding.inflate(layoutInflater)

    //íėŽėę°
    private val currentTime = System.currentTimeMillis()

    //dialog
    private val dialog = Dialog(context)

    //ListView, Adapter
    private val list: ListView by lazy { binding.dialogCalendarListview }
    private val adapter by lazy { ListCalendarAdapter(aContext,arraylist) }
    private var arraylist = ArrayList<ListCalendarAdapter.calendarAdapterList> ()

    //CalendarView
    private val calendar by lazy{binding.dialogCalendarCalendar}
    private var caldayClick:String?=null
    //
    private var gardenitemList=gardenitemList


    fun showDialog() {
        init()
        listener()
        // íėŽ ë ė§ė ë§ë ë°ėīí° ėķë Ĩ(íėŽë ė§ ė ëģīëĨž ę°ė§ static dataëĨž íë ë§ëĪęđ?)
        val cur_day= SimpleDateFormat("dd").format(currentTime).toInt()
        val cur_month= SimpleDateFormat("MM").format(currentTime).toInt()

    }


    private fun init(){
        //Dialog ėīęļ°ę°
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.show()

        //CalendarView ėīęļ°ę°
        calendar.addDecorators(FontDecorator(mContext),SaturdayDecorator(),SundayDecorator(),TodayDecorator(mContext))

        //Spinner ėīęļ°ę°
        var gardenNameList=ArrayList<String>()
        for(i in gardenitemList)
            gardenNameList.add(i.garden_name)


        var spinnerAdapter=ArrayAdapter(mContext,android.R.layout.simple_spinner_dropdown_item,gardenNameList)
        binding.dialogCalendarSpinner.adapter=spinnerAdapter

        //ListView ėīęļ°ę°
        list.adapter=adapter
    }


    private fun listener(){
        //Spinner Listener
        binding.dialogCalendarSpinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                arraylist.clear()
                for(i in gardenitemList.get(p2).plants){
                    arraylist.add(ListCalendarAdapter.calendarAdapterList(i.name,gardenitemList.get(p2).garden_name,categorySwitch(i.plantType),i.context1.toInt(),i.context2.toInt()))
                }
                if(caldayClick!=null){
                    var copyArraylist=ArrayList<ListCalendarAdapter.calendarAdapterList>()
                    for(i in arraylist){
                        if(i.start_day<=caldayClick!!.toInt()&&i.end_day>=caldayClick!!.toInt())
                            copyArraylist.add(i)
                    }
                    adapter.notifyChanged(copyArraylist)
                }else{
                    adapter.notifyChanged(arraylist)
                }

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        //dateë CalendarDay{2022-10-23}ė ę°ėī ėķë Ĩ. month-1 ėķë Ĩ, Yearęģž Dayë ė ėėķë Ĩ {2022-1-9}
        calendar.setOnDateChangedListener{widget,date,selected ->
            var copyArraylist=ArrayList<ListCalendarAdapter.calendarAdapterList>()

            var str=date.toString().split("-")
            var str_month=if(str[1].toInt()>8) (str[1].toInt()+1).toString() else "0${(str[1].toInt()+1).toString()}"
            var str_day=str[2].substring(0,str[2].length-1)

            var cal_day="${str[0].substring(12,str[0].length)}${str_month}${if(str_day.length==2) str_day else "0${str_day}"}"
            caldayClick=cal_day

            for(i in arraylist){
              if(i.start_day<=cal_day.toInt()&&i.end_day>=cal_day.toInt()){
                  copyArraylist.add(i)
              }
            }
            adapter.notifyChanged(copyArraylist)
        }
    }

    fun categorySwitch(str:String):String{
        when(str){
            "ACCUMULATE_TIMER"-> return "ëė  íėīëĻļ"
            "CHECKBOX"-> return "ėēīíŽë°ėĪ"
            "COUNTER"-> return "íė"
            "RECURSIVE_TIMER" -> return "ë°ëģĩ íėīëĻļ"
            "WALK_COUNTER" -> return "ë§ëģīęģ"
        }
        return ""
    }

    class Garden_item(var garden_id:Int,var garden_name:String, var plants:ArrayList<PlantDataContent>)
}



// dbė ë°ėīí° ė ėĨėĐėžëĄ íėė ėŽėĐíęļ°
/*var db=dbHelper.writableDatabase
db.execSQL(ContractDB.insertCalendarTB("ę°ëëĶŽ","ęąīę°","ë§ëģīęļ°",20220301,20220601))
* */