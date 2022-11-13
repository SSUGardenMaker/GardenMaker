package com.ssu.gardenmaker.CalendarAndCheckList

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ssu.gardenmaker.R

class List_Calendar_Adpater(context:Context,itemlist:ArrayList<List_Calendar_DB>) : BaseAdapter() {
    var itemlist=itemlist
    var context=context

    override fun getCount(): Int {
        return itemlist.size
    }

    override fun getItem(p0: Int): Any {
        return itemlist.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View? {
        var view: View? =p1                         //p1은 val타입으로 다른 값 대입 불가능.
        if(view==null){
            var inflater=context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view=inflater.inflate(R.layout.dialog_calendar_plantlist,p2,false)
        }

        view?.findViewById<TextView>(R.id.list_cal_name)?.text=itemlist.get(p0).name
        view?.findViewById<TextView>(R.id.list_cal_category)?.text="(${itemlist.get(p0).category}) "
        view?.findViewById<TextView>(R.id.list_cal_function)?.text=", ${itemlist.get(p0).function}"
        view?.findViewById<TextView>(R.id.list_cal_startday)?.text=StringBuffer( itemlist.get(p0).start_day.toString()).insert(4,".").insert(7,".").toString()
        view?.findViewById<TextView>(R.id.list_cal_endday)?.text=StringBuffer( itemlist.get(p0).end_day.toString()).insert(4,".").insert(7,".").toString()

        return view
    }
}
class List_Calendar_DB(name:String,category:String,function:String,start_day:Int,end_day:Int){
    var name=name
    var category=category
    var function=function
    var start_day=start_day
    var end_day=end_day
}