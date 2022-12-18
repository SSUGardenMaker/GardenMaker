package com.ssu.gardenmaker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R

class GridAdapter(var context: Context) : BaseAdapter() {

    private class PlantDoneHolder {
        var plantImage : ImageView? = null
        var plantName : TextView? = null
        var plantType : TextView? = null
        var plantStartDate : TextView? = null
        var plantEndDate : TextView? = null
    }

    override fun getCount(): Int {
        return ApplicationClass.plantDoneLists.size
    }

    override fun getItem(position: Int): Any {
        return ApplicationClass.plantDoneLists[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder : PlantDoneHolder
        val view : View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.plant_done_item, parent, false)
            viewHolder = PlantDoneHolder()
            viewHolder.plantImage = view.findViewById(R.id.iv_plant_done)
            viewHolder.plantName = view.findViewById(R.id.tv_plant_done_name_value)
            viewHolder.plantType = view.findViewById(R.id.tv_plant_done_type_value)
            viewHolder.plantStartDate = view.findViewById(R.id.tv_plant_done_start_value)
            viewHolder.plantEndDate = view.findViewById(R.id.tv_plant_done_end_value)
            view.tag = viewHolder
        }
        else {
            viewHolder = convertView.tag as PlantDoneHolder
            view = convertView
        }

        when (ApplicationClass.plantDoneLists[position].plantKind) {
            1 -> viewHolder.plantImage?.setImageResource(R.drawable.plant1)
            2 -> viewHolder.plantImage?.setImageResource(R.drawable.plant2)
            3 -> viewHolder.plantImage?.setImageResource(R.drawable.plant3)
            4 -> viewHolder.plantImage?.setImageResource(R.drawable.plant4)
            5 -> viewHolder.plantImage?.setImageResource(R.drawable.plant5)
            6 -> viewHolder.plantImage?.setImageResource(R.drawable.plant6)
            7 -> viewHolder.plantImage?.setImageResource(R.drawable.plant7)
            8 -> viewHolder.plantImage?.setImageResource(R.drawable.plant8)
            9 -> viewHolder.plantImage?.setImageResource(R.drawable.plant9)
            10 -> viewHolder.plantImage?.setImageResource(R.drawable.plant10)
            11 -> viewHolder.plantImage?.setImageResource(R.drawable.plant11)
            12 -> viewHolder.plantImage?.setImageResource(R.drawable.plant12)
            13 -> viewHolder.plantImage?.setImageResource(R.drawable.plant13)
            14 -> viewHolder.plantImage?.setImageResource(R.drawable.plant14)
            15 -> viewHolder.plantImage?.setImageResource(R.drawable.plant15)
            16 -> viewHolder.plantImage?.setImageResource(R.drawable.plant16)
            17 -> viewHolder.plantImage?.setImageResource(R.drawable.plant17)
            18 -> viewHolder.plantImage?.setImageResource(R.drawable.plant18)
            19 -> viewHolder.plantImage?.setImageResource(R.drawable.plant19)
            20 -> viewHolder.plantImage?.setImageResource(R.drawable.plant20)
            21 -> viewHolder.plantImage?.setImageResource(R.drawable.plant21)
            22 -> viewHolder.plantImage?.setImageResource(R.drawable.plant22)
            23 -> viewHolder.plantImage?.setImageResource(R.drawable.plant23)
            24 -> viewHolder.plantImage?.setImageResource(R.drawable.plant24)
            25 -> viewHolder.plantImage?.setImageResource(R.drawable.plant25)
            26 -> viewHolder.plantImage?.setImageResource(R.drawable.plant26)
            27 -> viewHolder.plantImage?.setImageResource(R.drawable.plant27)
            28 -> viewHolder.plantImage?.setImageResource(R.drawable.plant28)
            29 -> viewHolder.plantImage?.setImageResource(R.drawable.plant29)
            30 -> viewHolder.plantImage?.setImageResource(R.drawable.plant30)
            31 -> viewHolder.plantImage?.setImageResource(R.drawable.plant31)
            32 -> viewHolder.plantImage?.setImageResource(R.drawable.plant32)
            33 -> viewHolder.plantImage?.setImageResource(R.drawable.plant33)
            34 -> viewHolder.plantImage?.setImageResource(R.drawable.plant34)
            35 -> viewHolder.plantImage?.setImageResource(R.drawable.plant35)
            36 -> viewHolder.plantImage?.setImageResource(R.drawable.plant36)
            37 -> viewHolder.plantImage?.setImageResource(R.drawable.plant37)
        }

        viewHolder.plantName?.text = ApplicationClass.plantDoneLists[position].name

        when (ApplicationClass.plantDoneLists[position].plantType) {
            "CHECKBOX" -> viewHolder.plantType?.text = "체크박스"
            "WALK_COUNTER" -> viewHolder.plantType?.text = "만보기"
            "COUNTER" -> viewHolder.plantType?.text = "횟수"
            "ACCUMULATE_TIMER" -> viewHolder.plantType?.text = "누적 타이머"
            "RECURSIVE_TIMER" -> viewHolder.plantType?.text = "반복 타이머"
        }

        viewHolder.plantStartDate?.text = ApplicationClass.plantDoneLists[position].context1
        viewHolder.plantEndDate?.text = ApplicationClass.plantDoneLists[position].context2

        return view
    }
}