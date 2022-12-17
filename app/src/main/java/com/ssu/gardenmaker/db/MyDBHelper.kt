package com.ssu.gardenmaker.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)
class MyDBHelper(context: Context?): SQLiteOpenHelper(context, ContractDB.DB_NAME,null, ContractDB.DB_VERSION) {

    // DB가 생성되는 최초의 한번만 실행된다. 즉 DB가 생성되어 있다면, 해당 메소드는 실행되지 않는다.
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(ContractDB.CREATE_Calendar_TB)
        db?.execSQL(ContractDB.CREATE_Checkbox_TB)
    }

    // SQLiteOpenHelper class를 호출할때마다 실행되는 메소드이다.
    override fun onOpen(db: SQLiteDatabase?) {

    }

    // SQLiteDatabase 파일의 버전과 다를 경우에 자동으로 호출
    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {

    }
}