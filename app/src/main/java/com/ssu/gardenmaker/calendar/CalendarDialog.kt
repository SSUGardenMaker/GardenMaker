package com.ssu.gardenmaker.calendar

import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.widget.ListView
import androidx.annotation.RequiresApi
import com.ssu.gardenmaker.databinding.DialogCalendarBinding
import com.ssu.gardenmaker.db.ContractDB
import com.ssu.gardenmaker.db.MyDBHelper
import java.text.SimpleDateFormat

@RequiresApi(Build.VERSION_CODES.P)
class CalendarDialog(context: Context, aContext: Context, layoutInflater: LayoutInflater) {

    private val dialog = Dialog(context)
    private val binding = DialogCalendarBinding.inflate(layoutInflater)
    private val currentTime=System.currentTimeMillis()

    private val arraylist:ArrayList<ListCalendarAdapter.ListCalendarDB> by lazy { ArrayList() }

    private lateinit var db: SQLiteDatabase
    private val adapter by lazy { ListCalendarAdapter(aContext,arraylist) }
    private val dbHelper by lazy { MyDBHelper(aContext) }
    private val list: ListView by lazy { binding.dialogCalendarListview }

    fun showDialog() {
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        db=dbHelper.readableDatabase
        list.adapter=adapter

        val db=dbHelper.writableDatabase
        db.execSQL(ContractDB.insertCalendarTB("개나리","건강","만보기",20220301,20220601))
        db.execSQL(ContractDB.insertCalendarTB("개나리","건강","만보기",20220420,20220701))
        db.execSQL(ContractDB.insertCalendarTB("개나리","건강","만보기",20220515,20221211))

        // 현재 날짜에 맞는 데이터 출력(현재날짜 정보를 가진 static data를 하나 만들까?)
        val cur_day= SimpleDateFormat("dd").format(currentTime).toInt()
        val cur_month= SimpleDateFormat("MM").format(currentTime).toInt()
        val str_day= if(cur_day<10) "0$cur_day" else "$cur_day"
        val str_month=if(cur_month<10) "0${cur_month}" else "${cur_month}"
        selectSqlCalendarDB(ContractDB.selectDateCalendarTB("${SimpleDateFormat("yyyy").format(currentTime).toString()}$str_month$str_day".toInt()))

        //캘린더 클릭 리스너(month는 month-1, year & day는 정상출력)
        binding.dialogCalendarCalendar.setOnDateChangeListener { view, year, month, day ->
            arraylist.clear()
            val str_calendar_day= if(day<10) "0$day" else "$day"
            val str_calendar_month=if(month+1<10) "0${month+1}" else "${month+1}"

            selectSqlCalendarDB(ContractDB.selectDateCalendarTB("$year$str_calendar_month$str_calendar_day".toInt()))
            adapter.notifyDataSetChanged()
        }
    }

    private fun selectSqlCalendarDB(sql: String) {
        val cursor=db.rawQuery(sql,null)
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
/*
var db=dbHelper.writableDatabase
db.execSQL(ContractDB.insertCalendarTB("개나리","건강","만보기",20220301,20220601))
db.execSQL(ContractDB.insertCalendarTB("개나리","건강","만보기",20220801,20221101))
db.execSQL(ContractDB.insertCalendarTB("개나리","건강","만보기",20220420,20220701))
db.execSQL(ContractDB.insertCalendarTB("개나리","건강","만보기",20220515,20221211))
db.execSQL(ContractDB.insertCalendarTB("개나리","건강","만보기",20220611,20220901))
db.execSQL(ContractDB.insertCalendarTB("개나리","건강","만보기",20220721,20220809))
*/