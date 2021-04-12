package com.mvvm.component.view

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gyf.immersionbar.ktx.immersionBar
import com.mvvm.component.R
import com.mvvm.component.manager.AppManager
import org.greenrobot.eventbus.EventBus

abstract class BaseBindingActivity<T : ViewDataBinding> : CoroutineActivity() {

    abstract val layoutId: Int

    abstract fun initViewAndData(binding: T)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 强制竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val viewDataBinding = DataBindingUtil.setContentView<T>(this, layoutId)
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
        initViewAndData(viewDataBinding)
        AppManager.addActivity(this)
        observerViewModelEvent()
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

    open fun observerViewModelEvent() {

    }
}
