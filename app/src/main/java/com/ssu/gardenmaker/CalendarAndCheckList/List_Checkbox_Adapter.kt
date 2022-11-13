package com.ssu.gardenmaker.CalendarAndCheckList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ssu.gardenmaker.R

class List_Checkbox_Adapter(context: Context, itemlist:ArrayList<List_Checkbox_DB>) : BaseAdapter() {
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
            view=inflater.inflate(R.layout.dialog_checkbox_list,p2,false)
        }
        view?.findViewById<TextView>(R.id.dialog_checkbox_list_title_text)?.text=itemlist.get(p0).title
        view?.findViewById<TextView>(R.id.dialog_checkbox_list_sub_text)?.text=itemlist.get(p0).end_day.toString()

        return view
    }

}
class List_Checkbox_DB(id:Int,title:String,start_day:Int,end_day:Int){
    var id=id
    var title=title
    var start_day=start_day
    var end_day=end_day
}