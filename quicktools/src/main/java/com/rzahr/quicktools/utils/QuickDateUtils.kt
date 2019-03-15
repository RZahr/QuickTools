@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.rzahr.quicktools.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Rashad Zahr
 *
 * object used as a helper for date-related functions
 */
object QuickDateUtils {

    const val DASHED_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val SLASHED_FORMAT = "dd/MM/yyyy hh:mm:ss a"

    /**
     * returns a string date
     * @param date the date
     * @param format the format
     * @return a date string
     */
    fun getDateString(date: Date, format: String = DASHED_FORMAT): String {

        val calendar = Calendar.getInstance()
        calendar.time = date
        return SimpleDateFormat(format, Locale.ENGLISH).format(calendar.time)
    }

    /**
     * get the current date in a string format
     * @param english the language if english
     * @param format the format
     * @return a date string
     */
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(english: Boolean = true, format: String = DASHED_FORMAT): String {

        val now = Date()
        return if (english)
            SimpleDateFormat(format, Locale.ENGLISH).format(now)
        else
            SimpleDateFormat(format).format(now)
    }

    /**
     * changes the time to string
     * @param dateLong the date
     * @param format the format
     * @param timeZone the timezone
     * @return date string
     */
    fun changeTimeToString(dateLong: Long, format: String = SLASHED_FORMAT, timeZone: TimeZone? = null): String {

        val simpleDateFormat = SimpleDateFormat(format, Locale.ENGLISH)
        if (timeZone != null) simpleDateFormat.timeZone = timeZone
        return simpleDateFormat.format(Date(dateLong).time)
    }
}