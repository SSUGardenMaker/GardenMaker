package com.ssu.gardenmaker.CalendarAndCheckList

import android.app.ActionBar.LayoutParams
import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.widget.CalendarView
import android.widget.ListView
import androidx.annotation.RequiresApi
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.databinding.DialogCalendarBinding
import com.ssu.gardenmaker.db.ContractDB
import com.ssu.gardenmaker.db.GardenMaker_DBHelper
import java.text.SimpleDateFormat


@RequiresApi(Build.VERSION_CODES.P)
class CalendarDialog(context: Context, acontext:Context, layoutInflater: LayoutInflater) {
    private val dialog = Dialog(context)
    private val binding = DialogCalendarBinding.inflate(layoutInflater)
    private val currentTime=System.currentTimeMillis()

    private val arraylist:ArrayList<List_Calendar_DB> by lazy{
        ArrayList<List_Calendar_DB>()
    }
    private lateinit var db: SQLiteDatabase
    private val adapter by lazy{ List_Calendar_Adpater(acontext,arraylist) }
    private val dbHelper by lazy{ GardenMaker_DBHelper(acontext) }
    private val list: ListView by lazy { binding.dialogCalendarListview }

    fun showDialog(){
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        db=dbHelper.readableDatabase
        list.adapter=adapter


        //현재 날짜에 맞는 데이터 출력(현재날짜 정보를 가진 static data를 하나 만들까?)
        var cur_day= SimpleDateFormat("dd").format(currentTime).toInt()
        var cur_month= SimpleDateFormat("MM").format(currentTime).toInt()
        var str_day= if(cur_day<10) "0$cur_day" else "$cur_day"
        var str_month=if(cur_month<10) "0${cur_month}" else "${cur_month}"
        SelectSql_CalendarDB(ContractDB.SELECT_DATE_Calendar_TB("${SimpleDateFormat("yyyy").format(currentTime).toString()}$str_month$str_day".toInt()))

        //캘린더 클릭 리스너(month는 month-1, year & day는 정상출력)
        binding.dialogCalendarCalendar.setOnDateChangeListener { view, year, month, day ->
            arraylist.clear()
            var str_day= if(day<10) "0$day" else "$day"
            var str_month=if(month+1<10) "0${month+1}" else "${month+1}"

            SelectSql_CalendarDB(ContractDB.SELECT_DATE_Calendar_TB("$year$str_month$str_day".toInt()))
            adapter.notifyDataSetChanged()
        }
    }
    fun SelectSql_CalendarDB(sql:String){
        var cursor=db.rawQuery(sql,null)
        while(cursor.moveToNext()){
            arraylist.add(List_Calendar_DB(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getInt(4)))
        }
    }
}

//db에 데이터 저장용으로 필요시 사용하기
/* var db=dbHelper.writableDatabase
        db.execSQL(ContractDB.INSERT_Calendar_TB("개나리","건강","만보기",20220301,20220601)) //
        db.execSQL(ContractDB.INSERT_Calendar_TB("개나리","건강","만보기",20220801,20221101))
        db.execSQL(ContractDB.INSERT_Calendar_TB("개나리","건강","만보기",20220420,20220701)) //
        db.execSQL(ContractDB.INSERT_Calendar_TB("개나리","건강","만보기",20220515,20221211))//00
        db.execSQL(ContractDB.INSERT_Calendar_TB("개나리","건강","만보기",20220611,20220901))//00
        db.execSQL(ContractDB.INSERT_Calendar_TB("개나리","건강","만보기",20220721,20220809))//00*/