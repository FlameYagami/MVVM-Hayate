package com.mvvm.hayate.manager

import com.mvvm.component.BaseApplication.Companion.context

class PathManager {

    private val appDir = "${context.getExternalFilesDir("")?.absolutePath}${java.io.File.separator}"
    private val logDir = "$appDir/log/"
    private val iconDir = "$appDir/icon/"
    private val apkDir = "$appDir/apk/"
    private val avatarPath = "$iconDir/icon.jpg"
    private val avatarTempPath = "$iconDir/iconTemp.jpg"

    companion object {

        private val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
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
