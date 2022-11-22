package com.ssu.gardenmaker.plant

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.databinding.DialogCreateplantBinding
import com.ssu.gardenmaker.db.ContractDB
import com.ssu.gardenmaker.db.ContractDB.Companion.COUNT_Checkbox_TB
import java.lang.reflect.Type
import java.text.DecimalFormat
import java.text.SimpleDateFormat

// Container가 아닌 check박스에 이벤트 주기
/*
문제1. 다이얼러그를 꺼도 메모리에 남아 있어서인지, 입력하던 정보가 그대로 남아 있다.
문제2. 다이얼러그에서 기능을 선택하고 세부계획을 정하고, 다른 기능으로 넘어갔다가 다시 해당 기능으로 돌아오면 입력 정보가 그대로 있다.
        예시: 만보기->횟수->만보기 클릭시, 기존에 입력했던 만보기 정보가 그대로 남아있다.
* */
class PlantCreateDialog(context: Context, layoutInflater: LayoutInflater): View.OnClickListener {

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

    // 체크박스 기능 뷰
    private val tvCheckboxAlarm: TextView by lazy { binding.CheckboxAlarmTvDialog }
    private val btnCheckboxAlarm: Button by lazy { binding.CheckboxAlarmBtnDialog }
    private val tvCheckboxAlarmTimer: TextView by lazy { binding.CheckboxAlarmTimerTvDialog }
    private val btnCheckboxAlarmTimer: Button by lazy { binding.CheckboxAlarmTimerBtnDialog }
    private val tvCheckboxEndDay: TextView by lazy { binding.tvEndDay }
    private val btnCheckboxEndDay: TextView by lazy { binding.EndDayDialog }

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
        fiveFuction()
    }

    private fun valListener() {
        // 시작 날짜, 종료 날짜 리스너(DatePicker Spinner모드)
        val listener_day = View.OnClickListener { v->
            v as Button
            val datePickerDialog = DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth, DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 -> //i는 년도, i2는 (월-1), i3은 일을 표기한다.
                    v.text="" + i + "년 "+ (i2 + 1) + "월 " + i3 + "일"
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
            else {
                if (checkbox1.isChecked) {
                    var count = 0
                    val cursor = ApplicationClass.db.rawQuery(COUNT_Checkbox_TB, null)
                    while (cursor.moveToNext()) {
                        count = cursor.getInt(0)
                    }

//                    // 체크박스 하루에 다섯 개까지 설정 가능하게 코드 추가 (오늘 날짜 비교해서 오늘 날짜로 된 것이 5개 이상일 때)
//                    if (count >= 5)
//                        Toast.makeText(mContext, "하루에 체크박스는 5개까지만 생성할 수 있습니다.", Toast.LENGTH_SHORT).show()

                    if (binding.StartDayDialog.text.toString() == "-")
                        Toast.makeText(mContext, "목표 기간을 입력해주세요", Toast.LENGTH_SHORT).show()
                    else {
                        val regex = "[^0-9]".toRegex()
                        val startDay =
                            binding.StartDayDialog.text.toString().replace(regex, "").toInt()

                        ApplicationClass.db.execSQL(
                            ContractDB.insertCheckboxTB(
                                count + 1,
                                binding.PlainNameEdtDialog.text.toString(),
                                startDay,
                                "N"
                            )
                        )

                        ApplicationClass.db.execSQL(
                            ContractDB.insertCalendarTB(
                                binding.PlainNameEdtDialog.text.toString(),
                                "-",
                                "체크박스",
                                startDay,
                                startDay
                            )
                        )

                        dialog.dismiss()
                    }
                }
                else {
                    if (binding.CategoryBtnDialog.text.toString().trim().isEmpty()) {
                        Toast.makeText(mContext, "화단을 선택해주세요", Toast.LENGTH_SHORT).show()
                    }
                    else if (binding.StartDayDialog.text.toString() == "-" || binding.EndDayDialog.text.toString() == "-") {
                        Toast.makeText(mContext, "목표 기간을 입력해주세요", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        if (checkbox2.isChecked) {
                            if (binding.GoalStepPedometerBtnDialog.text.toString() == "-" || binding.GoalCountPedometerBtnDialog.text.toString() == "-")
                                toast()
                            else
                                storeFeature( binding.PlainNameEdtDialog.text.toString(),"만보기", binding.StartDayDialog.text.toString(), binding.EndDayDialog.text.toString(),
                                        stepPedometer =  binding.GoalStepPedometerBtnDialog.text.toString(), countPedometer = binding.GoalCountPedometerBtnDialog.text.toString())

                        } else if (checkbox3.isChecked) {
                            if (binding.GoalCountCounterBtnDialog.text.toString() == "-")
                                toast()
                            else
                                storeFeature( binding.PlainNameEdtDialog.text.toString(),"횟수", binding.StartDayDialog.text.toString(), binding.EndDayDialog.text.toString(),
                                        countCounter =  binding.GoalCountCounterBtnDialog.text.toString())

                        } else if (checkbox4.isChecked) {
                            if (binding.GoalTimerAccumulateBtnDialog.text.toString() == "-")
                                toast()
                            else
                                storeFeature( binding.PlainNameEdtDialog.text.toString(),"누적 타이머", binding.StartDayDialog.text.toString(), binding.EndDayDialog.text.toString(),
                                        timerAccumulate =  binding.GoalTimerAccumulateBtnDialog.text.toString())

                        } else if (checkbox5.isChecked) {
                            if (binding.GoalTimerRecursiveBtnDialog.text.toString() == "-" || binding.GoalCountTimerRecursiveBtnDialog.text.toString() == "-")
                                toast()
                            else
                                storeFeature( binding.PlainNameEdtDialog.text.toString(),"반복 타이머", binding.StartDayDialog.text.toString(), binding.EndDayDialog.text.toString(),
                                        timerRecursive = binding.GoalTimerRecursiveBtnDialog.text.toString(), countTimerRecursive = binding.GoalCountTimerRecursiveBtnDialog.text.toString())

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

    private fun fiveFuction() {
        // 1. 체크박스 알람
        btnCheckboxAlarm.setOnClickListener { v->
            v as Button
            v.text=if(v.text.equals("ON")) "OFF" else "ON"
        }

        btnCheckboxAlarmTimer.setOnClickListener { v->
            v as Button
            if (btnCheckboxAlarm.text.equals("ON")) {
                val timepicker_dialog=TimePickerDialog(mContext,android.R.style.Theme_Holo_Dialog_NoActionBar,{ timePicker, i, i2 ->
                        if (i.toString().trim() == "0" && i2.toString().trim() == "0") {
                            v.text="-"
                        }
                        else {
                            if (i < 10) {
                                v.text=if(i2 < 10) "0$i : 0$i2" else "0$i : $i2"
                            }
                            else {
                                v.text=if(i2 < 10) "$i : 0$i2" else "$i : $i2"
                            }
                        }}, SimpleDateFormat("HH").format(currentTime).toInt(), SimpleDateFormat("mm").format(currentTime).toInt(),false)
                timepicker_dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                timepicker_dialog.show()
            }else{
                Toast.makeText(mContext,"알람을 클릭하여 ON 모드로 바꿔주세요",Toast.LENGTH_SHORT).show()
            }
        }


        // 2. 만보기
        btnGoalParameter.setOnClickListener { v->
            v as Button
            val dialog_goalparameter=Dialog(mContext)
            dialog_goalparameter.setContentView(R.layout.dialog_pedometer)
            dialog_goalparameter.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialog_goalparameter.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog_goalparameter.findViewById<Button>(R.id.ok_btn_dialog_parameter).setOnClickListener {
                if (v.text.toString().trim().isNotEmpty() && v.text.toString().trim() != "0")
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
                if (et.text.toString().trim().isNotEmpty() && et.text.toString().trim() != "0")
                    v.text = et.text.toString().trim() + " 회"
                else
                    v.text="-"
                dialog_goalcount.dismiss()
            }
            dialog_goalcount.show()
        }
        btnGoalCountParameter.setOnClickListener(listener_count)

        // 3. 횟수
        btnGoalCountCounter.setOnClickListener(listener_count)

        // 4. 누적 타이머
        btnGoalTimerAccumulate.setOnClickListener { v->
            v as Button
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
                    v.text="$et2_str 분"
                else if (et2_str.trim().isEmpty() && et1_str.trim() != "0")
                    v.text="$et1_str 시간"
                else
                    v.text= "$et1_str 시간 $et2_str 분"
                dialog_accumulatetime.dismiss()
            }
            dialog_accumulatetime.show()
        }

        // 5. 반복 타이머
        // 예외: 반복 시간을 spinner가 아닌, text로 입력하고 바로 확인을 누르면 적용이 되지 않는다.
        btnGoalTimerRecursive.setOnClickListener { v->
            v as Button
            val timepicker_dialog=TimePickerDialog(mContext,android.R.style.Theme_Holo_Dialog_NoActionBar,TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                if ((i.toString().trim().isEmpty() && i2.toString().trim().isEmpty()) || (i.toString().trim() == "0" && i2.toString().trim() == "0"))
                    v.text="-"
                else if (i.toString().trim().isEmpty() && i2.toString().trim() != "0")
                    v.text="$i2 분"
                else if (i2.toString().trim().isEmpty() && i.toString().trim() != "0")
                    v.text="$i 시간"
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

        tvCheckboxAlarm.visibility=View.GONE
        btnCheckboxAlarm.visibility=View.GONE
        tvCheckboxAlarmTimer.visibility=View.GONE
        btnCheckboxAlarmTimer.visibility=View.GONE
        tvCheckboxEndDay.visibility=View.GONE
        btnCheckboxEndDay.visibility=View.GONE

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
                tvCheckboxAlarm.visibility=View.VISIBLE
                btnCheckboxAlarm.visibility=View.VISIBLE
                tvCheckboxAlarmTimer.visibility=View.VISIBLE
                btnCheckboxAlarmTimer.visibility=View.VISIBLE
                tvPrecaution.text="오늘 하루의 계획을 입력해주세요"
                tvPrecautionEx.text="화단에 저장되지 않고 홈 화면에 따로 모아 둡니다"
                tvCheckboxEndDay.visibility=View.GONE
                btnCheckboxEndDay.visibility=View.GONE
            }
            checkbox2.id->{
                checkbox2.isChecked=true
                tvGoalStepParameter.visibility=View.VISIBLE
                btnGoalParameter.visibility=View.VISIBLE
                tvGoalCountParameter.visibility=View.VISIBLE
                btnGoalCountParameter.visibility=View.VISIBLE
                tvPrecaution.text="목표 걸음 수와 목표 달성 횟수를 입력해주세요"
                tvPrecautionEx.text="예시) 한 달 내에 10,000걸음 10회 달성하기"
                tvCheckboxEndDay.visibility=View.VISIBLE
                btnCheckboxEndDay.visibility=View.VISIBLE
            }
            checkbox3.id->{
                checkbox3.isChecked=true
                tvGoalCountCounter.visibility=View.VISIBLE
                btnGoalCountCounter.visibility=View.VISIBLE
                tvPrecaution.text="목표 달성 횟수를 입력해주세요"
                tvPrecautionEx.text="예시) 금연을 위해 담배 50번 참기"
                tvCheckboxEndDay.visibility=View.VISIBLE
                btnCheckboxEndDay.visibility=View.VISIBLE
            }
            checkbox4.id->{
                checkbox4.isChecked=true
                tvGoalTimerAccumulate.visibility=View.VISIBLE
                btnGoalTimerAccumulate.visibility=View.VISIBLE
                tvPrecaution.text="목표 누적 시간을 입력해주세요"
                tvPrecautionEx.text="예시) 한 달 내에 목표를 위해 총 120시간 수행하기"
                tvCheckboxEndDay.visibility=View.VISIBLE
                btnCheckboxEndDay.visibility=View.VISIBLE
            }
            checkbox5.id->{
                checkbox5.isChecked=true
                tvGoalTimerRecursive.visibility=View.VISIBLE
                btnGoalTimerRecursive.visibility=View.VISIBLE
                tvGoalCountTimerRecursive.visibility=View.VISIBLE
                btnGoalCountTimerRecursive.visibility=View.VISIBLE
                tvPrecaution.text="반복해서 수행할 목표 시간을 입력해주세요"
                tvPrecautionEx.text="예시) 한 달 내에 1시간 30회 목표 수행하기"
                tvCheckboxEndDay.visibility=View.VISIBLE
                btnCheckboxEndDay.visibility=View.VISIBLE
            }
        }
    }

    private fun storeFeature(
        name: String, type: String, startDate: String, endDate: String,            // 공통 값
        stepPedometer: String = "", countPedometer: String = "",                   // 만보기
        countCounter: String = "",                                                 // 횟수
        timerAccumulate: String = "",                                              // 누적타이머
        timerRecursive: String = "", countTimerRecursive: String = "") {           // 반복타이머

            val editor = ApplicationClass.mSharedPreferences.edit()
            val gson = GsonBuilder().create()
            val data = PlantData(
               name, type, startDate, endDate,
               stepPedometer, countPedometer,
               countCounter,
               timerAccumulate,
               timerRecursive, countTimerRecursive
            )

            val tempArray = ArrayList<PlantData>()
            val groupListType: Type = object :
                TypeToken<ArrayList<PlantData?>?>() {}.type // json 을 객체로 만들 때 타입을 추론하는 역할
            val prev = ApplicationClass.mSharedPreferences.getString(
                binding.CategoryBtnDialog.text.toString(),
                "NONE"
            ) // json list 가져오기

            if (prev != "NONE") {
                if (prev != "[]" || prev != "")
                    tempArray.addAll(gson.fromJson(prev, groupListType))
                tempArray.add(data)
                val strList = gson.toJson(tempArray, groupListType)
                editor.putString(
                    binding.CategoryBtnDialog.text.toString(),
                    strList
                )
            } else {
                tempArray.add(data)
                val strList = gson.toJson(tempArray, groupListType)
                editor.putString(
                    binding.CategoryBtnDialog.text.toString(),
                    strList
                )
            }
            editor.apply()
            dialog.dismiss()
    }

    private fun toast() {
        Toast.makeText(mContext, "세부 계획을 입력해주세요", Toast.LENGTH_SHORT).show()
    }
}