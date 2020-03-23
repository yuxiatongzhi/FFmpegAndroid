package com.frank.ffmpeg.kotlin.mp3

open class Mp3LameKotlinBuilder {

    var inSampleRate: Int = 0
    var outSampleRate: Int = 0
    var outBitrate: Int = 0
    var outChannel: Int = 0
    var quality: Int = 0
    var vbrQuality: Int = 0
    var abrMeanBitrate: Int = 0
    var lowPassFreq: Int = 0
    var highPassFreq: Int = 0
    var scaleInput: Float = 0.toFloat()
    var mode: Mode
    var vbrMode: VbrMode

    var id3tagTitle: String? = null
    var id3tagArtist: String? = null
    var id3tagAlbum: String? = null
    var id3tagComment: String? = null
     var id3tagYear: String? = null

    enum class Mode {
        STEREO, JSTEREO, MONO, DEFAULT
    }

    enum class VbrMode {
        VBR_OFF, VBR_RH, VBR_MTRH, VBR_ABR, VBR_DEFAUT
    }

    init {

        this.id3tagTitle = null
        this.id3tagAlbum = null
        this.id3tagArtist = null
        this.id3tagComment = null
        this.id3tagYear = null

        this.inSampleRate = 44100
        this.outSampleRate = 0
        this.outChannel = 2
        this.outBitrate = 128
        this.scaleInput = 1f

        this.quality = 5
        this.mode = Mode.DEFAULT
        this.vbrMode = VbrMode.VBR_OFF
        this.vbrQuality = 5
        this.abrMeanBitrate = 128

        this.lowPassFreq = 0
        this.highPassFreq = 0
    }

    fun setQuality(quality: Int): Mp3LameKotlinBuilder {
        this.quality = quality
        return this
    }

    fun setInSampleRate(inSampleRate: Int): Mp3LameKotlinBuilder {
        this.inSampleRate = inSampleRate
        return this
    }

    fun setOutSampleRate(outSampleRate: Int): Mp3LameKotlinBuilder {
        this.outSampleRate = outSampleRate
        return this
    }

    fun setOutBitrate(bitrate: Int): Mp3LameKotlinBuilder {
        this.outBitrate = bitrate
        return this
    }

    fun setOutChannels(channels: Int): Mp3LameKotlinBuilder {
        this.outChannel = channels
        return this
    }

    fun setId3tagTitle(title: String): Mp3LameKotlinBuilder {
        this.id3tagTitle = title
        return this
    }

    fun setId3tagArtist(artist: String): Mp3LameKotlinBuilder {
        this.id3tagArtist = artist
        return this
    }

    fun setId3tagAlbum(album: String): Mp3LameKotlinBuilder {
        this.id3tagAlbum = album
        return this
    }

    fun setId3tagComment(comment: String): Mp3LameKotlinBuilder {
        this.id3tagComment = comment
        return this
    }

    fun setId3tagYear(year: String): Mp3LameKotlinBuilder {
        this.id3tagYear = year
        return this
    }

    fun setScaleInput(scaleAmount: Float): Mp3LameKotlinBuilder {
        this.scaleInput = scaleAmount
        return this
    }

    fun setMode(mode: Mode): Mp3LameKotlinBuilder {
        this.mode = mode
        return this
    }

    fun setVbrMode(mode: VbrMode): Mp3LameKotlinBuilder {
        this.vbrMode = mode
        return this
    }

    fun setVbrQuality(quality: Int): Mp3LameKotlinBuilder {
        this.vbrQuality = quality
        return this
    }

    fun setAbrMeanBitrate(bitrate: Int): Mp3LameKotlinBuilder {
        this.abrMeanBitrate = bitrate
        return this
    }

    fun setLowpassFreqency(freq: Int): Mp3LameKotlinBuilder {
        this.lowPassFreq = freq
        return this
    }

    fun setHighpassFreqency(freq: Int): Mp3LameKotlinBuilder {
        this.highPassFreq = freq
        return this
    }

    fun build(): Mp3KotlinLame {
        return Mp3KotlinLame(this)
    }

}
