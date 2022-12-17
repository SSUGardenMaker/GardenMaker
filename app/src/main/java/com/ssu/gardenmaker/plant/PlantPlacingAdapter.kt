package com.ssu.gardenmaker.plant

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.util.SharedPreferenceManager

class PlantPlacingAdapter(var context: Context, var dialog: Dialog, var plantArea : ImageView, var areaNum : String) : BaseAdapter() {

    private class PlantPlacingHolder {
        var plantImage : ImageView? = null
        var plantKind : Int? = null
        var plantName : TextView? = null
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
        val viewHolder: PlantPlacingHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.plant_placing_item, parent, false)
            viewHolder = PlantPlacingHolder()
            viewHolder.plantImage = view.findViewById(R.id.iv_plant_placing)
            viewHolder.plantName = view.findViewById(R.id.tv_plant_placing_name_value)
            view.tag = viewHolder
        }
        else {
            viewHolder = convertView.tag as PlantPlacingHolder
            view = convertView
        }

        when (ApplicationClass.plantDoneLists[position].plantKind) {
            1 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant1)
                viewHolder.plantKind = R.drawable.plant1
            }
            2 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant2)
                viewHolder.plantKind = R.drawable.plant2
            }
            3 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant3)
                viewHolder.plantKind = R.drawable.plant3
            }
            4 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant4)
                viewHolder.plantKind = R.drawable.plant4
            }
            5 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant5)
                viewHolder.plantKind = R.drawable.plant5
            }
            6 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant6)
                viewHolder.plantKind = R.drawable.plant6
            }
            7 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant7)
                viewHolder.plantKind = R.drawable.plant7
            }
            8 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant8)
                viewHolder.plantKind = R.drawable.plant8
            }
            9 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant9)
                viewHolder.plantKind = R.drawable.plant9
            }
            10 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant10)
                viewHolder.plantKind = R.drawable.plant10
            }
            11 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant11)
                viewHolder.plantKind = R.drawable.plant11
            }
            12 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant12)
                viewHolder.plantKind = R.drawable.plant12
            }
            13 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant13)
                viewHolder.plantKind = R.drawable.plant13
            }
            14 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant14)
                viewHolder.plantKind = R.drawable.plant14
            }
            15 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant15)
                viewHolder.plantKind = R.drawable.plant15
            }
            16 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant16)
                viewHolder.plantKind = R.drawable.plant16
            }
            17 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant17)
                viewHolder.plantKind = R.drawable.plant17
            }
            18 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant18)
                viewHolder.plantKind = R.drawable.plant18
            }
            19 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant19)
                viewHolder.plantKind = R.drawable.plant19
            }
            20 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant20)
                viewHolder.plantKind = R.drawable.plant20
            }
            21 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant21)
                viewHolder.plantKind = R.drawable.plant21
            }
            22 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant22)
                viewHolder.plantKind = R.drawable.plant22
            }
            23 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant23)
                viewHolder.plantKind = R.drawable.plant23
            }
            24 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant24)
                viewHolder.plantKind = R.drawable.plant24
            }
            25 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant25)
                viewHolder.plantKind = R.drawable.plant25
            }
            26 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant26)
                viewHolder.plantKind = R.drawable.plant26
            }
            27 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant27)
                viewHolder.plantKind = R.drawable.plant27
            }
            28 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant28)
                viewHolder.plantKind = R.drawable.plant28
            }
            29 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant29)
                viewHolder.plantKind = R.drawable.plant29
            }
            30 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant30)
                viewHolder.plantKind = R.drawable.plant30
            }
            31 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant31)
                viewHolder.plantKind = R.drawable.plant31
            }
            32 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant32)
                viewHolder.plantKind = R.drawable.plant32
            }
            33 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant33)
                viewHolder.plantKind = R.drawable.plant33
            }
            34 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant34)
                viewHolder.plantKind = R.drawable.plant34
            }
            35 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant35)
                viewHolder.plantKind = R.drawable.plant35
            }
            36 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant36)
                viewHolder.plantKind = R.drawable.plant36
            }
            37 -> {
                viewHolder.plantImage?.setImageResource(R.drawable.plant37)
                viewHolder.plantKind = R.drawable.plant37
            }
        }

        viewHolder.plantName?.text = ApplicationClass.plantDoneLists[position].name

        viewHolder.plantImage?.setOnClickListener {
            SharedPreferenceManager().setInt(SharedPreferenceManager().getString("email") + "Place" + areaNum, viewHolder.plantKind!!)
            plantArea.setImageResource(viewHolder.plantKind!!)
            plantArea.clearAnimation()
            notifyDataSetChanged()
            dialog.dismiss()
        }

        return view
    }
}