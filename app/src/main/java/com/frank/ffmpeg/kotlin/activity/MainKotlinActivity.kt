package com.frank.ffmpeg.kotlin.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View

import com.frank.ffmpeg.R

/**
 * 使用ffmpeg进行音视频处理入口
 * Created by frank on 2018/1/23.
 */
class MainKotlinActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewsWithClick(
                R.id.btn_audio,
                R.id.btn_video,
                R.id.btn_media,
                R.id.btn_play,
                R.id.btn_push,
                R.id.btn_live,
                R.id.btn_filter,
                R.id.btn_preview,
                R.id.btn_probe
        )
        Log.e("MainKotlinActivity", "Welcome to kotlin...")
    }

    public override fun onViewClick(view: View) {
        val intent = Intent()
        when (view.id) {
            R.id.btn_audio//音频处理
            -> intent.setClass(this@MainKotlinActivity, AudioHandleActivity::class.java)
            R.id.btn_video//视频处理
            -> intent.setClass(this@MainKotlinActivity, VideoHandleActivity::class.java)
            R.id.btn_media//音视频处理
            -> intent.setClass(this@MainKotlinActivity, MediaHandleActivity::class.java)
            R.id.btn_play//音视频播放
            -> intent.setClass(this@MainKotlinActivity, MediaPlayerActivity::class.java)
            R.id.btn_push//FFmpeg推流
            -> intent.setClass(this@MainKotlinActivity, PushActivity::class.java)
            R.id.btn_live//实时推流直播:AAC音频编码、H264视频编码、RTMP推流
            -> intent.setClass(this@MainKotlinActivity, LiveActivity::class.java)
            R.id.btn_filter//滤镜特效
            -> intent.setClass(this@MainKotlinActivity, FilterActivity::class.java)
            R.id.btn_preview//视频拖动实时预览
            -> intent.setClass(this@MainKotlinActivity, VideoPreviewActivity::class.java)
            R.id.btn_probe//解析音视频多媒体格式
            -> intent.setClass(this@MainKotlinActivity, ProbeFormatActivity::class.java)
            else -> {
            }
        }
        startActivity(intent)
    }

    override fun onSelectedFile(filePath: String) {

    }

}
