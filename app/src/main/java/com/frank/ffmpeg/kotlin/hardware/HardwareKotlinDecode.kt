package com.frank.ffmpeg.kotlin.hardware

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * Extract by MediaExtractor, decode by MediaCodec, and render to Surface
 * Created by frank on 2020/04/06.
 */

class HardwareKotlinDecode constructor(surface : Surface, filePath : String, onDataCallback : OnDataCallback){

    private var mSurface : Surface ?= null
    private var mFilePath : String = ""
    private var mVideoDecodeThread : VideoDecodeThread ?= null
    private var mCallback : OnDataCallback ?= null

    init {
        this.mSurface = surface
        this.mFilePath = filePath
        this.mCallback = onDataCallback
    }

    interface OnDataCallback {
        fun onData(duration : Long)
    }

    public fun decode() {
        mVideoDecodeThread = VideoDecodeThread(mFilePath)
        mVideoDecodeThread!!.start()
    }

    public fun seekTo(seekPosition : Long) {
        if (!mVideoDecodeThread!!.isInterrupted) {
            mVideoDecodeThread!!.seekTo(seekPosition)
        }
    }

    public fun setPreviewing(previewing : Boolean) {
        mVideoDecodeThread!!.isPreviewing = previewing;
    }

    public fun release() {
        if (!mVideoDecodeThread!!.isInterrupted) {
            mVideoDecodeThread!!.interrupt()
            mVideoDecodeThread!!.release()
            mVideoDecodeThread = null
        }
    }

    private inner class VideoDecodeThread(filePath: String) : Thread() {

        private var mediaExtractor : MediaExtractor ?= null
        private var mediaCodec : MediaCodec ?= null
        var isPreviewing : Boolean = false
        private var mFilePath : String = filePath

        fun seekTo(seekPosition: Long) {
            try {
                mediaExtractor!!.seekTo(seekPosition, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            } catch (e: IllegalStateException) {
                Log.e(TAG, "seekTo error=$e")
            }
        }

        fun release() {
            try {
                mediaCodec!!.stop()
                mediaCodec!!.release()
                mediaExtractor!!.release()
            }catch (e : Exception) {
                Log.e(TAG, "release error=$e")
            }
        }

        fun setPreviewRatio(mediaFormat : MediaFormat) {
            val videoWidth = mediaFormat.getInteger(MediaFormat.KEY_WIDTH)
            val videoHeight = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT)
            val previewRatio = when {
                videoWidth >= RATIO_1080 -> 10
                videoWidth >= RATIO_480 -> 6
                videoWidth >= RATIO_240 -> 4
                else -> 1
            }
            val previewWidth = videoWidth / previewRatio
            val previewHeight = videoHeight / previewRatio
            mediaFormat.setInteger(MediaFormat.KEY_WIDTH, previewWidth)
            mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, previewHeight)
        }

        override fun run() {
            super.run()
            mediaExtractor = MediaExtractor()
            var mediaFormat : MediaFormat ?= null
            var mimeType = ""

            try {
                mediaExtractor!!.setDataSource(mFilePath)
                val trackCount = mediaExtractor!!.trackCount
                for (i in 0..trackCount) {
                    mediaFormat = mediaExtractor!!.getTrackFormat(i)
                    mimeType = mediaFormat.getString(MediaFormat.KEY_MIME)
                    if (mimeType != null && mimeType.startsWith("video/")) {
                        mediaExtractor!!.selectTrack(i)
                        break
                    }
                }

                val width : Int = mediaFormat!!.getInteger(MediaFormat.KEY_WIDTH)
                val height : Int = mediaFormat.getInteger(MediaFormat.KEY_HEIGHT)
                val duration : Long = mediaFormat.getLong(MediaFormat.KEY_DURATION)
                mCallback!!.onData(duration)
                Log.i(TAG, "width=$width--height=$height--duration=$duration")
                setPreviewRatio(mediaFormat)
                mediaCodec = MediaCodec.createDecoderByType(mimeType)
                mediaCodec!!.configure(mediaFormat, mSurface, null, 0)
                mediaCodec!!.start()
                val inputBuffers = mediaCodec!!.getInputBuffers()
                val bufferInfo : MediaCodec.BufferInfo = MediaCodec.BufferInfo()

                while (!isInterrupted) {
                    if (!isPreviewing) {
                        SystemClock.sleep(SLEEP_TIME)
                        continue
                    }
                    val inputIndex = mediaCodec!!.dequeueInputBuffer(DEQUEUE_TIME)
                    if (inputIndex >= 0) {
                        val inputBuffer : ByteBuffer = inputBuffers[inputIndex]
                        val sampleSize = mediaExtractor!!.readSampleData(inputBuffer, 0)
                        if (sampleSize < 0) {
                            mediaCodec!!.queueInputBuffer(inputIndex, 0, 0, 0,
                                    MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        } else {
                            mediaCodec!!.queueInputBuffer(inputIndex, 0, sampleSize, mediaExtractor!!.sampleTime, 0)
                            mediaExtractor!!.advance()
                        }
                    }
                    val outputIndex = mediaCodec!!.dequeueOutputBuffer(bufferInfo, DEQUEUE_TIME)
                    when (outputIndex) {
                        MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> Log.i(TAG, "output format changed...")
                        MediaCodec.INFO_TRY_AGAIN_LATER -> Log.i(TAG, "try again later...")
                        MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED -> Log.i(TAG, "output buffer changed...")
                        else ->
                            //render to surface
                            mediaCodec!!.releaseOutputBuffer(outputIndex, true)
                    }
                }
            } catch (e : Exception) {
                Log.e(TAG, "decode error=$e")
            }
        }

    }

    companion object {
        private val TAG = HardwareKotlinDecode::class.java.simpleName

        private const val DEQUEUE_TIME : Long = 10 * 1000
        private const val SLEEP_TIME : Long = 10

        private const val RATIO_1080 = 1080
        private const val RATIO_480 = 480
        private const val RATIO_240 = 240
    }
}