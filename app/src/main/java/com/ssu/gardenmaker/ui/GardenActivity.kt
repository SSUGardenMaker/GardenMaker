package com.ssu.gardenmaker.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.databinding.ActivityGardenBinding
import com.ssu.gardenmaker.slider.SliderAdapter
import kotlin.math.abs

class GardenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGardenBinding
    private val sliderItems : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGardenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("NAME"))
            binding.tvGardenName.text = intent.getStringExtra("NAME")

        initSlider()
    }

    private fun initSlider() {
        initItem()
        initButton()

        binding.vpImageSlider.adapter = SliderAdapter(this, binding.vpImageSlider, sliderItems)
        binding.dotsIndicator.attachTo(binding.vpImageSlider)

        // 양쪽 페이지 미리보기 상태
        binding.vpImageSlider.clipToPadding = false
        binding.vpImageSlider.clipChildren = false

        // 이전 페이지 + 현재 페이지 + 다음 페이지
        binding.vpImageSlider.offscreenPageLimit = 3
        binding.vpImageSlider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        // Viewpager의 좌우 프리뷰를 구현
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(30))
        compositePageTransformer.addTransformer { page, position ->
            page.scaleY = 0.85f + (1 - abs(position)) * 0.15f
        }
        binding.vpImageSlider.setPageTransformer(compositePageTransformer)
    }

    private fun initItem() {
        sliderItems.add("https://cdn.pixabay.com/photo/2019/12/26/10/44/horse-4720178_1280.jpg")
        sliderItems.add("https://cdn.pixabay.com/photo/2020/11/04/15/29/coffee-beans-5712780_1280.jpg")
        sliderItems.add("https://cdn.pixabay.com/photo/2020/11/10/01/34/pet-5728249_1280.jpg")
        sliderItems.add("https://cdn.pixabay.com/photo/2020/12/21/19/05/window-5850628_1280.png")
        sliderItems.add("https://cdn.pixabay.com/photo/2014/03/03/16/15/mosque-279015_1280.jpg")
        sliderItems.add("https://cdn.pixabay.com/photo/2019/10/15/13/33/red-deer-4551678_1280.jpg")
    }

    private fun initButton() {
        binding.ibGardenBack.setOnClickListener { finish() }

        binding.ivWatering.setOnClickListener {
            Toast.makeText(this@GardenActivity, "물주기 효과", Toast.LENGTH_SHORT).show()
        }

        binding.btnDeletePlant.setOnClickListener {
            Toast.makeText(this@GardenActivity, "식물이 삭제되었습니다", Toast.LENGTH_SHORT).show()
        }
    }
}