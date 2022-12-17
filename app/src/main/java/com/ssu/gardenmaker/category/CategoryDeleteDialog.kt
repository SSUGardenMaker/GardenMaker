package com.ssu.gardenmaker.category

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.ssu.gardenmaker.R

class CategoryDeleteDialog(context : Context, name : String) {
    private val categoryName = name
    private val dialog = Dialog(context)
    private lateinit var onClickListener: CategoryDeleteDialogClickListener

    interface CategoryDeleteDialogClickListener {
        fun onClicked()
    }

    fun setOnClickListener(listener: CategoryDeleteDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_delete_category)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()

        val nameCategoryDelete = dialog.findViewById<TextView>(R.id.tv_category_delete)
        nameCategoryDelete.text = categoryName

        val btnCategoryDeleteO = dialog.findViewById<Button>(R.id.btn_category_delete_o)
        val btnCategoryDeleteX = dialog.findViewById<Button>(R.id.btn_category_delete_x)

        btnCategoryDeleteO.setOnClickListener {
            onClickListener.onClicked()
            dialog.dismiss()
        }

        btnCategoryDeleteX.setOnClickListener {
            dialog.dismiss()
        }
    }
}