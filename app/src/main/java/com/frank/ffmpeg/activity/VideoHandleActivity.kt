package com.frank.ffmpeg.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar

import com.frank.ffmpeg.FFmpegCmd
import com.frank.ffmpeg.R
import com.frank.ffmpeg.format.VideoLayout
import com.frank.ffmpeg.handler.FFmpegHandler
import com.frank.ffmpeg.util.FFmpegUtil
import com.frank.ffmpeg.util.FileUtil

import java.io.File

import com.frank.ffmpeg.handler.FFmpegHandler.MSG_BEGIN
import com.frank.ffmpeg.handler.FFmpegHandler.MSG_FINISH

class VideoHandleActivity : BaseActivity() {

    private var progressVideo: ProgressBar? = null
    private var layoutVideoHandle: LinearLayout? = null
    private var viewId: Int = 0
    private var ffmpegHandler: FFmpegHandler? = null

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_BEGIN -> {
                    progressVideo!!.visibility = View.VISIBLE
                    layoutVideoHandle!!.visibility = View.GONE
                }
                MSG_FINISH -> {
                    progressVideo!!.visibility = View.GONE
                    layoutVideoHandle!!.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_video_handle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideActionBar()
        intView()
        ffmpegHandler = FFmpegHandler(mHandler)
    }

    private fun intView() {
        progressVideo = getView(R.id.progress_video)
        layoutVideoHandle = getView(R.id.layout_video_handle)
        initViewsWithClick(
                R.id.btn_video_transform,
                R.id.btn_video_cut,
                R.id.btn_video_concat,
                R.id.btn_screen_shot,
                R.id.btn_water_mark,
                R.id.btn_generate_gif,
                R.id.btn_screen_record,
                R.id.btn_combine_video,
                R.id.btn_multi_video,
                R.id.btn_reverse_video,
                R.id.btn_denoise_video,
                R.id.btn_to_image,
                R.id.btn_pip,
                R.id.btn_moov
        )
    }

    public override fun onViewClick(view: View) {
        viewId = view.id
        if (viewId == R.id.btn_combine_video) {
            handlePhoto()
            return
        }
        selectFile()
    }

    override fun onSelectedFile(filePath: String) {
        doHandleVideo(filePath)
    }

    /**
     * 调用ffmpeg处理视频
     *
     * @param srcFile srcFile
     */
    private fun doHandleVideo(srcFile: String?) {
        var commandLine: Array<String>? = null
        if (!FileUtil.checkFileExist(srcFile)) {
            return
        }
        if (!FileUtil.isVideo(srcFile)) {
            showToast(getString(R.string.wrong_video_format))
            return
        }
        when (viewId) {
            R.id.btn_video_transform//视频转码:mp4转flv、wmv, 或者flv、wmv转Mp4
            -> {
                val transformVideo = PATH + File.separator + "transformVideo.mp4"
                commandLine = FFmpegUtil.transformVideo(srcFile, transformVideo)
            }
            R.id.btn_video_cut//视频剪切
            -> {
                val suffix = FileUtil.getFileSuffix(srcFile)
                if (suffix == null || "" == suffix) {
                    return
                }
                val cutVideo = PATH + File.separator + "cutVideo" + suffix
                val startTime = 0
                val duration = 20
                commandLine = FFmpegUtil.cutVideo(srcFile, startTime, duration, cutVideo)
            }
            R.id.btn_video_concat//视频合并
            -> {
            }
            R.id.btn_screen_shot//视频截图
            -> {
                val screenShot = PATH + File.separator + "screenShot.jpg"
                val time = 18
                commandLine = FFmpegUtil.screenShot(srcFile, time, screenShot)
            }
            R.id.btn_water_mark//视频添加水印
            -> {
                //1、图片
                val photo = PATH + File.separator + "launcher.png"
                val photoMark = PATH + File.separator + "photoMark.mp4"
                val mResolution = "720x1280"
                val bitRate = 1024
                commandLine = FFmpegUtil.addWaterMark(srcFile, photo, mResolution, bitRate, photoMark)
            }
            R.id.btn_generate_gif//视频转成gif
            -> {
                val Video2Gif = PATH + File.separator + "Video2Gif.gif"
                val gifStart = 30
                val gifDuration = 5
                val resolution = "720x1280"//240x320、480x640、1080x1920
                val frameRate = 10
                commandLine = FFmpegUtil.generateGif(srcFile, gifStart, gifDuration,
                        resolution, frameRate, Video2Gif)
            }
            R.id.btn_screen_record//屏幕录制
            -> {
            }
            R.id.btn_multi_video//视频画面拼接:分辨率、时长、封装格式不一致时，先把视频源转为一致
            -> {
                val input1 = PATH + File.separator + "input1.mp4"
                val input2 = PATH + File.separator + "input2.mp4"
                val outputFile = PATH + File.separator + "multi.mp4"
                if (!FileUtil.checkFileExist(input1) || !FileUtil.checkFileExist(input2)) {
                    return
                }
                commandLine = FFmpegUtil.multiVideo(input1, input2, outputFile, VideoLayout.LAYOUT_HORIZONTAL)
            }
            R.id.btn_reverse_video//视频反序倒播
            -> {
                val output = PATH + File.separator + "reverse.mp4"
                commandLine = FFmpegUtil.reverseVideo(srcFile, output)
            }
            R.id.btn_denoise_video//视频降噪
            -> {
                val denoise = PATH + File.separator + "denoise.mp4"
                commandLine = FFmpegUtil.denoiseVideo(srcFile, denoise)
            }
            R.id.btn_to_image//视频转图片
            -> {
                val imagePath = PATH + File.separator + "Video2Image/"//图片保存路径
                val imageFile = File(imagePath)
                if (!imageFile.exists()) {
                    val result = imageFile.mkdir()
                    if (!result) {
                        return
                    }
                }
                val mStartTime = 10//开始时间
                val mDuration = 20//持续时间（注意开始时间+持续时间之和不能大于视频总时长）
                val mFrameRate = 10//帧率（从视频中每秒抽多少帧）
                commandLine = FFmpegUtil.videoToImage(srcFile, mStartTime, mDuration, mFrameRate, imagePath)
            }
            R.id.btn_pip//两个视频合成画中画
            -> {
                val inputFile1 = PATH + File.separator + "beyond.mp4"
                val inputFile2 = PATH + File.separator + "small_girl.mp4"
                if (!FileUtil.checkFileExist(inputFile1) && !FileUtil.checkFileExist(inputFile2)) {
                    return
                }
                //x、y坐标点需要根据全屏视频与小视频大小，进行计算
                //比如：全屏视频为320x240，小视频为120x90，那么x=200 y=150
                val x = 200
                val y = 150
                val picInPic = PATH + File.separator + "PicInPic.mp4"
                commandLine = FFmpegUtil.picInPicVideo(inputFile1, inputFile2, x, y, picInPic)
            }
            R.id.btn_moov//moov前移操作，针对mp4视频moov在mdat后面的情况
            -> {
                if (!srcFile!!.endsWith(FileUtil.TYPE_MP4)) {
                    showToast(getString(R.string.tip_not_mp4_video))
                    return
                }
                val filePath = FileUtil.getFilePath(srcFile)
                var fileName = FileUtil.getFileName(srcFile)
                Log.e(TAG, "moov filePath=$filePath--fileName=$fileName")
                fileName = "moov_" + fileName!!
                val moovPath = filePath + File.separator + fileName
                if (useFFmpegCmd) {
                    commandLine = FFmpegUtil.moveMoovAhead(srcFile, moovPath)
                } else {
                    val start = System.currentTimeMillis()
                    val ffmpegCmd = FFmpegCmd()
                    val result = ffmpegCmd.moveMoovAhead(srcFile, moovPath)
                    Log.e(TAG, "result=" + (result == 0))
                    Log.e(TAG, "move moov use time=" + (System.currentTimeMillis() - start))
                }
            }
            else -> {
            }
        }
        if (ffmpegHandler != null && commandLine != null) {
            ffmpegHandler!!.executeFFmpegCmd(commandLine)
        }
    }

    /**
     * 图片合成视频
     */
    private fun handlePhoto() {
        // 图片所在路径，图片命名格式img+number.jpg
        // 这里指定目录为根目录下img文件夹
        val picturePath = "$PATH/img/"
        if (!FileUtil.checkFileExist(picturePath)) {
            return
        }
        val combineVideo = PATH + File.separator + "combineVideo.mp4"
        val frameRate = 2// 合成视频帧率建议:1-10  普通视频帧率一般为25
        val commandLine = FFmpegUtil.pictureToVideo(picturePath, frameRate, combineVideo)
        if (ffmpegHandler != null) {
            ffmpegHandler!!.executeFFmpegCmd(commandLine)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

    companion object {

        private val TAG = VideoHandleActivity::class.java.simpleName
        private val PATH = Environment.getExternalStorageDirectory().path
        private const val useFFmpegCmd = true
    }
}
