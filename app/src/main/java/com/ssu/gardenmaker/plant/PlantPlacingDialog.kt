package com.ssu.gardenmaker.plant

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.GridView
import android.widget.ImageView
import com.ssu.gardenmaker.R

class PlantPlacingDialog(var context : Context, var plantArea : ImageView, var AreaNum : String) {
    private val dialog = Dialog(context)

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_plant_placing)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val mGridview = dialog.findViewById<GridView>(R.id.gv_plant_placing)
        mGridview.adapter = PlantPlacingAdapter(context, dialog, plantArea, AreaNum)
    }
}