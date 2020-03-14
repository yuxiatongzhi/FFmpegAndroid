package com.frank.ffmpeg.kotlin.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * bitmap工具类：文字转成图片
 * Created by frank on 2020/3/14.
 */

object BitmapKotlinUtil {

    private const val TEXT_SIZE = 16
    private const val TEXT_COLOR = Color.RED

    /**
     * 文本转成Bitmap
     * @param text 文本内容
     * @param context 上下文
     * @return 图片的bitmap
     */
    private fun textToBitmap(text: String, context: Context): Bitmap {
        val scale = context.resources.displayMetrics.scaledDensity
        val tv = TextView(context)
        val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        tv.layoutParams = layoutParams
        tv.text = text
        tv.textSize = scale * TEXT_SIZE
        tv.gravity = Gravity.CENTER_HORIZONTAL
        tv.isDrawingCacheEnabled = true
        tv.setTextColor(TEXT_COLOR)
        tv.setBackgroundColor(Color.WHITE)
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        tv.layout(0, 0, tv.measuredWidth, tv.measuredHeight)
        tv.buildDrawingCache()
        val bitmap = tv.drawingCache
        val rate = bitmap.height / 20
        return Bitmap.createScaledBitmap(bitmap, bitmap.width / rate, 20, false)
    }

    /**
     * 文字生成图片
     * @param filePath filePath
     * @param text text
     * @param context context
     * @return 生成图片是否成功
     */
    fun textToPicture(filePath: String, text: String, context: Context): Boolean {
        val bitmap = textToBitmap(text, context)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(filePath)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return true
    }

    /**
     * 删除源文件
     * @param filePath filePath
     * @return 删除是否成功
     */
    fun deleteTextFile(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists() && file.delete()
    }

}
