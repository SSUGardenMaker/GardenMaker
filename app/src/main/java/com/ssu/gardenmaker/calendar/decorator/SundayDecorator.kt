package com.ssu.gardenmaker.calendar.decorator


import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.util.Log
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import java.time.LocalDate
import java.util.*

class SundayDecorator :DayViewDecorator {
    var calendar= Calendar.getInstance()
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        day?.copyTo(calendar)
        return calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(Color.RED))
    }
}