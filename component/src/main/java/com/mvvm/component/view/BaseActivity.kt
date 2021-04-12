package com.mvvm.component.view

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import com.mvvm.component.R
import com.mvvm.component.manager.AppManager
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity : CoroutineActivity() {

    abstract val layoutId: Int

    abstract fun initViewAndData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 强制竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(layoutId)
        if (applyImmersionBar()) {
            immersionBar {
                fitsSystemWindows(true)
                statusBarColor(R.color.colorPrimary)
                navigationBarColor(R.color.colorPrimary)
                autoDarkModeEnable(true)
            }
        }
        // 设置EventBus
        if (applyEventBus()) EventBus.getDefault().register(this)
        initViewAndData()
        AppManager.addActivity(this)
    }

    override fun onDestroy() {
        // 注销EventBus
        if (applyEventBus()) EventBus.getDefault().unregister(this)
        AppManager.finishActivity(this)
        super.onDestroy()
    }

    open fun applyEventBus(): Boolean {
        return false
    }

    open fun applyImmersionBar(): Boolean {
        return true
    }
}
