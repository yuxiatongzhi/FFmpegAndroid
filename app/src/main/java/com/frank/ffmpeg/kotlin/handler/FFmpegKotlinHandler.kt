package com.frank.ffmpeg.kotlin.handler

import android.os.Handler
import android.util.Log
import com.frank.ffmpeg.FFmpegCmd
import com.frank.ffmpeg.kotlin.model.MediaKotlinBean
import com.frank.ffmpeg.kotlin.tool.JsonParseKotlinTool
import com.frank.ffmpeg.listener.OnHandleListener

/**
 * Handler消息处理器
 * Created by frank on 2020/3/7.
 */
class FFmpegKotlinHandler(handler : Handler) {

    companion object {
        private val TAG = FFmpegKotlinHandler::class.java.simpleName

        const val MSG_BEGIN = 9012

        const val MSG_FINISH = 1112

        const val MSG_CONTINUE = 2012

        const val MSG_TOAST = 4562
    }

    private var mHandler : Handler? = null

    var isContinue : Boolean = false

    init {
        mHandler = handler
    }

    /**
     * 执行ffmpeg命令行
     * @param commandLine commandLine
     */
    fun executeFFmpegCmd(commandLine: Array<String>?) {
        if (commandLine == null) {
            return
        }
        FFmpegCmd.execute(commandLine, object : OnHandleListener {
            override fun onBegin() {
                Log.i(TAG, "handle onBegin...")
                mHandler!!.obtainMessage(MSG_BEGIN).sendToTarget()
            }

            override fun onEnd(resultCode: Int, resultMsg: String) {
                Log.i(TAG, "handle onEnd...")
                if (isContinue) {
                    mHandler!!.obtainMessage(MSG_CONTINUE).sendToTarget()
                } else {
                    mHandler!!.obtainMessage(MSG_FINISH).sendToTarget()
                }
            }
        })
    }

    /**
     * execute probe cmd
     * @param commandLine commandLine
     */
    fun executeFFprobeCmd(commandLine: Array<String>?) {
        if (commandLine == null) {
            return
        }
        FFmpegCmd.executeProbe(commandLine, object : OnHandleListener{
            override fun onBegin() {
                Log.i(TAG, "handle ffprobe onBegin...")
                mHandler!!.obtainMessage(MSG_BEGIN).sendToTarget()
            }

            override fun onEnd(resultCode: Int, resultMsg: String?) {
                Log.i(TAG, "handle ffprobe onEnd result=$resultMsg")
                var mediaBean : MediaKotlinBean? = null
                if (resultMsg != null) {
                    mediaBean = JsonParseKotlinTool.parseMediaFormat(resultMsg)
                }
                mHandler!!.obtainMessage(MSG_FINISH, mediaBean).sendToTarget()
            }
        })
    }

}