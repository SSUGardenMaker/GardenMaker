package com.ssu.gardenmaker.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.ssu.gardenmaker.adapter.SliderAdapter
import com.ssu.gardenmaker.databinding.ActivityGardenBinding
import com.ssu.gardenmaker.features.accumulateTimer.accumulateTimerService
import com.ssu.gardenmaker.features.counter.counter
import com.ssu.gardenmaker.features.pedometer.PedometerService
import com.ssu.gardenmaker.features.recursiveTimer.recursiveTimerService
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.retrofit.callback.RetrofitPlantCallback
import com.ssu.gardenmaker.retrofit.plant.PlantDataContent
import com.ssu.gardenmaker.util.SharedPreferenceManager
import java.util.*
import kotlin.math.abs

class GardenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGardenBinding
    private val TAG = "GardenActivity"

    private lateinit var gardenName: String
    private var gardenID: Int = -1

    private var currentPage: Int = 0
    private val sliderItems: MutableList<Int> = mutableListOf()
    private val plantLists: MutableList<PlantDataContent> = mutableListOf()

    private lateinit var count_featureTimer:Timer
    lateinit var serverConext:Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serverConext=this
        binding = ActivityGardenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("NAME"))
            gardenName = intent.getStringExtra("NAME").toString()

        if (intent.hasExtra("ID"))
            gardenID = intent.getIntExtra("ID", -1)

        binding.tvGardenName.text = gardenName

        initSlider()
    }

    private fun initSlider() {
        initItem()
        initButton()

        binding.vpImageSlider.adapter = SliderAdapter(this@GardenActivity, sliderItems)
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
                try{
                    count_featureTimer.cancel()
                }catch (e:java.lang.Exception){
                    //count_featureTimer가 counter기능을 쓰지 않으면 null이고, 맨처음 가든 액티비티 들어올떄도 null이다.
                }

                binding.tvCounterLimitText.visibility= GONE
                currentPage = position
                setPlantData(currentPage)

                if (plantLists.size != 0 && plantLists[currentPage].plantType=="COUNTER") {
                    var last_click_time:Long=ApplicationClass.mSharedPreferences.getLong("${SharedPreferenceManager().getString("email")}${plantLists[currentPage].gardenId}${plantLists[currentPage].id}",0)
                    if(last_click_time+300000>System.currentTimeMillis()) {
                       count_featureTimer=Timer()
                       count_featureTimer.schedule(counter(plantLists[currentPage].id,(last_click_time+300000-System.currentTimeMillis()),binding.tvCounterLimitText, count_featureTimer).createTimerTask(), 0, 1000)
                    }
                }
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

                for (i in data.indices) {
                    if (!data[i].isComplete)
                        plantLists.add(data[i])
                }

                if (plantLists.size != 0) {
                    for (i in plantLists.indices) {
                        setPage(plantLists[i].isComplete, plantLists[i].plantKind)
                    }
                    binding.vpImageSlider.adapter?.notifyDataSetChanged()
                    setPlantData(0)
                }
                else {
                    Toast.makeText(this@GardenActivity, "화단에 꽃이 비어 있어요", Toast.LENGTH_SHORT).show()
                    setEmpty()
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
            wateringPlant()
        }

        binding.btnDeletePlant.setOnClickListener {
            deletePlant()
        }
    }

    // 식물 물주기
    private fun wateringPlant() {
            if (plantLists[currentPage].plantType == "ACCUMULATE_TIMER") {
                if(ApplicationClass.mSharedPreferences.getBoolean("startTimer_FLAG",true)){
                    val intent= Intent(applicationContext,accumulateTimerService::class.java)
                    intent.putExtra("plantId",plantLists[currentPage].id)
                    intent.putExtra("plantName",plantLists[currentPage].name)
                    intent.putExtra("timerTotalMin",plantLists[currentPage].timerTotalMin)
                    intent.putExtra("timerCurMin",plantLists[currentPage].timerCurrentMin)
                    startForegroundService(intent)
                }else{
                    Toast.makeText(this@GardenActivity,"현재 다른 식물이 타이머를 사용중입니다.",Toast.LENGTH_SHORT).show()
                }
            }
            else if(plantLists[currentPage].plantType=="RECURSIVE_TIMER"){
                if(ApplicationClass.mSharedPreferences.getBoolean("startTimer_FLAG",true)){
                    val intent= Intent(applicationContext,recursiveTimerService::class.java)
                    intent.putExtra("plantId",plantLists[currentPage].id)
                    intent.putExtra("plantName",plantLists[currentPage].name)
                    intent.putExtra("timerRecurMin",plantLists[currentPage].timerRecurMin)
                    startForegroundService(intent)
                }else{
                    Toast.makeText(this@GardenActivity,"현재 다른 식물이 타이머를 사용중입니다.",Toast.LENGTH_SHORT).show()
                }
            }else if(plantLists[currentPage].plantType=="COUNTER"){
                if(ApplicationClass.mSharedPreferences.getLong("${SharedPreferenceManager().getString("email")}${plantLists[currentPage].gardenId}${plantLists[currentPage].id}",0)+300000>System.currentTimeMillis()){ //아직 시간이 안됨.
                    Toast.makeText(applicationContext,"기다려야할 시간이 남았습니다.",Toast.LENGTH_SHORT).show()
                }else{
                    ApplicationClass.mSharedPreferences.edit().putLong("${SharedPreferenceManager().getString("email")}${plantLists[currentPage].gardenId}${plantLists[currentPage].id}",System.currentTimeMillis()+1000).commit()

                    ApplicationClass.retrofitManager.plantWatering(plantLists[currentPage].id, object :
                        RetrofitCallback {
                        override fun onError(t: Throwable) {
                            Log.d(TAG, "onError : " + t.localizedMessage)
                        }

                        override fun onSuccess(message: String, data: String) {
                            Log.d(TAG, "onSuccess : message -> $message")
                            Log.d(TAG, "onSuccess : data -> $data")

                            plantLists[currentPage].counter += 1
                            if (plantLists[currentPage].counter == plantLists[currentPage].counterGoal) {
                                plantLists.removeAt(currentPage)
                                sliderItems.removeAt(currentPage)
                                binding.vpImageSlider.adapter?.notifyDataSetChanged()

                                if (plantLists.isEmpty())
                                    setEmpty()
                            }
                            else
                                setPlantData(currentPage)
                        }

                        override fun onFailure(errorMessage: String, errorCode: Int) {
                            Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                            Log.d(TAG, "onFailure : errorCode -> $errorCode")
                        }
                    })

                    count_featureTimer=Timer()
                    count_featureTimer.schedule(counter(plantLists[currentPage].id,(ApplicationClass.mSharedPreferences.getLong("${SharedPreferenceManager().getString("email")}${plantLists[currentPage].gardenId}${plantLists[currentPage].id}",0)+300000-System.currentTimeMillis()),binding.tvCounterLimitText, count_featureTimer).createTimerTask(), 0, 1000)
                }
            }else if(plantLists[currentPage].plantType=="WALK_COUNTER"){
                val intent= Intent(applicationContext, PedometerService::class.java)
                intent.putExtra("plantId",plantLists[currentPage].id)
                intent.putExtra("walkStep", plantLists[currentPage].walkStep)
                startForegroundService(intent)
            }
            else {
                ApplicationClass.retrofitManager.plantWatering(plantLists[currentPage].id, object : RetrofitCallback {
                    override fun onError(t: Throwable) {
                        Log.d(TAG, "onError : " + t.localizedMessage)
                    }

                    override fun onSuccess(message: String, data: String) {
                        Log.d(TAG, "onSuccess : message -> $message")
                        Log.d(TAG, "onSuccess : data -> $data")

                        plantLists.removeAt(currentPage)
                        sliderItems.removeAt(currentPage)
                        binding.vpImageSlider.adapter?.notifyDataSetChanged()

                        if (plantLists.isEmpty())
                            setEmpty()
                    }

                    override fun onFailure(errorMessage: String, errorCode: Int) {
                        Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                        Log.d(TAG, "onFailure : errorCode -> $errorCode")
                        Toast.makeText(this@GardenActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                })
            }
    }

    // 식물 삭제
    private fun deletePlant() {
        ApplicationClass.retrofitManager.plantDelete(plantLists[currentPage].id, object : RetrofitCallback {
            override fun onError(t: Throwable) {
                Log.d(TAG, "onError : " + t.localizedMessage)
            }

            override fun onSuccess(message: String, data: String) {
                Log.d(TAG, "onSuccess : message -> $message")
                Log.d(TAG, "onSuccess : data -> $data")
                Toast.makeText(this@GardenActivity, message, Toast.LENGTH_SHORT).show()

                plantLists.removeAt(currentPage)
                sliderItems.removeAt(currentPage)
                binding.vpImageSlider.adapter?.notifyDataSetChanged()

                if (plantLists.isEmpty())
                    setEmpty()
            }

            override fun onFailure(errorMessage: String, errorCode: Int) {
                Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                Log.d(TAG, "onFailure : errorCode -> $errorCode")
                Toast.makeText(this@GardenActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 식물 사진 set
    @SuppressLint("ResourceType")
    private fun setPage(isComplete : Boolean, plantKind : Int) {
        if (isComplete) {
            when (plantKind) {
                1 -> sliderItems.add(R.drawable.plant1)
                2 -> sliderItems.add(R.drawable.plant2)
                3 -> sliderItems.add(R.drawable.plant3)
                4 -> sliderItems.add(R.drawable.plant4)
                5 -> sliderItems.add(R.drawable.plant5)
                6 -> sliderItems.add(R.drawable.plant6)
                7 -> sliderItems.add(R.drawable.plant7)
                8 -> sliderItems.add(R.drawable.plant8)
                9 -> sliderItems.add(R.drawable.plant9)
                10 -> sliderItems.add(R.drawable.plant10)
                11 -> sliderItems.add(R.drawable.plant11)
                12 -> sliderItems.add(R.drawable.plant12)
                13 -> sliderItems.add(R.drawable.plant13)
                14 -> sliderItems.add(R.drawable.plant14)
                15 -> sliderItems.add(R.drawable.plant15)
                16 -> sliderItems.add(R.drawable.plant16)
                17 -> sliderItems.add(R.drawable.plant17)
                18 -> sliderItems.add(R.drawable.plant18)
                19 -> sliderItems.add(R.drawable.plant19)
                20 -> sliderItems.add(R.drawable.plant20)
                21 -> sliderItems.add(R.drawable.plant21)
                22 -> sliderItems.add(R.drawable.plant22)
                23 -> sliderItems.add(R.drawable.plant23)
                24 -> sliderItems.add(R.drawable.plant24)
                25 -> sliderItems.add(R.drawable.plant25)
                26 -> sliderItems.add(R.drawable.plant26)
                27 -> sliderItems.add(R.drawable.plant27)
                28 -> sliderItems.add(R.drawable.plant28)
                29 -> sliderItems.add(R.drawable.plant29)
                30 -> sliderItems.add(R.drawable.plant30)
                31 -> sliderItems.add(R.drawable.plant31)
                32 -> sliderItems.add(R.drawable.plant32)
                33 -> sliderItems.add(R.drawable.plant33)
                34 -> sliderItems.add(R.drawable.plant34)
                35 -> sliderItems.add(R.drawable.plant35)
                36 -> sliderItems.add(R.drawable.plant36)
                37 -> sliderItems.add(R.drawable.plant37)
            }
        }
        else {
            sliderItems.add(R.drawable.pot)
        }
    }

    // 식물 상세정보 set
    private fun setPlantData(position: Int) {
        if (plantLists.isNotEmpty()) {
            when (plantLists[position].plantType) {
                "CHECKBOX" -> {
                    binding.tvPlantNameValue.text = plantLists[position].name
                    binding.tvPlantTypeValue.text = "체크박스"
                    binding.tvPlantCompleteValue.text = if (plantLists[position].isComplete) " O " else " X "
                    binding.tvPlantStartDateValue.text = plantLists[position].context1
                    binding.tvPlantEndDateValue.text = plantLists[position].context2

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
                    binding.tvPlantStartDateValue.text = plantLists[position].context1
                    binding.tvPlantEndDateValue.text = plantLists[position].context2
                    binding.tvPlantPedometerGoalStepValue.text = plantLists[position].walkStep.toString() + " 걸음"
                    binding.tvPlantPedometerGoalCountValue.text =  plantLists[position].counter.toString() + " / " + plantLists[position].counterGoal.toString() + " 회"

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
                    binding.tvPlantStartDateValue.text = plantLists[position].context1
                    binding.tvPlantEndDateValue.text = plantLists[position].context2
                    binding.tvPlantCounterValue.text = plantLists[position].counter.toString() + " / " + plantLists[position].counterGoal.toString() + " 회"

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
                "ACCUMULATE_TIMER" -> {
                    binding.tvPlantNameValue.text = plantLists[position].name
                    binding.tvPlantTypeValue.text = "누적 타이머"
                    binding.tvPlantCompleteValue.text = if (plantLists[position].isComplete) " O " else " X "
                    binding.tvPlantStartDateValue.text = plantLists[position].context1
                    binding.tvPlantEndDateValue.text = plantLists[position].context2
                    binding.tvPlantTimerAccumulateValue.text = plantLists[position].timerCurrentMin.toString() + " / " + plantLists[position].timerTotalMin.toString() + " 분"

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
                    binding.tvPlantStartDateValue.text = plantLists[position].context1
                    binding.tvPlantEndDateValue.text = plantLists[position].context2
                    binding.tvPlantTimerRecursiveValue.text = plantLists[position].timerRecurMin.toString() + " 분"
                    binding.tvPlantTimerRecursiveCountValue.text = plantLists[position].counter.toString() + " / " + plantLists[position].counterGoal.toString() + " 회"

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

    // 식물 비어있을 때 식물 세부정보 설정
    fun setEmpty() {
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