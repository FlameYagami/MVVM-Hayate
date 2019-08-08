package com.mvvm.component.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    fun getCurrentTime(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
    }

    fun getLogTime(): String {
        return SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(System.currentTimeMillis())
    }

    fun getMsTime(): String {
        return SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(System.currentTimeMillis())
    }

    fun getHmTime(): String {
        return SimpleDateFormat("HHmm", Locale.getDefault()).format(System.currentTimeMillis())
    }
}
