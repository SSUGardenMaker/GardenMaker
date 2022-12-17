package com.ssu.gardenmaker.plant

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.ListView
import androidx.appcompat.widget.AppCompatButton
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.retrofit.garden.GardenDataContent

class PlantSelectCategoryDialog(context : Context, categoryList : MutableList<GardenDataContent>, selectedCategory: AppCompatButton) {
    private val dialog = Dialog(context)
    private val mAdapter = PlantCategoryListAdapter(context, dialog, categoryList, selectedCategory)

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_select_category)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val mListview = dialog.findViewById<ListView>(R.id.listview_select_category)
        mListview.adapter = mAdapter
    }
}