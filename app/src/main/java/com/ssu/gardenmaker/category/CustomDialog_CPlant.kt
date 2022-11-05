package com.ssu.gardenmaker.category

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.ssu.gardenmaker.R
import com.ssu.gardenmaker.databinding.DialogCreateplantBinding
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_OFF
import kotlinx.coroutines.currentCoroutineContext
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

// Container가 아닌 check박스에 이벤트 주기
/*
문제1. 다이얼러그를 꺼도 메모리에 남아 있어서인지, 입력하던 정보가 그대로 남아 있다.
문제2. 다이얼러그에서 기능을 선택하고 세부계획을 정하고, 다른 기능으로 넘어갔다가 다시 해당 기능으로 돌아오면 입력 정보가 그대로 있다.
        예시: 만보기->횟수->만보기 클릭시, 기존에 입력했던 만보기 정보가 그대로 남아있다.
* */
class CustomDialog_CPlant(context: Context, layoutinflater: LayoutInflater): View.OnClickListener {

    private val context1=context
    private val dialog=Dialog(context)
    private val currentTime=System.currentTimeMillis()
    private val binding=DialogCreateplantBinding.inflate(layoutinflater)

    //체크박스 뷰
    val checkbox1:CheckBox by lazy{
        binding.CheckboxCheck
    }
    val checkbox2:CheckBox by lazy{
        binding.CheckboxTimerAccumulate
    }
    val checkbox3:CheckBox by lazy{
        binding.CheckboxTimerCount
    }
    val checkbox4:CheckBox by lazy{
        binding.CheckboxPedometer
    }
    val checkbox5:CheckBox by lazy{
        binding.CheckboxGoalcounter
    }
    //체크박스 기능 뷰
    val checkbox_alarm_tv:TextView by lazy{
        binding.CheckboxAlarmTvDialog
    }
    val checkbox_alarm_btn:Button by lazy{
        binding.CheckboxAlarmBtnDialog
    }
    val checkbox_alarmtimer_tv:TextView by lazy{
        binding.CheckboxAlarmTimerTvDialog
    }
    val checkbox_alarmtimer_btn:Button by lazy{
        binding.CheckboxAlarmTimerBtnDialog
    }
    //만보기 기능 뷰
    val goalparameter_tv:TextView by lazy{
        binding.GoalParmeterTvDialog
    }
    val goalparameter_btn:Button by lazy{
        binding.GoalParameterBtnDialog
    }
    val countparameter_tv:TextView by lazy{
        binding.CountParameterTvDialog
    }
    val countparameter_btn:Button by lazy{
        binding.CountParameterBtnDialog
    }
    //누적 타이머 기능  뷰
    val goaltimer_accumulate_tv:TextView by lazy{
        binding.GoalTimerAccumulateTvDialog
    }
    val goaltimer_accumulate_btn:Button by lazy{
        binding.GoalTimerAccumulateBtnDialog
    }
    //횟수 타이머 기능 뷰
    val goaltimer_count_tv:TextView by lazy{
        binding.GoalTimerCountTvDialog
    }
    val goaltimer_count_btn:Button by lazy{
        binding.GoalTimerCountBtnDialog
    }
    val goal_count_tv:TextView by lazy{
        binding.GoalCountTvDialog
    }
    val goal_count_btn:Button by lazy{
        binding.GoalCountBtnDialog
    }
    //횟수 기능 뷰
    val countgoalcounter_tv:TextView by lazy{
        binding.CountGoalcounterTvDialog
    }
    val countgoalcounter_btn:Button by lazy{
        binding.CountGoalcounterBtnDialog
    }
    //주의 사항 텍스트
    val precaution_tv:TextView by lazy{
        binding.PrecautionTvDialog
    }
    val precautionex_tv:TextView by lazy{
        binding.PrecautionExTvDialog
    }

    fun showDialog(){
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val_listener()
        five_fuction()
    }

    fun val_listener(){
        //시작 날짜, 종료 날짜 리스너(DatePicker Spinner모드)
        var listener_day=View.OnClickListener { v->
            v as Button
            var datePickerDialog=DatePickerDialog(context1,android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                DatePickerDialog.OnDateSetListener { datePicker, i, i2, i3 ->
                    //i는 년도, i2는 (월-1), i3은 일을 표기한다.
                    v.text=""+i+"년 "+(i2+1)+"월 "+i3+"일 "
                },SimpleDateFormat("yyyy").format(currentTime).toInt(),SimpleDateFormat("MM").format(currentTime).toInt(),SimpleDateFormat("dd").format(currentTime).toInt())
            datePickerDialog.datePicker.calendarViewShown=false
            datePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            datePickerDialog.show()
        }
        binding.StartDayDialog.setOnClickListener(listener_day)
        binding.EndDayDialog.setOnClickListener(listener_day)


        //상단의 저장, 뒤로가기 리스너
        binding.okBtnDialog.setOnClickListener {
            dialog.dismiss()
        }
        binding.backBtnDialog.setOnClickListener {
            dialog.dismiss()
        }


        //체크 박스 리스너
        checkbox1.setOnClickListener(this)
        checkbox2.setOnClickListener(this)
        checkbox3.setOnClickListener(this)
        checkbox4.setOnClickListener(this)
        checkbox5.setOnClickListener(this)
    }

    fun five_fuction(){
        //1. 체크박스 알람
        checkbox_alarm_btn.setOnClickListener { v->
            v as Button
            if(v.text.equals("ON"))
                v.text="OFF"
            else
                v.text="ON"
        }
        checkbox_alarmtimer_btn.setOnClickListener {v->
            v as Button
            if(checkbox_alarm_btn.text.equals("ON")){
                var timepicker_dialog=TimePickerDialog(context1,android.R.style.Theme_Holo_Dialog_NoActionBar,TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                    if(i.toString().equals("0")&&i2.toString().equals("0")){
                        v.text="-"
                    }else{
                        v.text=""+i+": "+i2
                    }},SimpleDateFormat("HH").format(currentTime).toInt(), SimpleDateFormat("mm").format(currentTime).toInt(),false)
                timepicker_dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                timepicker_dialog.show()
            }else{
                Toast.makeText(context1,"알람을 클릭하여 ON모드로 바꿔주세요",Toast.LENGTH_SHORT).show()
            }
        }


        //2. 만보기
        goalparameter_btn.setOnClickListener { v->
            v as Button
            var dialog_goalparameter=Dialog(context1)
            dialog_goalparameter.setContentView(R.layout.dialog_pedometer)
            dialog_goalparameter.findViewById<Button>(R.id.ok_btn_dialog_paramter).setOnClickListener {
                if(v.text.toString().length!=0){
                    v.text= DecimalFormat("###,###").format(dialog_goalparameter.findViewById<EditText>(R.id.et_pedometer_dialog).text.toString().toInt())+"걸음"
                }else{
                    v.text="-"
                }
                dialog_goalparameter.dismiss()
            }
            dialog_goalparameter.show()
        }
        var listener_count=View.OnClickListener{v->
            v as Button
            var dialog_goalcount=Dialog(context1)
            dialog_goalcount.setContentView(R.layout.dialog_goalcount)
            dialog_goalcount.findViewById<Button>(R.id.ok_btn_dialog_paramter).setOnClickListener {
                var et=dialog_goalcount.findViewById<EditText>(R.id.et_goalcount_dialog)
                if(et.text.toString().length!=0&&!et.text.toString().equals("0")) {
                    v.text =et.text.toString() + "회"
                }else{
                    v.text="-"
                }
                dialog_goalcount.dismiss()
            }
            dialog_goalcount.show()
        }
        countparameter_btn.setOnClickListener(listener_count)


        //3. 횟수 타이머
        //예외: 반복 시간을 spinner가 아닌, text로 입력하고 바로 확인을 누르면 적용이 되지 않는다.
        goaltimer_count_btn.setOnClickListener { v->
            v as Button
            var timepicker_dialog=TimePickerDialog(context1,android.R.style.Theme_Holo_Dialog_NoActionBar,TimePickerDialog.OnTimeSetListener { timePicker, i, i2 ->
                if(i.toString().equals("0")&&i2.toString().equals("0")){
                 v.text="-"
                }else{
                    v.text=""+i+": "+i2
                }}, 0, 30,true)
            timepicker_dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            timepicker_dialog.show()
        }
        goal_count_btn.setOnClickListener(listener_count)


        //4. 누적 타이머
        goaltimer_accumulate_btn.setOnClickListener { v->
            v as Button
            var dialog_accumulatetime=Dialog(context1)
            dialog_accumulatetime.setContentView(R.layout.dialog_accumulatetime)
            dialog_accumulatetime.findViewById<Button>(R.id.ok_btn_dialog_paramter).setOnClickListener {
                var et1_str=dialog_accumulatetime.findViewById<EditText>(R.id.et1_accumulatetime_dialog).text.toString()
                var et2_str=dialog_accumulatetime.findViewById<EditText>(R.id.et2_accumulatetime_dialog).text.toString()
                if(et1_str.length==0&&et2_str.length==0){
                    v.text="-"
                }else if(et1_str.length==0){
                    v.text="00:"+et2_str
                }else if(et2_str.length==0){
                    v.text=et1_str+":00"
                }else{
                    v.text=et1_str+":"+et2_str
                }
                dialog_accumulatetime.dismiss()
            }
            dialog_accumulatetime.show()
        }

        //5. 횟수
        countgoalcounter_btn.setOnClickListener(listener_count)
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

        when(p0?.id){
            checkbox1.id-> {
                checkbox1.isChecked=true
                checkbox_alarm_tv.visibility=View.VISIBLE
                checkbox_alarm_btn.visibility=View.VISIBLE
                checkbox_alarmtimer_tv.visibility=View.VISIBLE
                checkbox_alarmtimer_btn.visibility=View.VISIBLE
                precaution_tv.text=""
                precautionex_tv.text="식물을 만들지 않고 홈 화면에 따로 모아 둡니다."
            }
            checkbox2.id->{
                checkbox2.isChecked=true
                goaltimer_accumulate_tv.visibility=View.VISIBLE
                goaltimer_accumulate_btn.visibility=View.VISIBLE
                precaution_tv.text="총 누적시간을 입력해주세요."
                precautionex_tv.text="예시)한달내에 목표를 위해 총 120시간 수행하기."
            }
            checkbox3.id->{
                checkbox3.isChecked=true
                goaltimer_count_tv.visibility=View.VISIBLE
                goaltimer_count_btn.visibility=View.VISIBLE
                goal_count_tv.visibility=View.VISIBLE
                goal_count_btn.visibility=View.VISIBLE
                precaution_tv.text="반복해서 수행할 시간을 입력해주세요."
                precautionex_tv.text="예시)한달내에 1시간 30회 목표 수행하기"
            }
            checkbox4.id->{
                checkbox4.isChecked=true
                goalparameter_tv.visibility=View.VISIBLE
                goalparameter_btn.visibility=View.VISIBLE
                countparameter_tv.visibility=View.VISIBLE
                countparameter_btn.visibility=View.VISIBLE
                precaution_tv.text="목표 걸음수와 달성 횟수를 입력해주세요."
                precautionex_tv.text="예시)한달내에 10,000걸음 10회 달성하기"
            }
            checkbox5.id->{
                checkbox5.isChecked=true
                countgoalcounter_tv.visibility=View.VISIBLE
                countgoalcounter_btn.visibility=View.VISIBLE
                precaution_tv.text="목표 횟수를 입력해주세요."
                precautionex_tv.text="예시)금연을 위해 담배 50번 참기"
            }
        }
    }
}