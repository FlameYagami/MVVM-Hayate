package com.mvvm.component.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import org.greenrobot.eventbus.EventBus

/**
 * Created by FlameYagami on 2017/5/3.
 */

abstract class BaseBindingFragment<T : ViewDataBinding> : CoroutineFragment() {

    protected lateinit var viewDataBinding: T

    // 是否初始化控件
    protected var isInitView: Boolean = false

    // 是否初始化数据
    protected var isInitData: Boolean = false

    //当前Fragment是否处于可见状态标志，防止因ViewPager的缓存机制而导致回调函数的触发
    private var isViewVisible: Boolean = false

    abstract val layoutId: Int

    abstract fun initView(binding: T)

    abstract fun initData(isViewVisible: Boolean)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (applyEventBus()) EventBus.getDefault().register(this)
        return DataBindingUtil.inflate<T>(inflater, layoutId, container, false).also {
            viewDataBinding = it
            it.lifecycleOwner = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(viewDataBinding)
        isInitView = true
        // 视图：可见 没有加载过数据
        if (isViewVisible) {
            initData(true)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            isViewVisible = true
        }
        if (!isInitView) {
            return
        }
        // 视图：不可见->可见 没有加载过数据
        if (isViewVisible) {
            initData(true)
            return
        }
        // 视图：可见—>不可见 已经加载过数据
        initData(false)
        isViewVisible = false
    }

    open fun applyEventBus(): Boolean {
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isInitView = false
        if (applyEventBus()) EventBus.getDefault().unregister(this)
    }
}
