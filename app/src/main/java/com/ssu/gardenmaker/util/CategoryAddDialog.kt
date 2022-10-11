package com.ssu.gardenmaker.util

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.ssu.gardenmaker.R

class CategoryAddDialog(context : Context) {
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener

    interface OnDialogClickListener {
        fun onClicked(name: String)
    }

    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_add_category)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val et_category_add = dialog.findViewById<EditText>(R.id.et_category_add)
        val btn_category_add = dialog.findViewById<Button>(R.id.btn_category_add)

        btn_category_add.setOnClickListener {
            onClickListener.onClicked(et_category_add.text.toString())
            dialog.dismiss()
        }
    }
}