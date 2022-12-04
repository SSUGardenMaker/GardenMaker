package com.ssu.gardenmaker.ui

import android.annotation.SuppressLint
import android.content.res.TypedArray
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.databinding.ActivityGardenBinding
import com.ssu.gardenmaker.retrofit.callback.RetrofitPlantCallback
import com.ssu.gardenmaker.retrofit.plant.PlantDataContent
import com.ssu.gardenmaker.slider.SliderAdapter
import kotlin.math.abs

class GardenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGardenBinding
    private lateinit var typedArray: TypedArray
    private val TAG = "GardenActivity"

    private lateinit var gardenName: String
    private var gardenID: Int = -1

    private var currentPage: Int = 0
    private val sliderItems: ArrayList<Int> = ArrayList()
    private val plantLists: MutableList<PlantDataContent> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGardenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("NAME"))
            gardenName = intent.getStringExtra("NAME").toString()

        if (intent.hasExtra("ID"))
            gardenID = intent.getIntExtra("ID", -1)

        binding.tvGardenName.text = gardenName

        typedArray = resources.obtainTypedArray(R.array.img_flowers)

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
                currentPage = position
                setPlantData(currentPage)
            }
        })
    }

    private fun initItem() {
        ApplicationClass.retrofitManager.plantGardenCheck(gardenID, object : RetrofitPlantCallback {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError : " + t.localizedMessage)
            }

            override fun onSuccess(message: String, data: List<PlantDataContent>) {
                Log.d(TAG, "onSuccess : message -> $message")
                Log.d(TAG, "onSuccess : data -> $data")
                Toast.makeText(this@GardenActivity, message, Toast.LENGTH_SHORT).show()

                plantLists.addAll(data)

                if (plantLists.size != 0) {
                    for (i in plantLists.indices) {
                        setPage(plantLists[i].isComplete, plantLists[i].plantKind)
                    }
                    setPlantData(0)
                }
                else {
                    Toast.makeText(this@GardenActivity, "화단에 꽃이 비어 있어요", Toast.LENGTH_SHORT).show()
                    binding.tvPlantName.visibility = GONE
                    binding.tvPlantNameValue.visibility = GONE
                    binding.tvPlantType.visibility = GONE
                    binding.tvPlantTypeValue.visibility = GONE
                    binding.tvPlantComplete.visibility = GONE
                    binding.tvPlantCompleteValue.visibility = GONE
                    binding.tvPlantStartDate.visibility = GONE
                    binding.tvPlantStartDateValue.visibility = GONE
                    binding.tvPlantEndDate.visibility = GONE
                    binding.tvPlantEndDateValue.visibility = GONE

                    binding.tvPlantPedometerGoalStep.visibility = GONE
                    binding.tvPlantPedometerGoalStepValue.visibility = GONE
                    binding.tvPlantPedometerGoalCount.visibility = GONE
                    binding.tvPlantPedometerGoalCountValue.visibility = GONE

                    binding.tvPlantCounter.visibility = GONE
                    binding.tvPlantCounterValue.visibility = GONE

                    binding.tvPlantTimerAccumulate.visibility = GONE
                    binding.tvPlantTimerAccumulateValue.visibility = GONE

                    binding.tvPlantTimerRecursive.visibility = GONE
                    binding.tvPlantTimerRecursiveValue.visibility = GONE
                    binding.tvPlantTimerRecursiveCount.visibility = GONE
                    binding.tvPlantTimerRecursiveCountValue.visibility = GONE

                    binding.ivWatering.visibility = View.INVISIBLE
                    binding.btnDeletePlant.visibility = View.INVISIBLE
                }
            }

            override fun onFailure(errorMessage: String, errorCode: Int) {
                Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                Log.d(TAG, "onFailure : errorCode -> $errorCode")
                Toast.makeText(this@GardenActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initButton() {
        binding.ibGardenBack.setOnClickListener { finish() }

        binding.ivWatering.setOnClickListener {
            Toast.makeText(this@GardenActivity, "물주기 효과", Toast.LENGTH_SHORT).show()
            wateringPlant()
        }

        binding.btnDeletePlant.setOnClickListener {
            //ApplicationClass.retrofitManager.gardenDelete()
            Toast.makeText(this@GardenActivity, "식물이 삭제되었습니다", Toast.LENGTH_SHORT).show()
            deletePlant()
        }
    }

    // 식물 물주기
    private fun wateringPlant() {

    }

    // 식물 삭제
    private fun deletePlant() {
        plantLists.removeAt(currentPage)
        sliderItems.removeAt(currentPage)
        binding.vpImageSlider.adapter?.notifyDataSetChanged()
    }

    // 식물 사진 set
    @SuppressLint("ResourceType")
    private fun setPage(isComplete : Boolean, plantKind : Int) {
        if (isComplete) {
            when (plantKind) {
                1 -> sliderItems.add(typedArray.getResourceId(1, -1))
                2 -> sliderItems.add(typedArray.getResourceId(2, -1))
                3 -> sliderItems.add(typedArray.getResourceId(3, -1))
                4 -> sliderItems.add(typedArray.getResourceId(4, -1))
                5 -> sliderItems.add(typedArray.getResourceId(5, -1))
                6 -> sliderItems.add(typedArray.getResourceId(6, -1))
                7 -> sliderItems.add(typedArray.getResourceId(7, -1))
                8 -> sliderItems.add(typedArray.getResourceId(8, -1))
                9 -> sliderItems.add(typedArray.getResourceId(9, -1))
                10 -> sliderItems.add(typedArray.getResourceId(10, -1))
                11 -> sliderItems.add(typedArray.getResourceId(11, -1))
                12 -> sliderItems.add(typedArray.getResourceId(12, -1))
                13 -> sliderItems.add(typedArray.getResourceId(13, -1))
                14 -> sliderItems.add(typedArray.getResourceId(14, -1))
                15 -> sliderItems.add(typedArray.getResourceId(15, -1))
                16 -> sliderItems.add(typedArray.getResourceId(16, -1))
                17 -> sliderItems.add(typedArray.getResourceId(17, -1))
                18 -> sliderItems.add(typedArray.getResourceId(18, -1))
                19 -> sliderItems.add(typedArray.getResourceId(19, -1))
                20 -> sliderItems.add(typedArray.getResourceId(20, -1))
                21 -> sliderItems.add(typedArray.getResourceId(21, -1))
                22 -> sliderItems.add(typedArray.getResourceId(22, -1))
                23 -> sliderItems.add(typedArray.getResourceId(23, -1))
                24 -> sliderItems.add(typedArray.getResourceId(24, -1))
                25 -> sliderItems.add(typedArray.getResourceId(25, -1))
                26 -> sliderItems.add(typedArray.getResourceId(26, -1))
                27 -> sliderItems.add(typedArray.getResourceId(27, -1))
                28 -> sliderItems.add(typedArray.getResourceId(28, -1))
                29 -> sliderItems.add(typedArray.getResourceId(29, -1))
                30 -> sliderItems.add(typedArray.getResourceId(30, -1))
                31 -> sliderItems.add(typedArray.getResourceId(31, -1))
                32 -> sliderItems.add(typedArray.getResourceId(32, -1))
                33 -> sliderItems.add(typedArray.getResourceId(33, -1))
                34 -> sliderItems.add(typedArray.getResourceId(34, -1))
                35 -> sliderItems.add(typedArray.getResourceId(35, -1))
                36 -> sliderItems.add(typedArray.getResourceId(36, -1))
                37 -> sliderItems.add(typedArray.getResourceId(37, -1))
            }
        }
        else {
            sliderItems.add(typedArray.getResourceId(0, -1))
        }
    }

    // 식물 상세정보 set
    private fun setPlantData(position: Int) {
        when (plantLists[position].plantType) {
            "CHECKBOX" -> {
                binding.tvPlantNameValue.text = plantLists[position].name
                binding.tvPlantTypeValue.text = "체크박스"
                binding.tvPlantCompleteValue.text = if (plantLists[position].isComplete) " O " else " X "
                binding.tvPlantStartDateValue.text = plantLists[position].startDate
                binding.tvPlantEndDateValue.text = plantLists[position].endDate

                binding.tvPlantName.visibility = VISIBLE
                binding.tvPlantNameValue.visibility = VISIBLE
                binding.tvPlantType.visibility = VISIBLE
                binding.tvPlantTypeValue.visibility = VISIBLE
                binding.tvPlantComplete.visibility = VISIBLE
                binding.tvPlantCompleteValue.visibility = VISIBLE
                binding.tvPlantStartDate.visibility = VISIBLE
                binding.tvPlantStartDateValue.visibility = VISIBLE
                binding.tvPlantEndDate.visibility = VISIBLE
                binding.tvPlantEndDateValue.visibility = VISIBLE

                binding.tvPlantPedometerGoalStep.visibility = GONE
                binding.tvPlantPedometerGoalStepValue.visibility = GONE
                binding.tvPlantPedometerGoalCount.visibility = GONE
                binding.tvPlantPedometerGoalCountValue.visibility = GONE

                binding.tvPlantCounter.visibility = GONE
                binding.tvPlantCounterValue.visibility = GONE

                binding.tvPlantTimerAccumulate.visibility = GONE
                binding.tvPlantTimerAccumulateValue.visibility = GONE

                binding.tvPlantTimerRecursive.visibility = GONE
                binding.tvPlantTimerRecursiveValue.visibility = GONE
                binding.tvPlantTimerRecursiveCount.visibility = GONE
                binding.tvPlantTimerRecursiveCountValue.visibility = GONE

                binding.ivWatering.visibility = VISIBLE
                binding.btnDeletePlant.visibility = VISIBLE
            }
            "WALK_COUNTER" -> {
                binding.tvPlantNameValue.text = plantLists[position].name
                binding.tvPlantTypeValue.text = "만보기"
                binding.tvPlantCompleteValue.text = if (plantLists[position].isComplete) " O " else " X "
                binding.tvPlantStartDateValue.text = plantLists[position].startDate
                binding.tvPlantEndDateValue.text = plantLists[position].endDate
                binding.tvPlantPedometerGoalStepValue.text = plantLists[position].walkStep.toString()
                binding.tvPlantPedometerGoalCountValue.text = plantLists[position].walkCountGoal.toString()

                binding.tvPlantName.visibility = VISIBLE
                binding.tvPlantNameValue.visibility = VISIBLE
                binding.tvPlantType.visibility = VISIBLE
                binding.tvPlantTypeValue.visibility = VISIBLE
                binding.tvPlantComplete.visibility = VISIBLE
                binding.tvPlantCompleteValue.visibility = VISIBLE
                binding.tvPlantStartDate.visibility = VISIBLE
                binding.tvPlantStartDateValue.visibility = VISIBLE
                binding.tvPlantEndDate.visibility = VISIBLE
                binding.tvPlantEndDateValue.visibility = VISIBLE

                binding.tvPlantPedometerGoalStep.visibility = VISIBLE
                binding.tvPlantPedometerGoalStepValue.visibility = VISIBLE
                binding.tvPlantPedometerGoalCount.visibility = VISIBLE
                binding.tvPlantPedometerGoalCountValue.visibility = VISIBLE

                binding.tvPlantCounter.visibility = GONE
                binding.tvPlantCounterValue.visibility = GONE

                binding.tvPlantTimerAccumulate.visibility = GONE
                binding.tvPlantTimerAccumulateValue.visibility = GONE

                binding.tvPlantTimerRecursive.visibility = GONE
                binding.tvPlantTimerRecursiveValue.visibility = GONE
                binding.tvPlantTimerRecursiveCount.visibility = GONE
                binding.tvPlantTimerRecursiveCountValue.visibility = GONE

                binding.ivWatering.visibility = VISIBLE
                binding.btnDeletePlant.visibility = VISIBLE
            }
            "COUNTER" -> {
                binding.tvPlantNameValue.text = plantLists[position].name
                binding.tvPlantTypeValue.text = "횟수"
                binding.tvPlantCompleteValue.text = if (plantLists[position].isComplete) " O " else " X "
                binding.tvPlantStartDateValue.text = plantLists[position].startDate
                binding.tvPlantEndDateValue.text = plantLists[position].endDate
                binding.tvPlantCounterValue.text = plantLists[position].counterGoal.toString()

                binding.tvPlantName.visibility = VISIBLE
                binding.tvPlantNameValue.visibility = VISIBLE
                binding.tvPlantType.visibility = VISIBLE
                binding.tvPlantTypeValue.visibility = VISIBLE
                binding.tvPlantComplete.visibility = VISIBLE
                binding.tvPlantCompleteValue.visibility = VISIBLE
                binding.tvPlantStartDate.visibility = VISIBLE
                binding.tvPlantStartDateValue.visibility = VISIBLE
                binding.tvPlantEndDate.visibility = VISIBLE
                binding.tvPlantEndDateValue.visibility = VISIBLE

                binding.tvPlantPedometerGoalStep.visibility = GONE
                binding.tvPlantPedometerGoalStepValue.visibility = GONE
                binding.tvPlantPedometerGoalCount.visibility = GONE
                binding.tvPlantPedometerGoalCountValue.visibility = GONE

                binding.tvPlantCounter.visibility = VISIBLE
                binding.tvPlantCounterValue.visibility = VISIBLE

                binding.tvPlantTimerAccumulate.visibility = GONE
                binding.tvPlantTimerAccumulateValue.visibility = GONE

                binding.tvPlantTimerRecursive.visibility = GONE
                binding.tvPlantTimerRecursiveValue.visibility = GONE
                binding.tvPlantTimerRecursiveCount.visibility = GONE
                binding.tvPlantTimerRecursiveCountValue.visibility = GONE

                binding.ivWatering.visibility = VISIBLE
                binding.btnDeletePlant.visibility = VISIBLE
            }
            "TIMER" -> {
                binding.tvPlantNameValue.text = plantLists[position].name
                binding.tvPlantTypeValue.text = "누적 타이머"
                binding.tvPlantCompleteValue.text = if (plantLists[position].isComplete) " O " else " X "
                binding.tvPlantStartDateValue.text = plantLists[position].startDate
                binding.tvPlantEndDateValue.text = plantLists[position].endDate
                binding.tvPlantTimerAccumulateValue.text = plantLists[position].timerTotalMin.toString()

                binding.tvPlantName.visibility = VISIBLE
                binding.tvPlantNameValue.visibility = VISIBLE
                binding.tvPlantType.visibility = VISIBLE
                binding.tvPlantTypeValue.visibility = VISIBLE
                binding.tvPlantComplete.visibility = VISIBLE
                binding.tvPlantCompleteValue.visibility = VISIBLE
                binding.tvPlantStartDate.visibility = VISIBLE
                binding.tvPlantStartDateValue.visibility = VISIBLE
                binding.tvPlantEndDate.visibility = VISIBLE
                binding.tvPlantEndDateValue.visibility = VISIBLE

                binding.tvPlantPedometerGoalStep.visibility = GONE
                binding.tvPlantPedometerGoalStepValue.visibility = GONE
                binding.tvPlantPedometerGoalCount.visibility = GONE
                binding.tvPlantPedometerGoalCountValue.visibility = GONE

                binding.tvPlantCounter.visibility = GONE
                binding.tvPlantCounterValue.visibility = GONE

                binding.tvPlantTimerAccumulate.visibility = VISIBLE
                binding.tvPlantTimerAccumulateValue.visibility = VISIBLE

                binding.tvPlantTimerRecursive.visibility = GONE
                binding.tvPlantTimerRecursiveValue.visibility = GONE
                binding.tvPlantTimerRecursiveCount.visibility = GONE
                binding.tvPlantTimerRecursiveCountValue.visibility = GONE

                binding.ivWatering.visibility = VISIBLE
                binding.btnDeletePlant.visibility = VISIBLE
            }
            "RECURSIVE_TIMER" -> {
                binding.tvPlantNameValue.text = plantLists[position].name
                binding.tvPlantTypeValue.text = "반복 타이머"
                binding.tvPlantCompleteValue.text = if (plantLists[position].isComplete) " O " else " X "
                binding.tvPlantStartDateValue.text = plantLists[position].startDate
                binding.tvPlantEndDateValue.text = plantLists[position].endDate
                binding.tvPlantTimerRecursiveValue.text = plantLists[position].timerTotalMin.toString()
                binding.tvPlantTimerRecursiveCountValue.text = plantLists[position].timerCurrentMin.toString()

                binding.tvPlantName.visibility = VISIBLE
                binding.tvPlantNameValue.visibility = VISIBLE
                binding.tvPlantType.visibility = VISIBLE
                binding.tvPlantTypeValue.visibility = VISIBLE
                binding.tvPlantComplete.visibility = VISIBLE
                binding.tvPlantCompleteValue.visibility = VISIBLE
                binding.tvPlantStartDate.visibility = VISIBLE
                binding.tvPlantStartDateValue.visibility = VISIBLE
                binding.tvPlantEndDate.visibility = VISIBLE
                binding.tvPlantEndDateValue.visibility = VISIBLE

                binding.tvPlantPedometerGoalStep.visibility = GONE
                binding.tvPlantPedometerGoalStepValue.visibility = GONE
                binding.tvPlantPedometerGoalCount.visibility = GONE
                binding.tvPlantPedometerGoalCountValue.visibility = GONE

                binding.tvPlantCounter.visibility = GONE
                binding.tvPlantCounterValue.visibility = GONE

                binding.tvPlantTimerAccumulate.visibility = GONE
                binding.tvPlantTimerAccumulateValue.visibility = GONE

                binding.tvPlantTimerRecursive.visibility = VISIBLE
                binding.tvPlantTimerRecursiveValue.visibility = VISIBLE
                binding.tvPlantTimerRecursiveCount.visibility = VISIBLE
                binding.tvPlantTimerRecursiveCountValue.visibility = VISIBLE

                binding.ivWatering.visibility = VISIBLE
                binding.btnDeletePlant.visibility = VISIBLE
            }
        }
    }
}