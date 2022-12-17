package com.ssu.gardenmaker.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ssu.gardenmaker.R

class ListCalendarAdapter(context: Context, itemList: ArrayList<calendarAdapterList>) : BaseAdapter() {

    class calendarAdapterList(var name: String, var category: String, var function: String, var start_day: Int, var end_day: Int)

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
        var view: View? = p1
        if(view==null){
            val inflater=mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view=inflater.inflate(R.layout.dialog_calendar_plantlist,p2,false)
        }

        view?.findViewById<TextView>(R.id.list_cal_name)?.text= mItemlist[p0].name
        view?.findViewById<TextView>(R.id.list_cal_category)?.text="(${mItemlist[p0].category}) "
        view?.findViewById<TextView>(R.id.list_cal_function)?.text=", ${mItemlist[p0].function}"
        view?.findViewById<TextView>(R.id.list_cal_startday)?.text=StringBuffer( mItemlist[p0].start_day.toString()).insert(4,".").insert(7,".").toString()
        view?.findViewById<TextView>(R.id.list_cal_endday)?.text=StringBuffer( mItemlist[p0].end_day.toString()).insert(4,".").insert(7,".").toString()

        return view
    }
    fun notifyChanged(itemList: ArrayList<calendarAdapterList>){
        mItemlist=itemList
        notifyDataSetChanged()
    }
}