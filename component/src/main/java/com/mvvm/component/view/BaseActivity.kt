package com.mvvm.component.view

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.mvvm.component.AppManager
import com.mvvm.component.utils.StatusBarUtils
import me.imid.swipebacklayout.lib.SwipeBackLayout
import me.imid.swipebacklayout.lib.app.SwipeBackActivity
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity : SwipeBackActivity() {

    abstract val layoutId: Int

    abstract fun initViewAndData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 强制竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(layoutId)
        // 设置状态栏透明度,必须在setContentView()之后
        if (applyStatusBar()) StatusBarUtils.setTranslucent(this, 16)
        // 设置EventBus
        if (applyEventBus()) EventBus.getDefault().register(this)
        // 设置SwipeBackActivity
        if (applySwipeBack()) {
            setSwipeBackEnable(true)
            swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
        } else setSwipeBackEnable(false)
        initViewAndData()
        AppManager.addActivity(this)
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

    open fun applySwipeBack(): Boolean {
        return true
    }

    open fun applyStatusBar(): Boolean {
        return true
    }
}
