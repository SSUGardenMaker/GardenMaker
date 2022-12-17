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

class CategoryChangeNameDialog(context : Context) {
    private val mContext = context
    private val dialog = Dialog(context)
    private lateinit var onClickListener: CategoryChangeNameDialogClickListener

    interface CategoryChangeNameDialogClickListener {
        fun onClicked(name: String)
    }

    fun setOnClickListener(listener: CategoryChangeNameDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_change_name_category)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val etCategoryChangeName = dialog.findViewById<EditText>(R.id.et_category_change_name)
        val btnCategoryChangeName = dialog.findViewById<Button>(R.id.btn_category_change_name)

        btnCategoryChangeName.setOnClickListener {
            if (etCategoryChangeName.text.toString().replace(" ", "") == "") {
                Toast.makeText(mContext, "변경할 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                onClickListener.onClicked(etCategoryChangeName.text.toString())
                dialog.dismiss()
            }
        }
    }
}