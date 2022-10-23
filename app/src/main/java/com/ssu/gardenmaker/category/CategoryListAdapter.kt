package com.ssu.gardenmaker.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.ssu.gardenmaker.R
import okhttp3.internal.notify

class CategoryListAdapter(
    private val context: Context,
    private val categoryList : MutableList<String>
    ) : BaseAdapter() {

    private class ViewHolder {
        var categoryName : TextView? = null
        var ButtonModify : ImageButton? = null
        var ButtonDelete : ImageButton? = null
    }

    override fun getCount(): Int {
        return categoryList.size
    }

    override fun getItem(position : Int): Any {
        return categoryList[position]
    }

    override fun getItemId(position : Int): Long {
        return position.toLong()
    }

    override fun getView(position : Int, convertView : View?, parent : ViewGroup?): View {
        val viewHolder : ViewHolder
        val view : View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listview_category, parent, false)
            viewHolder = ViewHolder()
            viewHolder.categoryName = view.findViewById(R.id.tv_listview)
            viewHolder.ButtonModify = view.findViewById(R.id.btn_modify)
            viewHolder.ButtonDelete = view.findViewById(R.id.btn_delete)
            view.tag = viewHolder
        }
        else {
            viewHolder = convertView.tag as ViewHolder
            view = convertView
        }

        viewHolder.categoryName?.text = categoryList[position]

        viewHolder.ButtonModify?.setOnClickListener {
            val categoryChangeNameDialog = CategoryChangeNameDialog(context)
            categoryChangeNameDialog.showDialog()
            categoryChangeNameDialog.setOnClickListener(object : CategoryChangeNameDialog.CategoryChangeNameDialogClickListener {
                override fun onClicked(name: String) {
                    categoryList[position] = name
                    notifyDataSetChanged()
                    Toast.makeText(context, "카테고리 이름이 변경되었습니다", Toast.LENGTH_SHORT).show()
                }
            })
        }

        viewHolder.ButtonDelete?.setOnClickListener {
            // 해당 카테고리 안에 아무것도 없을 경우에만 삭제 가능
            categoryList.removeAt(position)
            notifyDataSetChanged()
            Toast.makeText(context, "카테고리가 삭제되었습니다", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}