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
    private lateinit var onClickListener: CategoryAddDialogClickListener

    interface CategoryAddDialogClickListener {
        fun onClicked(name: String)
    }

    fun setOnClickListener(listener: CategoryAddDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_add_category)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val etCategoryAdd = dialog.findViewById<EditText>(R.id.et_category_add)
        val btnCategoryAdd = dialog.findViewById<Button>(R.id.btn_category_add)

        btnCategoryAdd.setOnClickListener {
            if (etCategoryAdd.text.toString().replace(" ", "") == "") {
                Toast.makeText(mContext, "화단 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                onClickListener.onClicked(etCategoryAdd.text.toString())
                dialog.dismiss()
            }
        }
    }
}