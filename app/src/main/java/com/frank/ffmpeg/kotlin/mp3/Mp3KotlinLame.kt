package com.frank.ffmpeg.kotlin.mp3

import com.frank.ffmpeg.AudioPlayer

class Mp3KotlinLame {

    constructor() {
        AudioPlayer.lameInitDefault()
    }

    constructor(builder: Mp3LameKotlinBuilder) {
        initialize(builder)
    }

    private fun initialize(builder: Mp3LameKotlinBuilder) {
        AudioPlayer.lameInit(builder.inSampleRate, builder.outChannel, builder.outSampleRate,
                builder.outBitrate, builder.scaleInput, getIntForMode(builder.mode), getIntForVbrMode(builder.vbrMode), builder.quality, builder.vbrQuality, builder.abrMeanBitrate,
                builder.lowPassFreq, builder.highPassFreq, builder.id3tagTitle, builder.id3tagArtist,
                builder.id3tagAlbum, builder.id3tagYear, builder.id3tagComment)
    }

    fun encode(buffer_l: ShortArray, buffer_r: ShortArray,
               samples: Int, mp3buf: ByteArray): Int {

        return AudioPlayer.lameEncode(buffer_l, buffer_r, samples, mp3buf)
    }

    internal fun encodeBufferInterLeaved(pcm: ShortArray, samples: Int,
                                         mp3buf: ByteArray): Int {
        return AudioPlayer.encodeBufferInterleaved(pcm, samples, mp3buf)
    }

    fun flush(mp3buf: ByteArray): Int {
        return AudioPlayer.lameFlush(mp3buf)
    }

    fun close() {
        AudioPlayer.lameClose()
    }

    private fun getIntForMode(mode: Mp3LameKotlinBuilder.Mode): Int {
        return when (mode) {
            Mp3LameKotlinBuilder.Mode.STEREO -> 0
            Mp3LameKotlinBuilder.Mode.JSTEREO -> 1
            Mp3LameKotlinBuilder.Mode.MONO -> 3
            Mp3LameKotlinBuilder.Mode.DEFAULT -> 4
        }
    }

    private fun getIntForVbrMode(mode: Mp3LameKotlinBuilder.VbrMode): Int {
        return when (mode) {
            Mp3LameKotlinBuilder.VbrMode.VBR_OFF -> 0
            Mp3LameKotlinBuilder.VbrMode.VBR_RH -> 2
            Mp3LameKotlinBuilder.VbrMode.VBR_ABR -> 3
            Mp3LameKotlinBuilder.VbrMode.VBR_MTRH -> 4
            Mp3LameKotlinBuilder.VbrMode.VBR_DEFAUT -> 6
        }
    }

}
