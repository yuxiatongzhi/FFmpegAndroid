package com.frank.ffmpeg.kotlin.activity

import android.annotation.SuppressLint
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar

import java.io.File
import java.util.ArrayList

import com.frank.ffmpeg.AudioPlayer
import com.frank.ffmpeg.R
import com.frank.ffmpeg.handler.FFmpegHandler
import com.frank.ffmpeg.mp3.Mp3Converter
import com.frank.ffmpeg.util.FFmpegUtil
import com.frank.ffmpeg.util.FileUtil

import com.frank.ffmpeg.handler.FFmpegHandler.MSG_BEGIN
import com.frank.ffmpeg.handler.FFmpegHandler.MSG_FINISH

/**
 * 使用ffmpeg处理音频
 * Created by frank on 2018/1/23.
 */

class AudioHandleActivity : BaseActivity() {
    private val appendFile = PATH + File.separator + "test.mp3"

    private var progressAudio: ProgressBar? = null
    private var layoutAudioHandle: LinearLayout? = null
    private var viewId: Int = 0
    private var ffmpegHandler: FFmpegHandler? = null

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_BEGIN -> {
                    progressAudio!!.visibility = View.VISIBLE
                    layoutAudioHandle!!.visibility = View.GONE
                }
                MSG_FINISH -> {
                    progressAudio!!.visibility = View.GONE
                    layoutAudioHandle!!.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_audio_handle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideActionBar()
        initView()
        ffmpegHandler = FFmpegHandler(mHandler)
    }

    private fun initView() {
        progressAudio = getView(R.id.progress_audio)
        layoutAudioHandle = getView(R.id.layout_audio_handle)
        initViewsWithClick(
                R.id.btn_transform,
                R.id.btn_cut,
                R.id.btn_concat,
                R.id.btn_mix,
                R.id.btn_play_audio,
                R.id.btn_play_opensl,
                R.id.btn_audio_encode,
                R.id.btn_pcm_concat
        )
    }

    public override fun onViewClick(view: View) {
        viewId = view.id
        selectFile()
    }

    override fun onSelectedFile(filePath: String) {
        doHandleAudio(filePath)
    }

    /**
     * 调用ffmpeg处理音频
     *
     * @param srcFile srcFile
     */
    private fun doHandleAudio(srcFile: String) {
        var commandLine: Array<String>? = null
        if (!FileUtil.checkFileExist(srcFile)) {
            return
        }
        if (!FileUtil.isAudio(srcFile)) {
            showToast(getString(R.string.wrong_audio_format))
            return
        }
        when (viewId) {
            R.id.btn_transform//转码
            -> if (useFFmpeg) { //使用FFmpeg转码
                val transformFile = PATH + File.separator + "transformAudio.mp3"
                commandLine = FFmpegUtil.transformAudio(srcFile, transformFile)
            } else { //使用MediaCodec与mp3lame转mp3
                Thread(Runnable {
                    val transformInput = PATH + File.separator + "transformAudio.mp3"
                    val mp3Converter = Mp3Converter()
                    mp3Converter.convertToMp3(srcFile, transformInput)
                }).start()
            }
            R.id.btn_cut//剪切(注意原文件与剪切文件格式一致，文件绝对路径最好不包含中文、特殊字符)
            -> {
                val suffix = FileUtil.getFileSuffix(srcFile)
                if (suffix == null || "" == suffix) {
                    return
                }
                val cutFile = PATH + File.separator + "cutAudio" + suffix
                commandLine = FFmpegUtil.cutAudio(srcFile, 10, 15, cutFile)
            }
            R.id.btn_concat//合并，支持MP3、AAC、AMR等，不支持PCM裸流，不支持WAV（PCM裸流加音频头）
            -> {
                if (!FileUtil.checkFileExist(appendFile)) {
                    return
                }
                val fileList = ArrayList<String>()
                fileList.add(srcFile)
                fileList.add(appendFile)
                val concatFile = PATH + File.separator + "concat.mp3"
                commandLine = FFmpegUtil.concatAudio(fileList, concatFile)
            }
            R.id.btn_mix//混音
            -> {
                if (!FileUtil.checkFileExist(appendFile)) {
                    return
                }
                val mixSuffix = FileUtil.getFileSuffix(srcFile)
                if (mixSuffix == null || "" == mixSuffix) {
                    return
                }
                val mixFile = PATH + File.separator + "mix" + mixSuffix
                commandLine = FFmpegUtil.mixAudio(srcFile, appendFile, mixFile)
            }
            R.id.btn_play_audio//解码播放（AudioTrack）
            -> {
                Thread(Runnable { AudioPlayer().play(srcFile) }).start()
                return
            }
            R.id.btn_play_opensl//解码播放（OpenSL ES）
            -> {
                Thread(Runnable { AudioPlayer().playAudio(srcFile) }).start()
                return
            }
            R.id.btn_audio_encode//音频编码
            -> {
                //可编码成WAV、AAC。如果需要编码成MP3，ffmpeg需要重新编译，把MP3库enable
                val pcmFile = PATH + File.separator + "concat.pcm"
                val wavFile = PATH + File.separator + "new.wav"
                //pcm数据的采样率，一般采样率为8000、16000、44100
                val sampleRate = 8000
                //pcm数据的声道，单声道为1，立体声道为2
                val channel = 1
                commandLine = FFmpegUtil.encodeAudio(pcmFile, wavFile, sampleRate, channel)
            }
            R.id.btn_pcm_concat//PCM裸流音频文件合并
            -> {
                val srcPCM = PATH + File.separator + "audio.pcm"//第一个pcm文件
                val appendPCM = PATH + File.separator + "audio.pcm"//第二个pcm文件
                val concatPCM = PATH + File.separator + "concat.pcm"//合并后的文件
                if (!FileUtil.checkFileExist(srcPCM) || !FileUtil.checkFileExist(appendPCM)) {
                    return
                }

                mHandler.obtainMessage(MSG_BEGIN).sendToTarget()
                FileUtil.concatFile(srcPCM, appendPCM, concatPCM)
                mHandler.obtainMessage(MSG_FINISH).sendToTarget()
                return
            }
            else -> {
            }
        }
        if (ffmpegHandler != null && commandLine != null) {
            ffmpegHandler!!.executeFFmpegCmd(commandLine)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

    companion object {

        private val PATH = Environment.getExternalStorageDirectory().path

        private const val useFFmpeg = true
    }

}
