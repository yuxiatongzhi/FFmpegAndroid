package com.frank.ffmpeg.kotlin.activity

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.ToggleButton

import com.frank.ffmpeg.R
import com.frank.ffmpeg.VideoPlayer
import com.frank.ffmpeg.adapter.HorizontalAdapter
import com.frank.ffmpeg.listener.OnItemClickListener
import com.frank.ffmpeg.util.FileUtil

import java.util.ArrayList
import java.util.Arrays

/**
 * 使用ffmpeg进行滤镜
 * Created by frank on 2018/6/5.
 */

class FilterActivity : BaseActivity(), SurfaceHolder.Callback {

    //本地视频路径
    private var videoPath = ""

    private var videoPlayer: VideoPlayer? = null
    private var surfaceView: SurfaceView? = null
    private var surfaceHolder: SurfaceHolder? = null
    //surface是否已经创建
    private var surfaceCreated: Boolean = false
    //是否正在播放
    private var isPlaying: Boolean = false
    //滤镜数组
    private val filters = arrayOf("lutyuv='u=128:v=128'", "hue='h=60:s=-3'", "lutrgb='r=0:g=0'", "edgedetect=low=0.1:high=0.4", "drawgrid=w=iw/3:h=ih/3:t=2:c=white@0.5", "colorbalance=bs=0.3", "drawbox=x=100:y=100:w=100:h=100:color=red@0.5'", "vflip", "unsharp")
    private val txtArray = arrayOf("素描", "鲜明", //hue
            "暖蓝", "边缘", "九宫格", "均衡", "矩形", "翻转", //vflip上下翻转,hflip是左右翻转
            "锐化")
    private var horizontalAdapter: HorizontalAdapter? = null
    private var recyclerView: RecyclerView? = null
    //是否播放音频
    private var playAudio = true
    private var btnSound: ToggleButton? = null
    private var btnSelect: Button? = null
    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == MSG_HIDE) {//无操作5s后隐藏滤镜操作栏
                recyclerView!!.visibility = View.GONE
                btnSound!!.visibility = View.GONE
                btnSelect!!.visibility = View.GONE
            }
        }
    }
    private var hideRunnable: HideRunnable? = null

    private inner class HideRunnable : Runnable {
        override fun run() {
            mHandler.obtainMessage(MSG_HIDE).sendToTarget()
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_filter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        hideActionBar()
        initView()
        registerLister()

        hideRunnable = HideRunnable()
        mHandler.postDelayed(hideRunnable, DELAY_TIME.toLong())
    }

    private fun initView() {
        surfaceView = getView(R.id.surface_filter)
        surfaceHolder = surfaceView!!.holder
        surfaceHolder!!.addCallback(this)
        videoPlayer = VideoPlayer()
        btnSound = getView(R.id.btn_sound)

        recyclerView = getView(R.id.recycler_view)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView!!.layoutManager = linearLayoutManager
        val itemList = ArrayList(Arrays.asList(*txtArray))
        horizontalAdapter = HorizontalAdapter(itemList)
        recyclerView!!.adapter = horizontalAdapter

        btnSelect = getView(R.id.btn_select_file)
        initViewsWithClick(R.id.btn_select_file)
    }

    //注册监听器
    private fun registerLister() {
        horizontalAdapter!!.setOnItemClickListener(OnItemClickListener { position ->
            if (!surfaceCreated)
                return@OnItemClickListener
            if (!FileUtil.checkFileExist(videoPath)) {
                showSelectFile()
                return@OnItemClickListener
            }
            doFilterPlay(position)
        })

        surfaceView!!.setOnClickListener {
            btnSelect!!.visibility = View.VISIBLE
            btnSound!!.visibility = View.VISIBLE
            recyclerView!!.visibility = View.VISIBLE//按下SurfaceView，弹出滤镜操作栏
            mHandler.postDelayed(hideRunnable, DELAY_TIME.toLong())//5s后发消息通知隐藏滤镜操作栏
        }

        btnSound!!.setOnCheckedChangeListener { buttonView, isChecked -> setPlayAudio() }
    }

    private fun doFilterPlay(position: Int) {
        Thread(Runnable {
            //切换播放
            if (isPlaying) {
                videoPlayer!!.again()
            }
            isPlaying = true
            videoPlayer!!.filter(videoPath, surfaceHolder!!.surface, filters[position])
        }).start()
    }

    //设置是否静音
    private fun setPlayAudio() {
        playAudio = !playAudio
        videoPlayer!!.playAudio(playAudio)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        surfaceCreated = true
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        surfaceCreated = false
    }

    override fun onDestroy() {
        super.onDestroy()
        isPlaying = false
        //暂时注释
        //videoPlayer.release()
        videoPlayer = null
        horizontalAdapter = null
    }

    override fun onViewClick(view: View) {
        if (view.id == R.id.btn_select_file) {
            selectFile()
        }
    }

    override fun onSelectedFile(filePath: String) {
        videoPath = filePath
        //选择滤镜模式
        doFilterPlay(5)
        //默认关闭声音
        btnSound!!.isChecked = true
    }
    companion object {

        private const val MSG_HIDE = 222
        private const val DELAY_TIME = 5000
    }

}
