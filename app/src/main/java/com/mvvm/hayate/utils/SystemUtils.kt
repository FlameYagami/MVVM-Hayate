package com.mvvm.hayate.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.LocaleList
import androidx.core.content.FileProvider
import com.mvvm.component.BaseApplication.Companion.context
import com.mvvm.hayate.BuildConfig
import java.io.File
import java.util.*


/**
 * Created by FlameYagami on 2018/5/9.
 */
object SystemUtils {

    /**
     * 获取当前系统版本名称
     */
    val versionName: String
        get() {
            return try {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                "1.0.0"
            }
        }

    /**
     * 安装apk
     */
    fun installApk(apkFile: File) {
        Intent(Intent.ACTION_VIEW).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.fileProvider", apkFile)
                setDataAndType(contentUri, "application/vnd.android.package-archive")
            } else {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val contentUri = Uri.fromFile(apkFile)
                setDataAndType(contentUri, "application/vnd.android.package-archive")
            }
            context.startActivity(this)
        }
    }
}
