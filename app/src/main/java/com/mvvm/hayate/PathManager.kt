package com.mvvm.hayate

import android.os.Environment
import com.mvvm.component.BaseApplication

class PathManager {

    private val appDir: String =
        "${Environment.getExternalStorageDirectory().absolutePath}/${BaseApplication.context.getString(R.string.app_name)}/"
    private val logDir: String = "$appDir/log/"
    private val iconDir: String = "$appDir/icon/"
    private val apkDir: String = "$appDir/apk/"
    private val avatarPath: String = "$iconDir/icon.jpg"
    private val avatarTempPath: String = "$iconDir/iconTemp.jpg"

    companion object {

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PathManager()
        }

        var appDir = instance.appDir

        var logDir = instance.logDir

        var profileIconDir = instance.iconDir

        var avatarPath = instance.avatarPath

        var avatarTempPath = instance.avatarTempPath

        var apkDir = instance.apkDir
    }
}
