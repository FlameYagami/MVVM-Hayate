package com.mvvm.component.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.greenrobot.eventbus.EventBus

/**
 * Created by FlameYagami on 2017/5/3.
 */

abstract class BaseFragment : CoroutineFragment() {

    // 是否初始化数据
    private var isFirstLoad = true

    abstract val layoutId: Int

    abstract fun initView(view: View)

    abstract fun initData()

    abstract fun observerViewModelEvent()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (applyEventBus()) EventBus.getDefault().register(this)
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerViewModelEvent()
    }

    override fun onResume() {
        super.onResume()
        if (isFirstLoad) {
            initData()
            isFirstLoad = false
        }
    }

    open fun applyEventBus(): Boolean {
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isFirstLoad = false
        if (applyEventBus()) EventBus.getDefault().unregister(this)
    }
}
