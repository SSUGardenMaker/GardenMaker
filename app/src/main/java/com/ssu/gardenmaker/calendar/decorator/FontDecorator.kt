package com.ssu.gardenmaker.calendar.decorator

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Typeface
import android.text.style.StyleSpan
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class FontDecorator(context: Context): DayViewDecorator {
    var context=context
    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return true
    }

    override fun decorate(view: DayViewFacade?) {
       view?.addSpan(StyleSpan(Typeface.BOLD_ITALIC))
    }
}