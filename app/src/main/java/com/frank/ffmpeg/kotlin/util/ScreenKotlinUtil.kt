package com.frank.ffmpeg.kotlin.util

import android.app.Service
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

object ScreenKotlinUtil {

    private fun getDisplayMetrics(context : Context) : DisplayMetrics {
        val windowManager : WindowManager = context.getSystemService(Service.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

    fun getScreenWidth(context: Context?) : Int {
        if (context == null) {
            return 0
        }
        val displayMetrics : DisplayMetrics = getDisplayMetrics(context)
        return displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context?) : Int {
        if (context == null) {
            return 0
        }
        val displayMetrics : DisplayMetrics = getDisplayMetrics(context)
        return displayMetrics.heightPixels
    }

}