package com.ssu.gardenmaker.category

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.Button
import android.widget.ListView
import com.ssu.gardenmaker.R

class CategoryEditDialog(context : Context, categoryList : MutableList<String>) {
    private val mAdapter = CategoryListAdapter(context, categoryList)
    private val dialog = Dialog(context)

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_edit_category)
        dialog.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val mListview = dialog.findViewById<ListView>(R.id.listview_category)
        mListview.adapter = mAdapter

        val btnCategoryEdit = dialog.findViewById<Button>(R.id.btn_category_edit)

        btnCategoryEdit.setOnClickListener {
            mAdapter.notifyDataSetChanged()
            dialog.dismiss()
        }
    }
}