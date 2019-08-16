package com.mvvm.hayate.view.prepose

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import com.mvvm.component.ext.clearTask
import com.mvvm.component.ext.intentFor
import com.mvvm.component.ext.newTask
import com.mvvm.component.utils.FileUtils
import com.mvvm.component.utils.SpUtils
import com.mvvm.component.view.base.BaseActivity
import com.mvvm.hayate.PathManager
import com.mvvm.hayate.ProfileManager
import com.mvvm.hayate.R
import com.mvvm.hayate.view.login.LoginActivity
import com.mvvm.hayate.view.main.MainActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoadingActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_loading

    override fun initViewAndData() {
        checkPermission()
    }

    private fun delayToActivity() {
        GlobalScope.launch {
            delay(2000)
            when {
                SpUtils.getString(ProfileManager.PROFILE_INFO).isNotBlank() -> startActivity(intentFor<MainActivity>().newTask().clearTask())
                else -> startActivity(intentFor<LoginActivity>().newTask().clearTask())
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun checkPermission() = runBlocking {
        if (Build.VERSION.SDK_INT >= 23) {
            RxPermissions(this@LoadingActivity).request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).subscribe { granted ->
                if (granted) {
                    FileUtils.createDirectory(PathManager.appDir)
                }
                delayToActivity()
            }
        } else {
            FileUtils.createDirectory(PathManager.appDir)
            delayToActivity()
        }
    }

    override fun applySwipeBack(): Boolean {
        return false
    }

    override fun applyStatusBar(): Boolean {
        return false
    }
}