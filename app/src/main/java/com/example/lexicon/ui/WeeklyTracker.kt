package com.example.lexicon.ui

import java.util.Calendar
import org.json.JSONArray
import org.json.JSONObject

object WeeklyTracker {
    fun getStartOfWeekMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return calendar.timeInMillis
    }
    
    fun getEndOfWeekMillis(startOfWeekMillis: Long): Long {
        return startOfWeekMillis + (7L * 24 * 60 * 60 * 1000) - 1
    }
}
