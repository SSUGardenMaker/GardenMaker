package com.ssu.gardenmaker.slider

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssu.gardenmaker.databinding.SliderItemBinding

class SliderAdapter(private val context: Context, private val sliderItems: MutableList<Int>) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(var binding: SliderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Int) {
            try {
                Glide.with(context).load(item).into(binding.ivImageSlider)
            }
            catch(e: Exception){
                Log.d("SliderAdapter", "ERROR: " + e.message);
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(SliderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(sliderItems[position])
    }

    override fun getItemCount(): Int {
        return sliderItems.size
    }
}