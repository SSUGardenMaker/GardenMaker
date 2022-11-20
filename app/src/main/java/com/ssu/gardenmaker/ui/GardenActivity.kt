package com.ssu.gardenmaker.ui

import android.os.Bundle
import android.view.View
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

        binding.vpImageSlider.setPageTransformer { page, position ->  }

        // Paging 할 때마다 호출
        binding.vpImageSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setPlantData(position)
            }
        })
    }

    private fun initItem() {
        // 화단 종류에 따라 SharedPreferences 에서 해당하는 값 가져옴
        val gson = GsonBuilder().create()
        val groupListType: Type = object: TypeToken<ArrayList<PlantData?>?>() {}.type // json 을 객체로 만들 때 타입을 추론하는 역할
        val prev = ApplicationClass.mSharedPreferences.getString(gardenName, "NONE") // json list 가져오기

        if (prev != "NONE") {
            if (prev != "[]" || prev != "")
                dataArray.addAll(gson.fromJson(prev, groupListType))
            for (i in dataArray.indices) {
                setPage(i)
            }
            setPlantData(0)
        }
        else {
            Toast.makeText(this@GardenActivity, "화단에 꽃이 비어 있어요", Toast.LENGTH_SHORT).show()
            binding.tvPlantName.visibility = View.GONE
            binding.tvPlantNameValue.visibility = View.GONE
            binding.tvPlantType.visibility = View.GONE
            binding.tvPlantTypeValue.visibility = View.GONE
            binding.tvPlantStartDate.visibility = View.GONE
            binding.tvPlantStartDateValue.visibility = View.GONE
            binding.tvPlantEndDate.visibility = View.GONE
            binding.tvPlantEndDateValue.visibility = View.GONE

            binding.tvPlantPedometerGoalStep.visibility = View.GONE
            binding.tvPlantPedometerGoalStepValue.visibility = View.GONE
            binding.tvPlantPedometerGoalCount.visibility = View.GONE
            binding.tvPlantPedometerGoalCountValue.visibility = View.GONE

            binding.tvPlantCounter.visibility = View.GONE
            binding.tvPlantCounterValue.visibility = View.GONE

            binding.tvPlantTimerAccumulate.visibility = View.GONE
            binding.tvPlantTimerAccumulateValue.visibility = View.GONE

            binding.tvPlantTimerRecursive.visibility = View.GONE
            binding.tvPlantTimerRecursiveValue.visibility = View.GONE
            binding.tvPlantTimerRecursiveCount.visibility = View.GONE
            binding.tvPlantTimerRecursiveCountValue.visibility = View.GONE

            binding.ivWatering.visibility = View.INVISIBLE
            binding.btnDeletePlant.visibility = View.INVISIBLE
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

    private fun setPage(position: Int) {
        when (dataArray[position].plantType) {
            "만보기" -> {
                sliderItems.add("https://cdn.pixabay.com/photo/2019/12/26/10/44/horse-4720178_1280.jpg")
            }
            "횟수" -> {
                sliderItems.add("https://cdn.pixabay.com/photo/2019/10/15/13/33/red-deer-4551678_1280.jpg")
            }
            "누적 타이머" -> {
                sliderItems.add("https://cdn.pixabay.com/photo/2020/11/10/01/34/pet-5728249_1280.jpg")
            }
            "반복 타이머" -> {
                sliderItems.add("https://cdn.pixabay.com/photo/2014/03/03/16/15/mosque-279015_1280.jpg")
            }
        }
    }

    private fun setPlantData(position: Int) {
        when (dataArray[position].plantType) {
            "만보기" -> {
                binding.tvPlantNameValue.text = dataArray[position].plantName
                binding.tvPlantTypeValue.text = dataArray[position].plantType
                binding.tvPlantStartDateValue.text = dataArray[position].startDate
                binding.tvPlantEndDateValue.text = dataArray[position].endDate
                binding.tvPlantPedometerGoalStepValue.text = dataArray[position].goalStepPedometer
                binding.tvPlantPedometerGoalCountValue.text = dataArray[position].goalCountPedometer

                binding.tvPlantName.visibility = View.VISIBLE
                binding.tvPlantNameValue.visibility = View.VISIBLE
                binding.tvPlantType.visibility = View.VISIBLE
                binding.tvPlantTypeValue.visibility = View.VISIBLE
                binding.tvPlantStartDate.visibility = View.VISIBLE
                binding.tvPlantStartDateValue.visibility = View.VISIBLE
                binding.tvPlantEndDate.visibility = View.VISIBLE
                binding.tvPlantEndDateValue.visibility = View.VISIBLE

                binding.tvPlantPedometerGoalStep.visibility = View.VISIBLE
                binding.tvPlantPedometerGoalStepValue.visibility = View.VISIBLE
                binding.tvPlantPedometerGoalCount.visibility = View.VISIBLE
                binding.tvPlantPedometerGoalCountValue.visibility = View.VISIBLE

                binding.tvPlantCounter.visibility = View.GONE
                binding.tvPlantCounterValue.visibility = View.GONE

                binding.tvPlantTimerAccumulate.visibility = View.GONE
                binding.tvPlantTimerAccumulateValue.visibility = View.GONE

                binding.tvPlantTimerRecursive.visibility = View.GONE
                binding.tvPlantTimerRecursiveValue.visibility = View.GONE
                binding.tvPlantTimerRecursiveCount.visibility = View.GONE
                binding.tvPlantTimerRecursiveCountValue.visibility = View.GONE

                binding.ivWatering.visibility = View.VISIBLE
                binding.btnDeletePlant.visibility = View.VISIBLE
            }
            "횟수" -> {
                binding.tvPlantNameValue.text = dataArray[position].plantName
                binding.tvPlantTypeValue.text = dataArray[position].plantType
                binding.tvPlantStartDateValue.text = dataArray[position].startDate
                binding.tvPlantEndDateValue.text = dataArray[position].endDate
                binding.tvPlantCounterValue.text = dataArray[position].goalCountCounter

                binding.tvPlantName.visibility = View.VISIBLE
                binding.tvPlantNameValue.visibility = View.VISIBLE
                binding.tvPlantType.visibility = View.VISIBLE
                binding.tvPlantTypeValue.visibility = View.VISIBLE
                binding.tvPlantStartDate.visibility = View.VISIBLE
                binding.tvPlantStartDateValue.visibility = View.VISIBLE
                binding.tvPlantEndDate.visibility = View.VISIBLE
                binding.tvPlantEndDateValue.visibility = View.VISIBLE

                binding.tvPlantPedometerGoalStep.visibility = View.GONE
                binding.tvPlantPedometerGoalStepValue.visibility = View.GONE
                binding.tvPlantPedometerGoalCount.visibility = View.GONE
                binding.tvPlantPedometerGoalCountValue.visibility = View.GONE

                binding.tvPlantCounter.visibility = View.VISIBLE
                binding.tvPlantCounterValue.visibility = View.VISIBLE

                binding.tvPlantTimerAccumulate.visibility = View.GONE
                binding.tvPlantTimerAccumulateValue.visibility = View.GONE

                binding.tvPlantTimerRecursive.visibility = View.GONE
                binding.tvPlantTimerRecursiveValue.visibility = View.GONE
                binding.tvPlantTimerRecursiveCount.visibility = View.GONE
                binding.tvPlantTimerRecursiveCountValue.visibility = View.GONE

                binding.ivWatering.visibility = View.VISIBLE
                binding.btnDeletePlant.visibility = View.VISIBLE
            }
            "누적 타이머" -> {
                binding.tvPlantNameValue.text = dataArray[position].plantName
                binding.tvPlantTypeValue.text = dataArray[position].plantType
                binding.tvPlantStartDateValue.text = dataArray[position].startDate
                binding.tvPlantEndDateValue.text = dataArray[position].endDate
                binding.tvPlantTimerAccumulateValue.text = dataArray[position].goalTimerAccumulate

                binding.tvPlantName.visibility = View.VISIBLE
                binding.tvPlantNameValue.visibility = View.VISIBLE
                binding.tvPlantType.visibility = View.VISIBLE
                binding.tvPlantTypeValue.visibility = View.VISIBLE
                binding.tvPlantStartDate.visibility = View.VISIBLE
                binding.tvPlantStartDateValue.visibility = View.VISIBLE
                binding.tvPlantEndDate.visibility = View.VISIBLE
                binding.tvPlantEndDateValue.visibility = View.VISIBLE

                binding.tvPlantPedometerGoalStep.visibility = View.GONE
                binding.tvPlantPedometerGoalStepValue.visibility = View.GONE
                binding.tvPlantPedometerGoalCount.visibility = View.GONE
                binding.tvPlantPedometerGoalCountValue.visibility = View.GONE

                binding.tvPlantCounter.visibility = View.GONE
                binding.tvPlantCounterValue.visibility = View.GONE

                binding.tvPlantTimerAccumulate.visibility = View.VISIBLE
                binding.tvPlantTimerAccumulateValue.visibility = View.VISIBLE

                binding.tvPlantTimerRecursive.visibility = View.GONE
                binding.tvPlantTimerRecursiveValue.visibility = View.GONE
                binding.tvPlantTimerRecursiveCount.visibility = View.GONE
                binding.tvPlantTimerRecursiveCountValue.visibility = View.GONE

                binding.ivWatering.visibility = View.VISIBLE
                binding.btnDeletePlant.visibility = View.VISIBLE
            }
            "반복 타이머" -> {
                binding.tvPlantNameValue.text = dataArray[position].plantName
                binding.tvPlantTypeValue.text = dataArray[position].plantType
                binding.tvPlantStartDateValue.text = dataArray[position].startDate
                binding.tvPlantEndDateValue.text = dataArray[position].endDate
                binding.tvPlantTimerRecursiveValue.text = dataArray[position].goalTimerRecursive
                binding.tvPlantTimerRecursiveCountValue.text = dataArray[position].goalCountTimerRecursive

                binding.tvPlantName.visibility = View.VISIBLE
                binding.tvPlantNameValue.visibility = View.VISIBLE
                binding.tvPlantType.visibility = View.VISIBLE
                binding.tvPlantTypeValue.visibility = View.VISIBLE
                binding.tvPlantStartDate.visibility = View.VISIBLE
                binding.tvPlantStartDateValue.visibility = View.VISIBLE
                binding.tvPlantEndDate.visibility = View.VISIBLE
                binding.tvPlantEndDateValue.visibility = View.VISIBLE

                binding.tvPlantPedometerGoalStep.visibility = View.GONE
                binding.tvPlantPedometerGoalStepValue.visibility = View.GONE
                binding.tvPlantPedometerGoalCount.visibility = View.GONE
                binding.tvPlantPedometerGoalCountValue.visibility = View.GONE

                binding.tvPlantCounter.visibility = View.GONE
                binding.tvPlantCounterValue.visibility = View.GONE

                binding.tvPlantTimerAccumulate.visibility = View.GONE
                binding.tvPlantTimerAccumulateValue.visibility = View.GONE

                binding.tvPlantTimerRecursive.visibility = View.VISIBLE
                binding.tvPlantTimerRecursiveValue.visibility = View.VISIBLE
                binding.tvPlantTimerRecursiveCount.visibility = View.VISIBLE
                binding.tvPlantTimerRecursiveCountValue.visibility = View.VISIBLE

                binding.ivWatering.visibility = View.VISIBLE
                binding.btnDeletePlant.visibility = View.VISIBLE
            }
        }
    }
}