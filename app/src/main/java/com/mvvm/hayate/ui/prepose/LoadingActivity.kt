package com.mvvm.hayate.ui.prepose

import android.os.Build
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.mvvm.component.ext.clearTask
import com.mvvm.component.ext.intentFor
import com.mvvm.component.ext.newTask
import com.mvvm.component.utils.FileUtils
import com.mvvm.component.utils.SpUtils
import com.mvvm.component.view.BaseActivity
import com.mvvm.hayate.R
import com.mvvm.hayate.manager.PathManager
import com.mvvm.hayate.manager.ProfileManager
import com.mvvm.hayate.ui.login.LoginActivity
import com.mvvm.hayate.ui.main.MainActivity
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

    private fun checkPermission() = runBlocking {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE)
            askForPermissions(*permissions) { result ->
                if (result.isAllGranted(*permissions)) {
                    FileUtils.createDirectory(PathManager.appDir)
                }
                delayToActivity()
            }
        } else {
            FileUtils.createDirectory(PathManager.appDir)
            delayToActivity()
        }
    }

    override fun applyImmersionBar(): Boolean {
        return false
    }
}