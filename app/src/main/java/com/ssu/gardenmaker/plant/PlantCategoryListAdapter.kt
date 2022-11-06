package com.ssu.gardenmaker.plant

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.ssu.gardenmaker.R

class PlantCategoryListAdapter(
    private val context: Context,
    private val dialog: Dialog,
    private val categoryList : MutableList<String>,
    private val selectedCategory: AppCompatButton
    ) : BaseAdapter() {

    private class ViewHolder {
        var categoryLayout : LinearLayout? = null
        var categoryName : TextView? = null
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
            view = LayoutInflater.from(context).inflate(R.layout.listview_select_category, parent, false)
            viewHolder = ViewHolder()
            viewHolder.categoryLayout = view.findViewById(R.id.layout_select_listview)
            viewHolder.categoryName = view.findViewById(R.id.tv_select_listview)
            view.tag = viewHolder
        }
        else {
            viewHolder = convertView.tag as ViewHolder
            view = convertView
        }

        viewHolder.categoryName?.text = categoryList[position]

        viewHolder.categoryLayout?.setOnClickListener {
            selectedCategory.text = categoryList[position]
            dialog.dismiss()
        }

        return view
    }
}