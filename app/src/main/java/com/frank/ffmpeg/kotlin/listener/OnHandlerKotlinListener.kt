package com.frank.ffmpeg.kotlin.listener

/**
 * 流程执行监听器
 * Created by frank on 2020/3/4.
 */
interface OnHandlerKotlinListener {
    fun onBegin()
    fun onEnd(resultCode: Int, resultMsg: String)
}