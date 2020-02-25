package com.frank.ffmpeg.kotlin.activity

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.TextureView
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import android.widget.ToggleButton

import com.frank.ffmpeg.R
import com.frank.live.LivePusherNew
import com.frank.live.camera2.Camera2Helper
import com.frank.live.listener.LiveStateChangeListener
import com.frank.live.param.AudioParam
import com.frank.live.param.VideoParam

/**
 * h264与rtmp实时推流直播
 * Created by frank on 2018/1/28.
 */

class LiveActivity : BaseActivity(), CompoundButton.OnCheckedChangeListener, LiveStateChangeListener {
    private var textureView: TextureView? = null
    private var livePusher: LivePusherNew? = null
    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MSG_ERROR) {
                val errMsg = msg.obj as String
                if (!TextUtils.isEmpty(errMsg)) {
                    Toast.makeText(this@LiveActivity, errMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_live

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideActionBar()
        initView()
        initPusher()
    }

    private fun initView() {
        initViewsWithClick(R.id.btn_swap)
        (findViewById<View>(R.id.btn_live) as ToggleButton).setOnCheckedChangeListener(this)
        (findViewById<View>(R.id.btn_mute) as ToggleButton).setOnCheckedChangeListener(this)
        textureView = getView(R.id.surface_camera)
    }

    private fun initPusher() {
        val width = 640//分辨率设置
        val height = 480
        val videoBitRate = 400//kb/s
        val videoFrameRate = 20//fps
        val videoParam = VideoParam(width, height,
                Integer.valueOf(Camera2Helper.CAMERA_ID_BACK), videoBitRate, videoFrameRate)
        val sampleRate = 44100//采样率：Hz
        val channelConfig = AudioFormat.CHANNEL_IN_STEREO//立体声道
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT//pcm16位
        val numChannels = 2//声道数
        val audioParam = AudioParam(sampleRate, channelConfig, audioFormat, numChannels)
        livePusher = LivePusherNew(this@LiveActivity, videoParam, audioParam, textureView)
        //TODO:暂时去掉音频推流
        livePusher!!.setMute(true)
        findViewById<View>(R.id.btn_mute).visibility = View.INVISIBLE
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.btn_live//开始/停止直播
            -> if (isChecked) {
                livePusher!!.startPush(LIVE_URL, this)
            } else {
                livePusher!!.stopPush()
            }
            R.id.btn_mute//设置静音
            -> {
                Log.i(TAG, "isChecked=$isChecked")
                livePusher!!.setMute(isChecked)
            }
            else -> {
            }
        }
    }

    override fun onError(msg: String) {
        Log.e(TAG, "errMsg=$msg")
        mHandler.obtainMessage(MSG_ERROR, msg).sendToTarget()
    }

    override fun onDestroy() {
        super.onDestroy()
        //if (livePusher != null) {
        //    livePusher.release()
        //}
    }

    override fun onViewClick(view: View) {
        if (view.id == R.id.btn_swap) {//切换摄像头
            livePusher!!.switchCamera()
        }
    }

    override fun onSelectedFile(filePath: String) {

    }

    companion object {

        private val TAG = LiveActivity::class.java.simpleName
        private const val LIVE_URL = "rtmp://192.168.1.3/live/stream"
        private const val MSG_ERROR = 100
    }
}
