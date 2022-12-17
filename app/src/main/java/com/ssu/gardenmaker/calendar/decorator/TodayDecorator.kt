package com.ssu.gardenmaker.calendar.decorator

import android.content.Context
import android.graphics.Color
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.ssu.gardenmaker.R


class TodayDecorator(context:Context):DayViewDecorator {
    var context=context
    var date=CalendarDay.today()
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day?.equals(date)!!
    }

    override fun decorate(view: DayViewFacade?) {
        view?.setBackgroundDrawable(context.getDrawable(R.drawable.calendar_today_drawable)!!)
        view?.addSpan(RelativeSizeSpan(1.2f))
        view?.addSpan(ForegroundColorSpan(Color.GREEN))
    }
}