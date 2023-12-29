package com.android.alist.utils

import android.util.Log
import com.android.alist.utils.constant.AppConstant
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 *格式化时间为yyyy-MM-dd HH:mm:ss格式
 */
fun formatDateTime(inputDateTime: String): String {
    val instant = Instant.ofEpochSecond(Instant.parse(inputDateTime).epochSecond)
    val localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    return localDateTime.format(formatter)
}

fun convertTimeToDisplayString(time: String): String {
    val date = Date()
    date.time = time.toLong()
    //同一年 显示MM-dd HH:mm
    if (isSameYear(date)) {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.CHINA)
        //同一天，显示HH:mm
        if (isSameDay(date)) {
            val minute = isMinutesBefore(time.toLong())
            //1个小时之内，显示n分钟前
            return if (minute < 60) {
                //一分钟之内，显示刚刚
                if (minute <= 1) {
                    "刚刚"
                } else {
                    minute.toString() + "分钟前"
                }
            } else {
                dateFormat.format(date)
            }
        } else {
            //昨天，显示昨天+HH:mm
            if (isYesterday(date)) {
                return "昨天 " + dateFormat.format(date)
            } else if (isSameWeek(date)) {
                //本周，显示周几+HH:mm
                val weekday = when (dateFormat.calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.SUNDAY -> "周日"
                    Calendar.MONDAY -> "周一"
                    Calendar.TUESDAY -> "周二"
                    Calendar.WEDNESDAY -> "周三"
                    Calendar.THURSDAY -> "周四"
                    Calendar.FRIDAY -> "周五"
                    Calendar.SATURDAY -> "周六"
                    else -> ""
                }
                return weekday + " " + dateFormat.format(date)
            } else {
                //同一年 显示MM-dd
                val sdf = SimpleDateFormat("MM-dd", Locale.CHINA)
                return sdf.format(date)
            }
        }
    } else {
        //不是同一年，显示yyyy年
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
        return sdf.format(date)
    }
}

fun getLocalTime(): String {
    return System.currentTimeMillis().toString()
}

/**
 * 获取date第n天的日期
 */
private fun getNextDate(date: Date, n: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date
    calendar.add(Calendar.DATE, n)
    return calendar.time
}

/**
 * 格式化Date,判断是否相等
 */
private fun isEquals(date: Date, format: String): Boolean {
    //当前时间
    val now = Date()
    val formatter = SimpleDateFormat(format, Locale.CHINA)
    //获取今天时间
    val nowDay = formatter.format(now)
    //对比时间
    val day = formatter.format(date)
    return day.equals(nowDay)
}

/**
 * 判断与当前日期是否为同一年
 */
private fun isSameYear(date: Date): Boolean {
    return isEquals(date, "yyyy")
}

/**
 * 判断与当前日期是否为同一月
 */
private fun isSameMonth(date: Date): Boolean {
    return isEquals(date, "yyyy-MM")
}

/**
 * 判断与当前日期是否为同一天获取指定时间的后一天的日期，判断与当前日期是否是同一天
 */
private fun isSameDay(date: Date): Boolean {
    return isEquals(date, "yyyy-MM-dd")
}

/**
 * 是否为当前时间的昨天
 * 获取指定时间的后一天的日期，判断与当前日期是否是同一天
 */
private fun isYesterday(date: Date): Boolean {
    val yesterday = getNextDate(date, 1)
    return isSameDay(yesterday)
}

/**
 *与当前时间是否在同一周
 * 先判断是否在同一年，然后根据Calendar.DAY_OF_YEAR判断所得的周数是否一致
 */
private fun isSameWeek(date: Date): Boolean {
    if (isSameYear(date)) {
        val calendar = Calendar.getInstance()
        //西方周日为一周的第一天，将周一设为一周第一天
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.time = date

        val nowCalender = Calendar.getInstance()
        nowCalender.firstDayOfWeek = Calendar.MONDAY
        nowCalender.time = Date()

        if (calendar.get(Calendar.WEEK_OF_YEAR) == nowCalender.get(Calendar.WEEK_OF_YEAR)) {
            return true
        }
    } else {
        return false
    }
    return false
}

/**
 * 几分钟前
 */
private fun isMinutesBefore(time: Long): Int {
    return ((System.currentTimeMillis() - time) / (1000 * 60)).toInt()
}