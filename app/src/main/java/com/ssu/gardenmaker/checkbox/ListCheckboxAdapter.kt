package com.ssu.gardenmaker.checkbox

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ssu.gardenmaker.R

class ListCheckboxAdapter(context: Context, itemList: ArrayList<ListCheckboxDB>) : BaseAdapter() {

    class ListCheckboxDB(var id: Int, var title: String, var start_day: Int, var end_day: Int)

    private var mContext = context
    private var mItemlist = itemList

    override fun getCount(): Int {
        return mItemlist.size
    }

    override fun getItem(p0: Int): Any {
        return mItemlist[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }


    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
        var view: View? =p1
        if (view == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view=inflater.inflate(R.layout.dialog_checkbox_list,p2,false)
        }

        view?.findViewById<TextView>(R.id.dialog_checkbox_list_title_text)?.text= mItemlist[p0].title
        view?.findViewById<TextView>(R.id.dialog_checkbox_list_sub_text)?.text= mItemlist[p0].end_day.toString()

        return view
    }

}