package com.ssu.gardenmaker.category

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.ssu.gardenmaker.R

class CategoryAddDialog(context : Context) {
    private val mContext = context
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
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val et_category_add = dialog.findViewById<EditText>(R.id.et_category_add)
        val btn_category_add = dialog.findViewById<Button>(R.id.btn_category_add)

        btn_category_add.setOnClickListener {
            if (et_category_add.text.toString().replace(" ", "") == "") {
                Toast.makeText(mContext, "카테고리 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                onClickListener.onClicked(et_category_add.text.toString())
                dialog.dismiss()
            }
        }
    }
}