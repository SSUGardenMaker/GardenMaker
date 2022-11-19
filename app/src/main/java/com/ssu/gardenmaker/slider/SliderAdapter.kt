package com.ssu.gardenmaker.slider

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.ssu.gardenmaker.databinding.SliderItemBinding

class SliderAdapter(context: Context, viewPager2: ViewPager2, sliderImage: ArrayList<String>) :
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private val mContext: Context
    private val mViewPager2 : ViewPager2
    private val mSliderItems: ArrayList<String>

    init {
        mContext = context
        mViewPager2 = viewPager2
        mSliderItems = sliderImage
    }

    inner class SliderViewHolder(binding: SliderItemBinding) : RecyclerView.ViewHolder(binding.root) {

        private val mBinding: SliderItemBinding

        init {
            mBinding = binding
        }

        fun bind(sliderItem: String?) {
            try {
                Glide.with(mContext).load(sliderItem).into(mBinding.ivImageSlider)
            }
            catch (e: Exception) {
                Log.d("SliderAdapter", "ERROR: " + e.message)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(SliderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(mSliderItems[position])
    }

    override fun getItemCount(): Int {
        return mSliderItems.size
    }
}