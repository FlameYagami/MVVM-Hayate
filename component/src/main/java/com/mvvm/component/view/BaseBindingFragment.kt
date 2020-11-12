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

    private lateinit var viewDataBinding: T

    // 是否初始化数据
    private var isFirstLoad = true

    abstract val layoutId: Int

    abstract fun initView(binding: T)

    abstract fun initData()

    abstract fun observerViewModelEvent()

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
