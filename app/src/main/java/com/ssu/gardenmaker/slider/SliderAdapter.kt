package com.ssu.gardenmaker.slider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssu.gardenmaker.R

class SliderAdapter(private val context: Context, private val sliderItems: MutableList<Int>) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item: ImageView =  itemView.findViewById(R.id.iv_image_slider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(LayoutInflater.from(context).inflate(R.layout.slider_item, parent, false))
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.item.setImageDrawable(ContextCompat.getDrawable(context, sliderItems[position]))
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }
}