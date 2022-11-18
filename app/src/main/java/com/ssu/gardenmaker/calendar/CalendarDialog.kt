package com.ssu.gardenmaker.calendar

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.ListView
import androidx.annotation.RequiresApi
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.calendar.decorator.FontDecorator
import com.ssu.gardenmaker.calendar.decorator.SaturdayDecorator
import com.ssu.gardenmaker.calendar.decorator.SundayDecorator
import com.ssu.gardenmaker.calendar.decorator.TodayDecorator
import com.ssu.gardenmaker.databinding.DialogCalendarBinding
import com.ssu.gardenmaker.db.ContractDB
import java.text.SimpleDateFormat
import kotlin.String as String

//params?.width=1200 값의 의미는
@RequiresApi(Build.VERSION_CODES.P)
class CalendarDialog(context: Context, aContext: Context, layoutInflater: LayoutInflater) {
    private val TAG="CalendarDialog"
    private val context=context
    private val dialog = Dialog(context)
    private val binding = DialogCalendarBinding.inflate(layoutInflater)
    private val currentTime = System.currentTimeMillis()
    private val arraylist: ArrayList<ListCalendarAdapter.ListCalendarDB> by lazy { ArrayList() }
    private val adapter by lazy { ListCalendarAdapter(aContext,arraylist) }
    private val list: ListView by lazy { binding.dialogCalendarListview }
    private val calendar by lazy{binding.dialogCalendarCalendar}
    fun showDialog() {
        init()
        listener()

        // 현재 날짜에 맞는 데이터 출력(현재날짜 정보를 가진 static data를 하나 만들까?)
        val cur_day= SimpleDateFormat("dd").format(currentTime).toInt()
        val cur_month= SimpleDateFormat("MM").format(currentTime).toInt()
        selectSqlCalendarDB(ContractDB.selectDateCalendarTB((
                "${SimpleDateFormat("yyyy").format(currentTime).toString()}"+ //년도
                    "${if(cur_month<10) "0${cur_month}" else "${cur_month}"}"+       //월
                    "${if(cur_day<10) "0$cur_day" else "$cur_day"}").toInt()))       //일

    }

    private fun init(){
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var params=dialog.window?.attributes
        params?.width=1200
        dialog.window?.attributes=params as android.view.WindowManager.LayoutParams

        calendar.addDecorators(FontDecorator(context),SaturdayDecorator(),SundayDecorator(),TodayDecorator(context))

        list.adapter=adapter

        dialog.show()
    }

    private fun listener(){
        //date는 CalendarDay{2022-10-23}와 같이 출력. month-1 출력, Year과 Day는 정상출력
        calendar.setOnDateChangedListener{widget,date,selected ->
            arraylist.clear()
            var str=date.toString().split("-")
            var str_day=str[2].substring(0,str[2].length-1).toInt()
            var str_month=str[1].toInt()

            selectSqlCalendarDB(ContractDB.selectDateCalendarTB((
                    "${str[0].substring(str[0].length-4)}" + //년도
                            "${if(str_month+1<10) "0${str_month+1}" else "${str_month+1}"}" +//월
                            "${if(str_day<10) "0$str_day" else "$str_day"}").toInt())) //날짜
            adapter.notifyDataSetChanged()
        }
    }

    private fun selectSqlCalendarDB(sql: String) {
        val cursor=ApplicationClass.db.rawQuery(sql,null)
        while (cursor.moveToNext()) {
            arraylist.add(
                ListCalendarAdapter.ListCalendarDB(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getInt(4)
                )
            )
        }
    }
}



// db에 데이터 저장용으로 필요시 사용하기
/*var db=dbHelper.writableDatabase
db.execSQL(ContractDB.insertCalendarTB("개나리","건강","만보기",20220301,20220601))
* */