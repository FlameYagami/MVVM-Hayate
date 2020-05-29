package com.mvvm.component.view

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.mvvm.component.AppManager
import com.mvvm.component.utils.StatusBarUtils
import org.greenrobot.eventbus.EventBus

abstract class BaseBindingActivity<T : ViewDataBinding> : CoroutineActivity() {

    abstract val layoutId: Int

    abstract fun initViewAndData(binding: T)

    abstract fun observerViewModelEvent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 强制竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val viewDataBinding = DataBindingUtil.setContentView<T>(this, layoutId)
        // 设置状态栏透明度,必须在setContentView()之后
        if (applyStatusBar()) StatusBarUtils.setTranslucent(this, 16)
        // 设置EventBus
        if (applyEventBus()) EventBus.getDefault().register(this)
        initViewAndData(viewDataBinding)
        AppManager.addActivity(this)
        observerViewModelEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 注销EventBus
        if (applyEventBus()) EventBus.getDefault().unregister(this)
        AppManager.finishActivity(this)
    }

    open fun applyEventBus(): Boolean {
        return false
    }

    open fun applyStatusBar(): Boolean {
        return true
    }
}
