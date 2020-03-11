package com.frank.ffmpeg.kotlin.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 时间转换工具类
 * Created by frank on 2020/3/11.
 */

object TimeKotlinUtil {

    private const val YMDHMS = "yyyy-MM-dd HH:mm:ss"

    /**
     * 时间戳年月日时分秒
     * @param time time
     * @return 年月日时分秒 yyyy/MM/dd HH:mm:ss
     */
    fun getDetailTime(time: Long): String {
        val format = SimpleDateFormat(YMDHMS, Locale.getDefault())
        val date = Date(time)
        return format.format(date)
    }

    /**
     * 时间转为时间戳
     * @param time time
     * @return 时间戳
     */
    fun getLongTime(time: String, locale: Locale): Long {
        val simpleDateFormat = SimpleDateFormat(YMDHMS, locale)
        try {
            val dt = simpleDateFormat.parse(time)
            return dt.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }

    private fun addZero(time: Int): String {
        return when {
            time in 0..9 -> "0$time"
            time >= 10 -> "" + time
            else -> ""
        }
    }

    /**
     * 获取视频时长
     * @param time time
     * @return 视频时长
     */
    fun getVideoTime(time: Long): String? {
        if (time <= 0)
            return null
        var vTime = time / 1000
        val second: Int = vTime.toInt() % 60
        var minute = 0
        var hour = 0
        vTime /= 60
        if (vTime > 0) {
            minute = vTime.toInt() % 60
            hour = vTime.toInt() / 60
        }
        return when {
            hour > 0 -> {addZero(hour) + ":" + addZero(minute) + ":" + addZero(second)}
            minute > 0 -> {addZero(minute) + ":" + addZero(second)}
            else -> {"00:" + addZero(second)}
        }
    }

}
