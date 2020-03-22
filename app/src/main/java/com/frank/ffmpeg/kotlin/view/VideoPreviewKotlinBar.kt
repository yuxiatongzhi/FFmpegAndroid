package com.frank.ffmpeg.kotlin.view

import android.content.Context
import android.graphics.SurfaceTexture
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.frank.ffmpeg.R
import com.frank.ffmpeg.hardware.HardwareDecode
import com.frank.ffmpeg.kotlin.util.ScreenKotlinUtil
import com.frank.ffmpeg.util.TimeUtil

/**
 * 视频拖动实时预览的控件
 * Created by frank on 2020/3/21.
 */

class VideoPreviewKotlinBar : RelativeLayout, HardwareDecode.OnDataCallback {

    companion object {
        private val TAG = VideoPreviewKotlinBar::class.java.simpleName
    }

    private var texturePreView : TextureView? = null

    private var previewBar : SeekBar? = null

    private var txtVideoProgress : TextView? = null

    private var txtVideoDuration : TextView? = null

    private var hardwareDecode : HardwareDecode? = null

    private var mPreviewBarCallback : PreviewBarCallback? = null

    private var duration : Int = 0

    private var screenWidth : Int = 0

    private var moveEndPos : Int = 0

    private var previewHalfWidth : Int = 0

    constructor(context : Context) : this(context, null)

    constructor(context: Context, attributes: AttributeSet?) : super(context, attributes) {
        initView(context)
    }

    fun initView(context: Context) {
        val view : View = LayoutInflater.from(context).inflate(R.layout.preview_video, this)
        previewBar = view.findViewById(R.id.preview_bar)
        texturePreView = view.findViewById(R.id.texture_preview)
        txtVideoProgress = view.findViewById(R.id.txt_video_progress)
        txtVideoDuration = view.findViewById(R.id.txt_video_duration)
        setListener()
        screenWidth = ScreenKotlinUtil.getScreenWidth(context)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (moveEndPos == 0) {
            val previewWidth:Int = texturePreView!!.width
            previewHalfWidth = previewWidth / 2
            val layoutParams:MarginLayoutParams = texturePreView!!.layoutParams as MarginLayoutParams
            val marginEnd = layoutParams.marginEnd
            moveEndPos = screenWidth - previewWidth - marginEnd
        }
    }

    fun setPreviewCallback(filePath : String, texturePreView : TextureView) {
        texturePreView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                doPreview(filePath, Surface(surface))
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {

            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                return false
            }
        }
    }

    fun doPreview(filePath : String, surface : Surface?) {
        if (surface == null || TextUtils.isEmpty(filePath)) {
            return
        }
        release()
        hardwareDecode = HardwareDecode(surface, filePath, this)
        hardwareDecode!!.decode()
    }

    private fun setListener() {
        previewBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) {
                    return
                }
                previewBar!!.progress = progress
                if (hardwareDecode != null && progress < duration) {
                    // us to ms
                    hardwareDecode!!.seekTo((progress * 1000).toLong())
                }
                val percent:Int = progress * screenWidth / duration
                if (percent > previewHalfWidth && percent < moveEndPos && texturePreView != null) {
                    texturePreView!!.translationX = (percent - previewHalfWidth).toFloat()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                texturePreView!!.visibility = VISIBLE
                hardwareDecode!!.setPreviewing(true)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                texturePreView!!.visibility = GONE
                mPreviewBarCallback!!.onStopTracking(seekBar!!.progress.toLong())
                hardwareDecode!!.setPreviewing(false)
            }
        })
    }

    override fun onData(duration: Long) {
        //us to ms
        val durationMs = (duration / 1000).toInt()
        Log.i(TAG, "duration=$duration")
        this.duration = durationMs
        post {
            previewBar!!.max = durationMs
            txtVideoDuration!!.text = TimeUtil.getVideoTime(durationMs.toLong())
            texturePreView!!.visibility = View.GONE
        }
    }

    private fun checkArgument(videoPath : String?) {
        if (texturePreView == null) {
            throw IllegalStateException("Must init TextureView first...")
        }
        if (videoPath == null || videoPath.isEmpty()) {
            throw IllegalStateException("videoPath is empty...")
        }
    }

    internal fun init(videoPath : String, previewBarCallback : PreviewBarCallback) {
        checkArgument(videoPath)
        this.mPreviewBarCallback = previewBarCallback
        doPreview(videoPath, Surface(texturePreView!!.surfaceTexture))
    }

    fun initDefault(videoPath: String, previewBarCallback: PreviewBarCallback) {
        checkArgument(videoPath)
        this.mPreviewBarCallback = previewBarCallback
        setPreviewCallback(videoPath, texturePreView!!)
    }

    fun updateProgress(progress: Int) {
        if (progress in 0..duration) {
            txtVideoProgress!!.text = TimeUtil.getVideoTime(progress.toLong())
            previewBar!!.progress = progress
        }
    }

    fun release() {
        hardwareDecode!!.release()
    }

    interface PreviewBarCallback {
        fun onStopTracking(progress : Long)
    }

}