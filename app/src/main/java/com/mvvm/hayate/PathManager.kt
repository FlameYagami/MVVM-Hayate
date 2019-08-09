package com.mvvm.hayate

import android.os.Environment
import com.mvvm.component.BaseApplication
import java.io.File

class PathManager {

    private val appDir: String =
        "${Environment.getExternalStorageDirectory().absolutePath}/${BaseApplication.context.getString(R.string.app_name)}/"
    private val logDir: String = appDir + "log" + File.separator
    private val iconDir: String = appDir + "icon" + File.separator
    private val apkDir: String = appDir + "apk" + File.separator
    private val profileIconPath: String = iconDir + "icon.jpg"
    private val profileIconTempPath: String = iconDir + "iconTemp.jpg"

    companion object {

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PathManager()
        }

        var appDir: String = instance.appDir

        var logDir: String = instance.logDir

        var profileIconDir = instance.iconDir

        var profileIconPath: String = instance.profileIconPath

        var profileIconTempPath: String = instance.profileIconTempPath

        var apkDir: String = instance.apkDir
    }
}
