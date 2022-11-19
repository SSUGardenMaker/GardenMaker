package com.ssu.gardenmaker.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.databinding.ActivityGardenBinding
import com.ssu.gardenmaker.plant.PlantData
import com.ssu.gardenmaker.slider.SliderAdapter
import java.lang.reflect.Type
import kotlin.math.abs

class GardenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGardenBinding
    private lateinit var gardenName: String
    private val sliderItems: ArrayList<String> = ArrayList()
    private val dataArray: ArrayList<PlantData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGardenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("NAME"))
            gardenName = intent.getStringExtra("NAME").toString()

        binding.tvGardenName.text = gardenName

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

        // Viewpager 의 좌우 프리뷰를 구현
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(30))
        compositePageTransformer.addTransformer { page, position ->
            val scaleFactor = 0.85f + (1 - abs(position)) * 0.15f
            page.scaleY = scaleFactor
            page.alpha = scaleFactor
        }
        binding.vpImageSlider.setPageTransformer(compositePageTransformer)

        // Paging 할 때마다 호출
        binding.vpImageSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("GardenActivity", "Page ${position+1}")
            }
        })
    }

    private fun initItem() {
        // 만보기
        sliderItems.add("https://cdn.pixabay.com/photo/2019/12/26/10/44/horse-4720178_1280.jpg")

        // 횟수
        sliderItems.add("https://cdn.pixabay.com/photo/2019/10/15/13/33/red-deer-4551678_1280.jpg")

        // 누적 타이머
        sliderItems.add("https://cdn.pixabay.com/photo/2020/11/10/01/34/pet-5728249_1280.jpg")

        // 횟수 타이머
        sliderItems.add("https://cdn.pixabay.com/photo/2014/03/03/16/15/mosque-279015_1280.jpg")

        // 화단 종류에 따라 SharedPreferences 에서 해당하는 값 가져옴
        val gson = GsonBuilder().create()
        val groupListType: Type = object: TypeToken<ArrayList<PlantData?>?>() {}.type // json 을 객체로 만들 때 타입을 추론하는 역할
        val prev = ApplicationClass.mSharedPreferences.getString(gardenName, "NONE") // json list 가져오기

        if (prev != "NONE") {
            if (prev != "[]" || prev != "")
                dataArray.addAll(gson.fromJson(prev, groupListType))
        }
        else {
            Toast.makeText(this@GardenActivity, "화단에 꽃이 비어 있어요", Toast.LENGTH_SHORT).show()
        }
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