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
import com.ssu.gardenmaker.ApplicationClass
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.databinding.DialogCreateplantBinding
import com.ssu.gardenmaker.db.ContractDB
import com.ssu.gardenmaker.db.ContractDB.Companion.COUNT_Checkbox_TB
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
    private val checkbox_alarm_tv: TextView by lazy { binding.CheckboxAlarmTvDialog }
    private val checkbox_alarm_btn: Button by lazy { binding.CheckboxAlarmBtnDialog }
    private val checkbox_alarmtimer_tv: TextView by lazy { binding.CheckboxAlarmTimerTvDialog }
    private val checkbox_alarmtimer_btn: Button by lazy { binding.CheckboxAlarmTimerBtnDialog }
    private val checkbox_endday_tv: TextView by lazy { binding.tvEndDay }
    private val checkbox_endday_btn: TextView by lazy { binding.EndDayDialog }

    // 만보기 기능 뷰
    private val goalparameter_tv: TextView by lazy { binding.GoalParmeterTvDialog }
    private val goalparameter_btn: Button by lazy { binding.GoalParameterBtnDialog }
    private val countparameter_tv: TextView by lazy { binding.CountParameterTvDialog }
    private val countparameter_btn: Button by lazy { binding.CountParameterBtnDialog }

    // 누적 타이머 기능  뷰
    private val goaltimer_accumulate_tv: TextView by lazy { binding.GoalTimerAccumulateTvDialog }
    private val goaltimer_accumulate_btn: Button by lazy { binding.GoalTimerAccumulateBtnDialog }

    // 횟수 타이머 기능 뷰
    private val goaltimer_count_tv: TextView by lazy { binding.GoalTimerCountTvDialog }
    private val goaltimer_count_btn: Button by lazy { binding.GoalTimerCountBtnDialog }
    private val goal_count_tv: TextView by lazy { binding.GoalCountTvDialog }
    private val goal_count_btn: Button by lazy { binding.GoalCountBtnDialog }

    // 횟수 기능 뷰
    private val countgoalcounter_tv: TextView by lazy { binding.CountGoalcounterTvDialog }
    private val countgoalcounter_btn: Button by lazy { binding.CountGoalcounterBtnDialog }

    // 주의 사항 텍스트
    private val precaution_tv: TextView by lazy { binding.PrecautionTvDialog }
    private val precautionex_tv: TextView by lazy { binding.PrecautionExTvDialog }

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
            val datePickerDialog = DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    //i는 년도, i2는 (월-1), i3은 일을 표기한다.
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
                    if (binding.StartDayDialog.text.toString() == "-")
                        Toast.makeText(mContext, "목표 기간을 입력해주세요", Toast.LENGTH_SHORT).show()
                    else {
                        // 체크박스 하루에 다섯 개까지 설정 가능하게 코드 추가

                        Toast.makeText(mContext, "체크박스 선택", Toast.LENGTH_SHORT).show()
                        val regex = "[^0-9]".toRegex()
                        val start_day = binding.StartDayDialog.text.toString().replace(regex, "").toInt()

                        // id로 cursor.count+1 을 하면서 부여하려 했으나 count=1 문제
                        val cursor = ApplicationClass.db.rawQuery(COUNT_Checkbox_TB, null)

                        ApplicationClass.db.execSQL(
                            ContractDB.insertCheckboxTB(
                                (Math.random() * 10).toInt() + 1,
                                binding.PlainNameEdtDialog.text.toString(),
                                start_day,
                                "N"
                            )
                        )
                    }
                } else if (checkbox2.isChecked) {
                    Toast.makeText(mContext, "만보기 선택", Toast.LENGTH_SHORT).show()
                    ApplicationClass.db.execSQL(
                        ContractDB.insertCalendarTB(
                            "개나리",
                            "건강",
                            "만보기",
                            20221101,
                            20221201
                        )
                    )
                } else if (checkbox3.isChecked) {
                    Toast.makeText(mContext, "횟수 선택", Toast.LENGTH_SHORT).show()
                } else if (checkbox4.isChecked) {
                    Toast.makeText(mContext, "누적 타이머 선택", Toast.LENGTH_SHORT).show()
                } else if (checkbox5.isChecked) {
                    Toast.makeText(mContext, "횟수 타이머 선택", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mContext, "아무것도 선택 X", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
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
        checkbox_alarm_btn.setOnClickListener { v->
            v as Button
            if(v.text.equals("ON"))
                v.text="OFF"
            else
                v.text="ON"
        }

        checkbox_alarmtimer_btn.setOnClickListener {v->
            v as Button
            if (checkbox_alarm_btn.text.equals("ON")) {
                val timepicker_dialog=TimePickerDialog(mContext,android.R.style.Theme_Holo_Dialog_NoActionBar,
                    { timePicker, i, i2 ->
                        if (i.toString().trim() == "0" && i2.toString().trim() == "0") {
                            v.text="-"
                        }
                        else {
                            if (i < 10) {
                                if (i2 < 10)
                                    v.text= "0$i : 0$i2"
                                else
                                    v.text= "0$i : $i2"
                            }
                            else {
                                if (i2 < 10)
                                    v.text= "$i : 0$i2"
                                else
                                    v.text= "$i : $i2"
                            }
                        }}, SimpleDateFormat("HH").format(currentTime).toInt(), SimpleDateFormat("mm").format(currentTime).toInt(),false)
                timepicker_dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                timepicker_dialog.show()
            }else{
                Toast.makeText(mContext,"알람을 클릭하여 ON 모드로 바꿔주세요",Toast.LENGTH_SHORT).show()
            }
        }


        // 2. 만보기
        goalparameter_btn.setOnClickListener { v->
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
        countparameter_btn.setOnClickListener(listener_count)

        // 3. 횟수
        countgoalcounter_btn.setOnClickListener(listener_count)

        // 4. 누적 타이머
        goaltimer_accumulate_btn.setOnClickListener { v->
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

        // 5. 횟수 타이머
        // 예외: 반복 시간을 spinner가 아닌, text로 입력하고 바로 확인을 누르면 적용이 되지 않는다.
        goaltimer_count_btn.setOnClickListener { v->
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
        goal_count_btn.setOnClickListener(listener_count)
    }

    override fun onClick(p0: View?) {
        checkbox1.isChecked=false
        checkbox2.isChecked=false
        checkbox3.isChecked=false
        checkbox4.isChecked=false
        checkbox5.isChecked=false

        checkbox_alarm_tv.visibility=View.GONE
        checkbox_alarm_btn.visibility=View.GONE
        checkbox_alarmtimer_tv.visibility=View.GONE
        checkbox_alarmtimer_btn.visibility=View.GONE
        checkbox_endday_tv.visibility=View.GONE
        checkbox_endday_btn.visibility=View.GONE

        goaltimer_accumulate_tv.visibility=View.GONE
        goaltimer_accumulate_btn.visibility=View.GONE

        goaltimer_count_tv.visibility=View.GONE
        goaltimer_count_btn.visibility=View.GONE
        goal_count_tv.visibility=View.GONE
        goal_count_btn.visibility=View.GONE

        goalparameter_tv.visibility=View.GONE
        goalparameter_btn.visibility=View.GONE
        countparameter_tv.visibility=View.GONE
        countparameter_btn.visibility=View.GONE

        countgoalcounter_tv.visibility=View.GONE
        countgoalcounter_btn.visibility=View.GONE

        when (p0?.id) {
            checkbox1.id-> {
                checkbox1.isChecked=true
                checkbox_alarm_tv.visibility=View.VISIBLE
                checkbox_alarm_btn.visibility=View.VISIBLE
                checkbox_alarmtimer_tv.visibility=View.VISIBLE
                checkbox_alarmtimer_btn.visibility=View.VISIBLE
                precaution_tv.text="오늘 하루의 계획을 입력해주세요"
                precautionex_tv.text="화단에 저장되지 않고 홈 화면에 따로 모아 둡니다"
                checkbox_endday_tv.visibility=View.GONE
                checkbox_endday_btn.visibility=View.GONE
            }
            checkbox2.id->{
                checkbox2.isChecked=true
                goalparameter_tv.visibility=View.VISIBLE
                goalparameter_btn.visibility=View.VISIBLE
                countparameter_tv.visibility=View.VISIBLE
                countparameter_btn.visibility=View.VISIBLE
                precaution_tv.text="목표 걸음 수와 목표 달성 횟수를 입력해주세요"
                precautionex_tv.text="예시) 한 달 내에 10,000걸음 10회 달성하기"
                checkbox_endday_tv.visibility=View.VISIBLE
                checkbox_endday_btn.visibility=View.VISIBLE
            }
            checkbox3.id->{
                checkbox3.isChecked=true
                countgoalcounter_tv.visibility=View.VISIBLE
                countgoalcounter_btn.visibility=View.VISIBLE
                precaution_tv.text="목표 달성 횟수를 입력해주세요"
                precautionex_tv.text="예시) 금연을 위해 담배 50번 참기"
                checkbox_endday_tv.visibility=View.VISIBLE
                checkbox_endday_btn.visibility=View.VISIBLE
            }
            checkbox4.id->{
                checkbox4.isChecked=true
                goaltimer_accumulate_tv.visibility=View.VISIBLE
                goaltimer_accumulate_btn.visibility=View.VISIBLE
                precaution_tv.text="목표 누적 시간을 입력해주세요"
                precautionex_tv.text="예시) 한 달 내에 목표를 위해 총 120시간 수행하기"
                checkbox_endday_tv.visibility=View.VISIBLE
                checkbox_endday_btn.visibility=View.VISIBLE
            }
            checkbox5.id->{
                checkbox5.isChecked=true
                goaltimer_count_tv.visibility=View.VISIBLE
                goaltimer_count_btn.visibility=View.VISIBLE
                goal_count_tv.visibility=View.VISIBLE
                goal_count_btn.visibility=View.VISIBLE
                precaution_tv.text="반복해서 수행할 목표 시간을 입력해주세요"
                precautionex_tv.text="예시) 한 달 내에 1시간 30회 목표 수행하기"
                checkbox_endday_tv.visibility=View.VISIBLE
                checkbox_endday_btn.visibility=View.VISIBLE
            }
        }
    }
}