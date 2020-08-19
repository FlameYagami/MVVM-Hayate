package com.mvvm.component.utils

import android.content.Context
import android.net.ConnectivityManager
import com.mvvm.component.BaseApplication.Companion.context


/**
 * Created by Administrator on 2015/5/11.
 * 判断网络的工具类
 */
object NetUtils {

    enum class NetworkType {
        NETWORK_NONE,
        NETWORK_WIFI,
        NETWORK_MOBILE,
        NETWORK_UNKNOWN
    }

    fun isNetworkConnect(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun getNetworkType(): NetworkType {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfo = connectivityManager.activeNetworkInfo
        return when {
            activeNetInfo == null -> NetworkType.NETWORK_NONE
            activeNetInfo.type == ConnectivityManager.TYPE_WIFI -> NetworkType.NETWORK_WIFI
            activeNetInfo.type == ConnectivityManager.TYPE_MOBILE -> NetworkType.NETWORK_MOBILE
            else -> NetworkType.NETWORK_UNKNOWN
        }
    }
}
