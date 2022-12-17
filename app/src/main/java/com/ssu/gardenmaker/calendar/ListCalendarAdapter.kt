package com.ssu.gardenmaker.calendar

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ssu.gardenmaker.R
import kotlin.random.Random

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
        var random = Random(System.currentTimeMillis())
        var rand=random.nextInt(20)+17
        var imageView= view?.findViewById<ImageView>(R.id.list_cal_imageview)
        when(rand){
            17 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant17))
            18 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant18))
            19 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant19))
            20 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant20))
            21 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant21))
            22 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant22))
            23 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant23))
            24 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant24))
            25 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant25))
            26 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant26))
            27 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant27))
            28 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant28))
            29 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant29))
            30 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant30))
            31 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant31))
            32 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant32))
            33 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant33))
            34 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant34))
            35 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant35))
            36 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant36))
            37 -> imageView?.setImageDrawable(mContext.resources.getDrawable(R.drawable.plant37))
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