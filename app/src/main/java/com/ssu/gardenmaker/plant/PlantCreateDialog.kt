package com.ssu.gardenmaker.plant

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.databinding.DialogCreateplantBinding
import com.ssu.gardenmaker.retrofit.callback.RetrofitCallback
import com.ssu.gardenmaker.util.SharedPreferenceManager
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Container가 아닌 check박스에 이벤트 주기
/*
문제1. 다이얼러그를 꺼도 메모리에 남아 있어서인지, 입력하던 정보가 그대로 남아 있다.
문제2. 다이얼러그에서 기능을 선택하고 세부계획을 정하고, 다른 기능으로 넘어갔다가 다시 해당 기능으로 돌아오면 입력 정보가 그대로 있다.
        예시: 만보기->횟수->만보기 클릭시, 기존에 입력했던 만보기 정보가 그대로 남아있다.
* */
class PlantCreateDialog(context: Context, layoutInflater: LayoutInflater): View.OnClickListener {

    private val TAG = "PlantCreateDialog"
    private val mContext = context
    private val dialog = Dialog(context)
    private val currentTime = System.currentTimeMillis()
    private val binding = DialogCreateplantBinding.inflate(layoutInflater)

    // 체크박스 뷰
    private val checkbox1: CheckBox by lazy { binding.CheckboxCheck }
    private val checkbox2: CheckBox by lazy { binding.CheckboxPedometer }
    private val checkbox3: CheckBox by lazy { binding.CheckboxGoalcounter }
    private val checkbox4: CheckBox by lazy { binding.CheckboxTimerAccumulate }
    private val checkbox5: CheckBox by lazy { binding.CheckboxTimerCount }

    // 만보기 기능 뷰
    private val tvGoalStepParameter: TextView by lazy { binding.GoalStepPedometerTvDialog }
    private val btnGoalParameter: Button by lazy { binding.GoalStepPedometerBtnDialog }
    private val tvGoalCountParameter: TextView by lazy { binding.GoalCountPedometerTvDialog }
    private val btnGoalCountParameter: Button by lazy { binding.GoalCountPedometerBtnDialog }

    // 횟수 기능 뷰
    private val tvGoalCountCounter: TextView by lazy { binding.GoalCountCounterTvDialog }
    private val btnGoalCountCounter: Button by lazy { binding.GoalCountCounterBtnDialog }

    // 누적 타이머 기능 뷰
    private val tvGoalTimerAccumulate: TextView by lazy { binding.GoalTimerAccumulateTvDialog }
    private val btnGoalTimerAccumulate: Button by lazy { binding.GoalTimerAccumulateBtnDialog }

    // 반복 타이머 기능 뷰
    private val tvGoalTimerRecursive: TextView by lazy { binding.GoalTimerRecursiveTvDialog }
    private val btnGoalTimerRecursive: Button by lazy { binding.GoalTimerRecursiveBtnDialog }
    private val tvGoalCountTimerRecursive: TextView by lazy { binding.GoalCountTimerRecursiveTvDialog}
    private val btnGoalCountTimerRecursive: Button by lazy { binding.GoalCountTimerRecursiveBtnDialog }

    // 주의 사항 텍스트
    private val tvPrecaution: TextView by lazy { binding.PrecautionTvDialog }
    private val tvPrecautionEx: TextView by lazy { binding.PrecautionExTvDialog }

    fun showDialog(){
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.show()

        valListener()
        plantFunction()
    }

    private fun valListener() {
        // 시작 날짜, 종료 날짜 리스너(DatePicker Spinner모드)
        val listener_day = View.OnClickListener { v-> v as Button
            //i는 년도, i2는 (월-1), i3은 일을 표기한다.
            val datePickerDialog = DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth, DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    if (i2 + 1 < 10) {
                        if (i3 < 10)
                            v.text="" + i + "년 0"+ (i2 + 1) + "월 0" + i3 + "일"
                        else
                            v.text="" + i + "년 0"+ (i2 + 1) + "월 " + i3 + "일"
                    }
                    else {
                        if (i3 < 10)
                            v.text="" + i + "년 "+ (i2 + 1) + "월 0" + i3 + "일"
                        else
                            v.text="" + i + "년 "+ (i2 + 1) + "월 " + i3 + "일"
                    }
                }, SimpleDateFormat("yyyy").format(currentTime).toInt(),
                SimpleDateFormat("MM").format(currentTime).toInt(), SimpleDateFormat("dd").format(currentTime).toInt())
            datePickerDialog.datePicker.calendarViewShown=false
            datePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            datePickerDialog.show()
        }
        binding.StartDayDialog.setOnClickListener(listener_day)
        binding.EndDayDialog.setOnClickListener(listener_day)


        // 상단의 저장, 뒤로가기 리스너
        binding.okBtnDialog.setOnClickListener {
            if (binding.PlainNameEdtDialog.text.toString().trim().isEmpty()) {
                Toast.makeText(mContext, "꽃 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else if (binding.CategoryBtnDialog.text.toString().trim().isEmpty()) {
                Toast.makeText(mContext, "화단을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
            else if (binding.StartDayDialog.text.toString() == "-" || binding.EndDayDialog.text.toString() == "-") {
                Toast.makeText(mContext, "목표 기간을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                var categoryId = -1
                for (i in 0 until ApplicationClass.categoryLists.size) {
                    if (binding.CategoryBtnDialog.text.equals(ApplicationClass.categoryLists[i].name))
                        categoryId = ApplicationClass.categoryLists[i].id
                }

                val dateFormat = SimpleDateFormat("yyyyMMdd")
                val todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                val startDate = dateFormat.parse(binding.StartDayDialog.text.toString().replace("[^0-9]".toRegex(), ""))
                val endDate = dateFormat.parse(binding.EndDayDialog.text.toString().replace("[^0-9]".toRegex(), ""))
                val days : Int = ((endDate!!.time - startDate!!.time) / (1000 * 24 * 60 * 60)).toInt() + 1

                var context1=binding.StartDayDialog.text.toString().replace(" ","").replace("년","-").replace("월","-").replace("일","")
                var context2=binding.EndDayDialog.text.toString().replace(" ","").replace("년","-").replace("월","-").replace("일","")
                if (Integer.parseInt(todayDate) > Integer.parseInt(binding.StartDayDialog.text.toString().replace("[^0-9]".toRegex(), ""))) {
                    Toast.makeText(mContext, "시작 날짜는 오늘 날짜보다 빠를 수 없습니다", Toast.LENGTH_SHORT).show()
                }
                else if (Integer.parseInt(binding.StartDayDialog.text.toString().replace("[^0-9]".toRegex(), ""))
                    > Integer.parseInt(binding.EndDayDialog.text.toString().replace("[^0-9]".toRegex(), ""))) {
                    Toast.makeText(mContext, "종료 날짜는 시작 날짜보다 빠를 수 없습니다", Toast.LENGTH_SHORT).show()
                }
                if (checkbox1.isChecked) {
                    ApplicationClass.retrofitManager.plantCreate(categoryId, "CHECKBOX", binding.PlainNameEdtDialog.text.toString(), days,
                        0, 0, 0, 0, 0, 0,context1,context2 ,object : RetrofitCallback {
                            override fun onError(t: Throwable) {
                                Log.d(TAG, "onError : " + t.localizedMessage)
                            }

                            override fun onSuccess(message: String, data: String) {
                                Log.d(TAG, "onSuccess : message -> $message")
                                Log.d(TAG, "onSuccess : data -> $data")
                                dialog.dismiss()
                            }

                            override fun onFailure(errorMessage: String, errorCode: Int) {
                                Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                                Log.d(TAG, "onFailure : errorCode -> $errorCode")
                                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        })
                }
                else if (checkbox2.isChecked) {
                    // 만보기 식물이 존재하는지 확인
                    ApplicationClass.retrofitManager.pedometerCheck(object : RetrofitCallback {
                        override fun onError(t: Throwable) {
                            Log.d(TAG, "onError : " + t.localizedMessage)
                            SharedPreferenceManager().setString("pedometerHave", "YES")
                        }

                        override fun onSuccess(message: String, data: String) {
                            Log.d(TAG, "onSuccess : message -> $message")
                            Log.d(TAG, "onSuccess : data -> $data")
                        }

                        override fun onFailure(errorMessage: String, errorCode: Int) {
                            Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                            Log.d(TAG, "onFailure : errorCode -> $errorCode")

                            if (errorMessage == "존재하는 만보계 식물이 없습니다.")
                                SharedPreferenceManager().setString("pedometerHave", "NO")
                        }
                    })

                    if (SharedPreferenceManager().getString("pedometerHave").equals("YES")) {
                        Toast.makeText(mContext, "이미 만보기 식물이 존재합니다", Toast.LENGTH_SHORT).show()
                    }
                    else if (binding.GoalStepPedometerBtnDialog.text.toString() == "-" || binding.GoalCountPedometerBtnDialog.text.toString() == "-")
                        Toast.makeText(mContext, "세부 계획을 입력해주세요", Toast.LENGTH_SHORT).show()
                    else {
                        val walkStep = Integer.parseInt(binding.GoalStepPedometerBtnDialog.text.toString().replace("[^0-9]".toRegex(), ""))
                        val walkCounterGoal = Integer.parseInt(binding.GoalCountPedometerBtnDialog.text.toString().replace("[^0-9]".toRegex(), ""))

                        ApplicationClass.retrofitManager.plantCreate(categoryId, "WALK_COUNTER", binding.PlainNameEdtDialog.text.toString(), days,
                            walkStep, walkCounterGoal, 0, 0, 0, 0, context1,context2 ,object : RetrofitCallback {
                                override fun onError(t: Throwable) {
                                    Log.d(TAG, "onError : " + t.localizedMessage)
                                }

                                override fun onSuccess(message: String, data: String) {
                                    Log.d(TAG, "onSuccess : message -> $message")
                                    Log.d(TAG, "onSuccess : data -> $data")
                                    dialog.dismiss()
                                }

                                override fun onFailure(errorMessage: String, errorCode: Int) {
                                    Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                                    Log.d(TAG, "onFailure : errorCode -> $errorCode")
                                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }
                else if (checkbox3.isChecked) {
                    if (binding.GoalCountCounterBtnDialog.text.toString() == "-")
                        Toast.makeText(mContext, "세부 계획을 입력해주세요", Toast.LENGTH_SHORT).show()
                    else {
                        val countCounterGoal = Integer.parseInt(binding.GoalCountCounterBtnDialog.text.toString().replace("[^0-9]".toRegex(), ""))

                        ApplicationClass.retrofitManager.plantCreate(categoryId, "COUNTER", binding.PlainNameEdtDialog.text.toString(), days,
                            0, countCounterGoal, 0, 0, 0, 0, context1,context2 ,object : RetrofitCallback {
                                override fun onError(t: Throwable) {
                                    Log.d(TAG, "onError : " + t.localizedMessage)
                                }

                                override fun onSuccess(message: String, data: String) {
                                    Log.d(TAG, "onSuccess : message -> $message")
                                    Log.d(TAG, "onSuccess : data -> $data")
                                    dialog.dismiss()
                                }

                                override fun onFailure(errorMessage: String, errorCode: Int) {
                                    Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                                    Log.d(TAG, "onFailure : errorCode -> $errorCode")
                                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            })
                    }
                }
                else if (checkbox4.isChecked) {
                    if (binding.GoalTimerAccumulateBtnDialog.text.toString() == "-")
                        Toast.makeText(mContext, "세부 계획을 입력해주세요", Toast.LENGTH_SHORT).show()
                    else {
                        val timeSplit = binding.GoalTimerAccumulateBtnDialog.text.toString().split(" ")

                        if (Integer.parseInt(timeSplit[0]) < 24 && Integer.parseInt(timeSplit[2]) < 60) {
                            val timerTotalMin = Integer.parseInt(timeSplit[0]) * 60 + Integer.parseInt(timeSplit[2])

                            ApplicationClass.retrofitManager.plantCreate(categoryId, "ACCUMULATE_TIMER", binding.PlainNameEdtDialog.text.toString(), days,
                                0, 0, timerTotalMin, 0, 0, 0, context1,context2 ,object : RetrofitCallback {
                                    override fun onError(t: Throwable) {
                                        Log.d(TAG, "onError : " + t.localizedMessage)
                                    }

                                    override fun onSuccess(message: String, data: String) {
                                        Log.d(TAG, "onSuccess : message -> $message")
                                        Log.d(TAG, "onSuccess : data -> $data")
                                        dialog.dismiss()
                                    }

                                    override fun onFailure(errorMessage: String, errorCode: Int) {
                                        Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                                        Log.d(TAG, "onFailure : errorCode -> $errorCode")
                                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                        else {
                            Toast.makeText(mContext, "시간 형식이 잘못되었습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else if (checkbox5.isChecked) {
                    if (binding.GoalTimerRecursiveBtnDialog.text.toString() == "-" || binding.GoalCountTimerRecursiveBtnDialog.text.toString() == "-")
                        Toast.makeText(mContext, "세부 계획을 입력해주세요", Toast.LENGTH_SHORT).show()
                    else {
                        val timeSplit = binding.GoalTimerRecursiveBtnDialog.text.toString().split(" ")
                        if (Integer.parseInt(timeSplit[0]) < 24 && Integer.parseInt(timeSplit[2]) < 60) {
                            val timerRecurMin = Integer.parseInt(timeSplit[0]) * 60 + Integer.parseInt(timeSplit[2])
                            val timerCounterGoal = Integer.parseInt(binding.GoalCountTimerRecursiveBtnDialog.text.toString().replace("[^0-9]".toRegex(), ""))

                            ApplicationClass.retrofitManager.plantCreate(categoryId, "RECURSIVE_TIMER", binding.PlainNameEdtDialog.text.toString(), days,
                                0, timerCounterGoal, 0, timerRecurMin, 0, 0, context1,context2 ,object : RetrofitCallback {
                                    override fun onError(t: Throwable) {
                                        Log.d(TAG, "onError : " + t.localizedMessage)
                                    }

                                    override fun onSuccess(message: String, data: String) {
                                        Log.d(TAG, "onSuccess : message -> $message")
                                        Log.d(TAG, "onSuccess : data -> $data")
                                        dialog.dismiss()
                                    }

                                    override fun onFailure(errorMessage: String, errorCode: Int) {
                                        Log.d(TAG, "onFailure : errorMessage -> $errorMessage")
                                        Log.d(TAG, "onFailure : errorCode -> $errorCode")
                                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                        else {
                            Toast.makeText(mContext, "시간 형식이 잘못되었습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.backBtnDialog.setOnClickListener {
            dialog.dismiss()
        }

        // 체크 박스 리스너
        checkbox1.setOnClickListener(this)
        checkbox2.setOnClickListener(this)
        checkbox3.setOnClickListener(this)
        checkbox4.setOnClickListener(this)
        checkbox5.setOnClickListener(this)

        // 카테고리 선택 버튼
        binding.CategoryBtnDialog.setOnClickListener {
            val plantSelectCategoryDialog = PlantSelectCategoryDialog(mContext, ApplicationClass.categoryLists, binding.CategoryBtnDialog)
            plantSelectCategoryDialog.showDialog()
        }
    }

    private fun plantFunction() {
        // 만보기
        btnGoalParameter.setOnClickListener { v-> v as Button
            val dialog_goalparameter=Dialog(mContext)
            dialog_goalparameter.setContentView(R.layout.dialog_pedometer)
            dialog_goalparameter.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_goalparameter.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_goalparameter.findViewById<Button>(R.id.ok_btn_dialog_parameter).setOnClickListener {
                if (v.text.toString().trim().isNotEmpty() && v.text.toString().trim() != "0" && v.text.toString().trim() != "")
                    v.text = DecimalFormat("###,###").format(dialog_goalparameter.findViewById<EditText>(R.id.et_pedometer_dialog).text.toString().trim().toInt())+" 걸음"
                else
                    v.text="-"
                dialog_goalparameter.dismiss()
            }
            dialog_goalparameter.show()
        }

        val listener_count = View.OnClickListener{ v-> v as Button
            val dialog_goalcount=Dialog(mContext)
            dialog_goalcount.setContentView(R.layout.dialog_goalcount)
            dialog_goalcount.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_goalcount.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_goalcount.findViewById<Button>(R.id.ok_btn_dialog_parameter).setOnClickListener {
                val et=dialog_goalcount.findViewById<EditText>(R.id.et_goalcount_dialog)
                if (et.text.toString().trim().isNotEmpty() && et.text.toString().trim() != "0" && et.text.toString().trim() != "")
                    v.text = et.text.toString().trim() + " 회"
                else
                    v.text="-"
                dialog_goalcount.dismiss()
            }
            dialog_goalcount.show()
        }
        btnGoalCountParameter.setOnClickListener(listener_count)

        // 횟수
        btnGoalCountCounter.setOnClickListener(listener_count)

        // 누적 타이머
        btnGoalTimerAccumulate.setOnClickListener { v-> v as Button
            val dialog_accumulatetime=Dialog(mContext)
            dialog_accumulatetime.setContentView(R.layout.dialog_accumulatetime)
            dialog_accumulatetime.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_accumulatetime.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_accumulatetime.findViewById<Button>(R.id.ok_btn_dialog_parameter).setOnClickListener {
                val et1_str=dialog_accumulatetime.findViewById<EditText>(R.id.et1_accumulatetime_dialog).text.toString()
                val et2_str=dialog_accumulatetime.findViewById<EditText>(R.id.et2_accumulatetime_dialog).text.toString()
                if ((et1_str.trim().isEmpty() && et2_str.trim().isEmpty()) || (et1_str.trim() == "0" && et2_str.trim() == "0"))
                    v.text="-"
                else if (et1_str.trim().isEmpty() && et2_str.trim() != "0")
                    v.text="0 시간 $et2_str 분"
                else if (et2_str.trim().isEmpty() && et1_str.trim() != "0")
                    v.text="$et1_str 시간 0 분"
                else
                    v.text= "$et1_str 시간 $et2_str 분"
                dialog_accumulatetime.dismiss()
            }
            dialog_accumulatetime.show()
        }

        // 반복 타이머
        // 예외: 반복 시간을 spinner가 아닌, text로 입력하고 바로 확인을 누르면 적용이 되지 않는다.
        btnGoalTimerRecursive.setOnClickListener { v-> v as Button
            val timepicker_dialog=TimePickerDialog(mContext,android.R.style.Theme_Holo_Dialog_NoActionBar,TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                if ((i.toString().trim().isEmpty() && i2.toString().trim().isEmpty()) || (i.toString().trim() == "0" && i2.toString().trim() == "0"))
                    v.text="-"
                else if (i.toString().trim().isEmpty() && i2.toString().trim() != "0")
                    v.text="0 시간 $i2 분"
                else if (i2.toString().trim().isEmpty() && i.toString().trim() != "0")
                    v.text="$i 시간 0 분"
                else
                    v.text= "$i 시간 $i2 분"
                }, 0, 30,true)
            timepicker_dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timepicker_dialog.show()
        }
        btnGoalCountTimerRecursive.setOnClickListener(listener_count)
    }

    override fun onClick(p0: View?) {
        checkbox1.isChecked=false
        checkbox2.isChecked=false
        checkbox3.isChecked=false
        checkbox4.isChecked=false
        checkbox5.isChecked=false

        tvGoalTimerAccumulate.visibility=View.GONE
        btnGoalTimerAccumulate.visibility=View.GONE

        tvGoalTimerRecursive.visibility=View.GONE
        btnGoalTimerRecursive.visibility=View.GONE
        tvGoalCountTimerRecursive.visibility=View.GONE
        btnGoalCountTimerRecursive.visibility=View.GONE

        tvGoalStepParameter.visibility=View.GONE
        btnGoalParameter.visibility=View.GONE
        tvGoalCountParameter.visibility=View.GONE
        btnGoalCountParameter.visibility=View.GONE

        tvGoalCountCounter.visibility=View.GONE
        btnGoalCountCounter.visibility=View.GONE

        when (p0?.id) {
            checkbox1.id-> {
                checkbox1.isChecked=true
                tvPrecaution.text="체크박스를 이용한 계획을 입력해주세요"
                tvPrecautionEx.text="예시) 정보처리기사 취득하기"
            }
            checkbox2.id->{
                checkbox2.isChecked=true
                tvGoalStepParameter.visibility=View.VISIBLE
                btnGoalParameter.visibility=View.VISIBLE
                tvGoalCountParameter.visibility=View.VISIBLE
                btnGoalCountParameter.visibility=View.VISIBLE
                tvPrecaution.text="목표 걸음 수와 목표 달성 횟수를 입력해주세요"
                tvPrecautionEx.text="예시) 한 달 내에 10,000걸음 10회 달성하기"
            }
            checkbox3.id->{
                checkbox3.isChecked=true
                tvGoalCountCounter.visibility=View.VISIBLE
                btnGoalCountCounter.visibility=View.VISIBLE
                tvPrecaution.text="목표 달성 횟수를 입력해주세요"
                tvPrecautionEx.text="예시) 금연을 위해 담배 50번 참기"
            }
            checkbox4.id->{
                checkbox4.isChecked=true
                tvGoalTimerAccumulate.visibility=View.VISIBLE
                btnGoalTimerAccumulate.visibility=View.VISIBLE
                tvPrecaution.text="목표 누적 시간을 입력해주세요"
                tvPrecautionEx.text="예시) 한 달 내에 목표를 위해 총 120시간 수행하기"
            }
            checkbox5.id->{
                checkbox5.isChecked=true
                tvGoalTimerRecursive.visibility=View.VISIBLE
                btnGoalTimerRecursive.visibility=View.VISIBLE
                tvGoalCountTimerRecursive.visibility=View.VISIBLE
                btnGoalCountTimerRecursive.visibility=View.VISIBLE
                tvPrecaution.text="반복해서 수행할 목표 시간을 입력해주세요"
                tvPrecautionEx.text="예시) 한 달 내에 1시간 30회 목표 수행하기"
            }
        }
    }

//    private fun storeFeature(
//        name: String, type: String, startDate: String, endDate: String,            // 공통 값
//        stepPedometer: String = "", countPedometer: String = "",                   // 만보기
//        countCounter: String = "",                                                 // 횟수
//        timerAccumulate: String = "",                                              // 누적타이머
//        timerRecursive: String = "", countTimerRecursive: String = "") {           // 반복타이머
//
//            val editor = ApplicationClass.mSharedPreferences.edit()
//            val gson = GsonBuilder().create()
//            val data = PlantData(
//               name, type, startDate, endDate,
//               stepPedometer, countPedometer,
//               countCounter,
//               timerAccumulate,
//               timerRecursive, countTimerRecursive
//            )
//
//            val tempArray = ArrayList<PlantData>()
//            val groupListType: Type = object :
//                TypeToken<ArrayList<PlantData?>?>() {}.type // json 을 객체로 만들 때 타입을 추론하는 역할
//            val prev = ApplicationClass.mSharedPreferences.getString(
//                binding.CategoryBtnDialog.text.toString(),
//                "NONE"
//            ) // json list 가져오기
//
//            if (prev != "NONE") {
//                if (prev != "[]" || prev != "")
//                    tempArray.addAll(gson.fromJson(prev, groupListType))
//                tempArray.add(data)
//                val strList = gson.toJson(tempArray, groupListType)
//                editor.putString(
//                    binding.CategoryBtnDialog.text.toString(),
//                    strList
//                )
//            } else {
//                tempArray.add(data)
//                val strList = gson.toJson(tempArray, groupListType)
//                editor.putString(
//                    binding.CategoryBtnDialog.text.toString(),
//                    strList
//                )
//            }
//            editor.apply()
//            dialog.dismiss()
//    }
}