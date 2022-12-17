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

    //현재시각
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
        // 현재 날짜에 맞는 데이터 출력(현재날짜 정보를 가진 static data를 하나 만들까?)
        val cur_day= SimpleDateFormat("dd").format(currentTime).toInt()
        val cur_month= SimpleDateFormat("MM").format(currentTime).toInt()

    }


    private fun init(){
        //Dialog 초기값
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.show()

        //CalendarView 초기값
        calendar.addDecorators(FontDecorator(mContext),SaturdayDecorator(),SundayDecorator(),TodayDecorator(mContext))

        //Spinner 초기값
        var gardenNameList=ArrayList<String>()
        for(i in gardenitemList)
            gardenNameList.add(i.garden_name)


        var spinnerAdapter=ArrayAdapter(mContext,android.R.layout.simple_spinner_dropdown_item,gardenNameList)
        binding.dialogCalendarSpinner.adapter=spinnerAdapter

        //ListView 초기값
        list.adapter=adapter
    }


    private fun listener(){
        //Spinner Listener
        binding.dialogCalendarSpinner.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                arraylist.clear()
                for(i in gardenitemList.get(p2).plants){
                    arraylist.add(ListCalendarAdapter.calendarAdapterList(i.name,gardenitemList.get(p2).garden_name,i.plantType,i.startDate.replace("-","").toInt(),i.endDate.replace("-","").toInt()))
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

        //date는 CalendarDay{2022-10-23}와 같이 출력. month-1 출력, Year과 Day는 정상출력 {2022-1-9}
        calendar.setOnDateChangedListener{widget,date,selected ->
            var copyArraylist=ArrayList<ListCalendarAdapter.calendarAdapterList>()

            var str=date.toString().split("-")
            var str_month=if(str[1].toInt()>8) (str[1].toInt()+1).toString() else "0${(str[1].toInt()+1).toString()}"
            var str_day=str[2].substring(0,str[2].length-1)

            var cal_day="${str[0].substring(12,str[0].length)}${str_month}${if(str_day.length==2) str_day else "0${str_day}"}"
            caldayClick=cal_day

            for(i in arraylist){
              if(i.start_day<=cal_day.toInt()&&i.end_day>=cal_day.toInt()){
                  Log.d(TAG,"시작일:${i.start_day}종료일:${i.start_day}캘린더:${cal_day}")
                  copyArraylist.add(i)
              }
            }
            adapter.notifyChanged(copyArraylist)
        }
    }



    class Garden_item(var garden_id:Int,var garden_name:String, var plants:ArrayList<PlantDataContent>)
}



// db에 데이터 저장용으로 필요시 사용하기
/*var db=dbHelper.writableDatabase
db.execSQL(ContractDB.insertCalendarTB("개나리","건강","만보기",20220301,20220601))
* */