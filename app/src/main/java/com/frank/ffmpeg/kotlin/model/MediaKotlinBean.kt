package com.frank.ffmpeg.kotlin.model

/**
 * the model of media data
 * Created by frank on 2020/3/4.
 */
class MediaKotlinBean {

    var videoBean: VideoKotlinBean? = null

    var audioBean: AudioKotlinBean? = null

    // "duration": "313.330000"
    var duration: Long = 0

    // "size": "22160429"
    var size: Long = 0

    // "bit_rate": "565804"
    var bitRate: Int = 0

    // "format_name": "mov,mp4,m4a,3gp,3g2,mj2"
    var formatName: String? = null

    // "nb_streams": 2
    var streamNum: Int = 0
}
