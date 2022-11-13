package com.ssu.gardenmaker.checkbox

import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.widget.ListView
import androidx.annotation.RequiresApi
import com.ssu.gardenmaker.databinding.DialogCheckboxBinding
import com.ssu.gardenmaker.db.ContractDB
import com.ssu.gardenmaker.db.MyDBHelper

@RequiresApi(Build.VERSION_CODES.P)
class CheckboxDialog(context: Context, aContext: Context, layoutInflater: LayoutInflater) {

    private val dialog = Dialog(context)
    private val binding = DialogCheckboxBinding.inflate(layoutInflater)

    private val arraylist: ArrayList<ListCheckboxAdapter.ListCheckboxDB> by lazy { ArrayList() }

    private lateinit var db: SQLiteDatabase
    private val adapter by lazy{ ListCheckboxAdapter(aContext, arraylist) }
    private val dbHelper by lazy{ MyDBHelper(aContext) }

    private val list: ListView by lazy { binding.dialogCheckboxListview }

    fun showDialog() {
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        db=dbHelper.readableDatabase
        list.adapter=adapter

        db=dbHelper.writableDatabase
        db.execSQL(ContractDB.insertCheckboxTB(1,"저녁먹기",20220301,20220301))

        val cursor=db.rawQuery(ContractDB.SELECTAll_Checkbox_TB,null)
        while (cursor.moveToNext()) {
            arraylist.add(
                ListCheckboxAdapter.ListCheckboxDB(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3)
                )
            )
        }
    }
}

/*
db=dbHelper.writableDatabase
db.execSQL(ContractDB.insertCheckBoxTB(1,"저녁먹기",20220301,20220301))
*/