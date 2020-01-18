package com.frank.ffmpeg.activity

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.frank.ffmpeg.R
import com.frank.ffmpeg.handler.FFmpegHandler
import com.frank.ffmpeg.util.FFmpegUtil
import com.frank.ffmpeg.util.FileUtil

import java.io.File

import com.frank.ffmpeg.handler.FFmpegHandler.MSG_BEGIN
import com.frank.ffmpeg.handler.FFmpegHandler.MSG_CONTINUE
import com.frank.ffmpeg.handler.FFmpegHandler.MSG_FINISH

/**
 * 使用ffmpeg进行音视频合成与分离
 * Created by frank on 2018/1/23.
 */
class MediaHandleActivity : BaseActivity() {
    private var videoFile: String? = null
    private val temp = PATH + File.separator + "temp.mp4"

    private var progressMedia: ProgressBar? = null
    private var viewId: Int = 0
    private var layoutMediaHandle: LinearLayout? = null
    private var ffmpegHandler: FFmpegHandler? = null

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_CONTINUE -> {
                    val audioFile = PATH + File.separator + "tiger.mp3"//tiger.mp3
                    val muxFile = PATH + File.separator + "media-mux.mp4"

                    try {
                        //使用MediaPlayer获取视频时长
                        val mediaPlayer = MediaPlayer()
                        mediaPlayer.setDataSource(videoFile)
                        mediaPlayer.prepare()
                        //单位为ms
                        val videoDuration = mediaPlayer.duration / 1000
                        Log.i(TAG, "videoDuration=$videoDuration")
                        mediaPlayer.release()
                        //使用MediaMetadataRetriever获取音频时长
                        val mediaRetriever = MediaMetadataRetriever()
                        mediaRetriever.setDataSource(audioFile)
                        //单位为ms
                        val duration = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        val audioDuration = (java.lang.Long.parseLong(duration) / 1000).toInt()
                        Log.i(TAG, "audioDuration=$audioDuration")
                        mediaRetriever.release()
                        //如果视频时长比音频长，采用音频时长，否则用视频时长
                        val mDuration = Math.min(audioDuration, videoDuration)
                        //使用纯视频与音频进行合成
                        val commandLine = FFmpegUtil.mediaMux(temp, audioFile, mDuration, muxFile)
                        if (ffmpegHandler != null) {
                            ffmpegHandler!!.isContinue(false)
                            ffmpegHandler!!.executeFFmpegCmd(commandLine)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                MSG_BEGIN -> {
                    progressMedia!!.visibility = View.VISIBLE
                    layoutMediaHandle!!.visibility = View.GONE
                }
                MSG_FINISH -> {
                    progressMedia!!.visibility = View.GONE
                    layoutMediaHandle!!.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_media_handle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideActionBar()
        initView()
        ffmpegHandler = FFmpegHandler(mHandler)
    }

    private fun initView() {
        progressMedia = getView(R.id.progress_media)
        layoutMediaHandle = getView(R.id.layout_media_handle)
        initViewsWithClick(
                R.id.btn_mux,
                R.id.btn_extract_audio,
                R.id.btn_extract_video
        )
    }

    public override fun onViewClick(view: View) {
        viewId = view.id
        selectFile()
    }

    override fun onSelectedFile(filePath: String) {
        doHandleMedia(filePath)
    }

    /**
     * 调用ffmpeg处理音视频
     * @param srcFile srcFile
     */
    private fun doHandleMedia(srcFile: String) {
        var commandLine: Array<String>? = null
        if (!FileUtil.checkFileExist(srcFile)) {
            return
        }
        if (!FileUtil.isVideo(srcFile)) {
            showToast(getString(R.string.wrong_video_format))
            return
        }

        when (viewId) {
            R.id.btn_mux//音视频合成
            -> try {
                //视频文件有音频,先把纯视频文件抽取出来
                videoFile = srcFile
                commandLine = FFmpegUtil.extractVideo(srcFile, temp)
                if (ffmpegHandler != null) {
                    ffmpegHandler!!.isContinue(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            R.id.btn_extract_audio//提取音频
            -> {
                val extractAudio = PATH + File.separator + "extractAudio.aac"
                commandLine = FFmpegUtil.extractAudio(srcFile, extractAudio)
            }
            R.id.btn_extract_video//提取视频
            -> {
                val extractVideo = PATH + File.separator + "extractVideo.mp4"
                commandLine = FFmpegUtil.extractVideo(srcFile, extractVideo)
            }
            else -> {
            }
        }
        if (ffmpegHandler != null) {
            ffmpegHandler!!.executeFFmpegCmd(commandLine)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

    companion object {

        private val TAG = MediaHandleActivity::class.java.simpleName
        private val PATH = Environment.getExternalStorageDirectory().path
    }
}
