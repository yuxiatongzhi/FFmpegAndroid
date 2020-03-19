package com.frank.ffmpeg.kotlin.util

import android.text.TextUtils
import android.util.Log

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * 文件工具类
 * Created by frank on 2018/5/9.
 */

object FileKotlinUtil {

    private const val TYPE_MP3 = "mp3"
    private const val TYPE_AAC = "aac"
    private const val TYPE_AMR = "amr"
    private const val TYPE_FLAC = "flac"
    private const val TYPE_M4A = "m4a"
    private const val TYPE_WMA = "wma"
    private const val TYPE_WAV = "wav"
    private const val TYPE_OGG = "ogg"
    private const val TYPE_AC3 = "ac3"

    const val TYPE_MP4 = "mp4"
    private const val TYPE_MKV = "mkv"
    private const val TYPE_WEBM = "webm"
    private const val TYPE_AVI = "avi"
    private const val TYPE_WMV = "wmv"
    private const val TYPE_FLV = "flv"
    private const val TYPE_TS = "ts"
    private const val TYPE_M3U8 = "m3u8"
    private const val TYPE_3GP = "3gp"
    private const val TYPE_MOV = "mov"
    private const val TYPE_MPG = "mpg"

    fun concatFile(srcFilePath: String, appendFilePath: String, concatFilePath: String): Boolean {
        if (TextUtils.isEmpty(srcFilePath)
                || TextUtils.isEmpty(appendFilePath)
                || TextUtils.isEmpty(concatFilePath)) {
            return false
        }
        val srcFile = File(srcFilePath)
        if (!srcFile.exists()) {
            return false
        }
        val appendFile = File(appendFilePath)
        if (!appendFile.exists()) {
            return false
        }
        var outputStream: FileOutputStream? = null
        var inputStream1: FileInputStream? = null
        var inputStream2: FileInputStream? = null
        try {
            inputStream1 = FileInputStream(srcFile)
            inputStream2 = FileInputStream(appendFile)
            outputStream = FileOutputStream(File(concatFilePath))
            val data = ByteArray(1024)
            var len: Int
            do {
                len = inputStream1.read(data)
                if (len > 0) {
                    outputStream.write(data, 0, len)
                }
            } while (len > 0)
            outputStream.flush()
            do {
                len = inputStream2.read(data)
                if (len > 0) {
                    outputStream.write(data, 0, len)
                }
            } while (len > 0)
            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream1?.close()
                inputStream2?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return true
    }

    /**
     * 判断文件是否存在
     * @param path 文件路径
     * @return 文件是否存在
     */
    fun checkFileExist(path: String): Boolean {
        if (TextUtils.isEmpty(path)) {
            return false
        }
        val file = File(path)
        if (!file.exists()) {
            Log.e("FileUtil", "$path is not exist!")
            return false
        }
        return true
    }

    fun isAudio(path: String): Boolean {
        if (TextUtils.isEmpty(path)) {
            return false
        }
        with(path.toLowerCase(Locale.getDefault())) {
            return (this.endsWith(TYPE_MP3)
                    || this.endsWith(TYPE_AAC)
                    || this.endsWith(TYPE_AMR)
                    || this.endsWith(TYPE_FLAC)
                    || this.endsWith(TYPE_M4A)
                    || this.endsWith(TYPE_WMA)
                    || this.endsWith(TYPE_WAV)
                    || this.endsWith(TYPE_OGG)
                    || this.endsWith(TYPE_AC3))
        }
    }

    fun isVideo(path: String): Boolean {
        if (TextUtils.isEmpty(path)) {
            return false
        }
        with(path.toLowerCase(Locale.getDefault())) {
            return (this.endsWith(TYPE_MP4)
                    || this.endsWith(TYPE_MKV)
                    || this.endsWith(TYPE_WEBM)
                    || this.endsWith(TYPE_WMV)
                    || this.endsWith(TYPE_AVI)
                    || this.endsWith(TYPE_FLV)
                    || this.endsWith(TYPE_3GP)
                    || this.endsWith(TYPE_TS)
                    || this.endsWith(TYPE_M3U8)
                    || this.endsWith(TYPE_MOV)
                    || this.endsWith(TYPE_MPG))
        }
    }

    fun getFileSuffix(fileName: String): String? {
        return if (TextUtils.isEmpty(fileName) || !fileName.contains(".")) {
            null
        } else fileName.substring(fileName.lastIndexOf("."))
    }

    fun getFilePath(filePath: String): String? {
        return if (TextUtils.isEmpty(filePath) || !filePath.contains("/")) {
            null
        } else filePath.substring(0, filePath.lastIndexOf("/"))
    }

    fun getFileName(filePath: String): String? {
        return if (TextUtils.isEmpty(filePath) || !filePath.contains("/")) {
            null
        } else filePath.substring(filePath.lastIndexOf("/") + 1)
    }

    fun createListFile(listPath: String, fileArray: Array<String>?): String? {
        if (TextUtils.isEmpty(listPath) || fileArray == null || fileArray.size == 0) {
            return null
        }
        var outputStream: FileOutputStream? = null
        try {
            val listFile = File(listPath)
            if (!listFile.parentFile.exists()) {
                if (!listFile.mkdirs()) {
                    return null
                }
            }
            if (!listFile.exists()) {
                if (!listFile.createNewFile()) {
                    return null
                }
            }
            outputStream = FileOutputStream(listFile)
            val fileBuilder = StringBuilder()
            for (file in fileArray) {
                fileBuilder
                        .append("file")
                        .append(" ")
                        .append("'")
                        .append(file)
                        .append("'")
                        .append("\n")
            }
            val fileData = fileBuilder.toString().toByteArray()
            outputStream.write(fileData, 0, fileData.size)
            outputStream.flush()
            return listFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return null
    }

    fun ensureDir(fileDir: String): Boolean {
        if (TextUtils.isEmpty(fileDir)) {
            return false
        }
        val listFile = File(fileDir)
        return if (!listFile.exists()) {
            listFile.mkdirs()
        } else true
    }

}
