package com.mvvm.component.binding.androidx

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.uc.recyclerview.annotation.AdapterResUtils
import com.mvvm.component.binding.androidx.RecyclerViewBinding.Companion.TAG
import com.mvvm.component.uc.recyclerview.LayoutManagerHelper
import com.mvvm.component.uc.recyclerview.annotation.HeaderResHolder
import com.mvvm.component.uc.recyclerview.annotation.SubResHolder
import com.mvvm.component.uc.recyclerview.annotation.TitleResHolder
import com.mvvm.component.uc.recyclerview.multi.MultiItemVm
import com.mvvm.component.uc.recyclerview.sgl.SglItemVm

/**
 * Created by FlameYagami on 2018/1/15.
 */
open class RecyclerViewBinding {
    companion object {
        val TAG: String = RecyclerViewBinding::class.java.simpleName
    }
}

@BindingAdapter("sglVm", "layoutManagerHelper", requireAll = false)
fun <T> setSglVm(
    view: RecyclerView,
    vm: SglItemVm<T>?,
    layoutManagerHelper: LayoutManagerHelper?
) {
    if (null == vm) {
        throw IllegalArgumentException("$TAG => SglItemVm is null")
    }
    if (null == layoutManagerHelper) {
        throw IllegalArgumentException("$TAG => layoutManagerHelper is null")
    }
    val subView = AdapterResUtils.getItemView(vm.javaClass.getAnnotation(SubResHolder::class.java))
            ?: throw IllegalArgumentException("$TAG => SubView is null, maybe you forget @SubResHolder(R.layout.XXX) in ${vm.javaClass.canonicalName}")
    view.layoutManager = layoutManagerHelper.obtainLayoutManager(view.context)
    view.adapter = vm.adapter.apply { lateInit(subView, layoutManagerHelper) }
    layoutManagerHelper.itemDecoration?.apply { view.addItemDecoration(this) }
}

@BindingAdapter("multiVm", "layoutManagerHelper", requireAll = false)
fun <R, T> setMultiVm(
    view: RecyclerView,
    vm: MultiItemVm<R, T>?,
    layoutManagerHelper: LayoutManagerHelper?
) {
    if (null == vm) {
        throw IllegalArgumentException("$TAG => MultiItemVm is null")
    }
    if (null == layoutManagerHelper) {
        throw IllegalArgumentException("$TAG => LayoutManagerHelper is null")
    }
    val titleView = AdapterResUtils.getItemView(vm.javaClass.getAnnotation(TitleResHolder::class.java))
            ?: throw IllegalArgumentException("$TAG => HeaderView is null, maybe you forget @TitleResHolder(R.layout.XXX) in ${vm.javaClass.canonicalName}")
    val subView = AdapterResUtils.getItemView(vm.javaClass.getAnnotation(SubResHolder::class.java))
            ?: throw IllegalArgumentException("$TAG => SubView is null, maybe you forget @SubResHolder(R.layout.XXX) in ${vm.javaClass.canonicalName}")
    val headerView = AdapterResUtils.getItemView(vm.javaClass.getAnnotation(HeaderResHolder::class.java))
    view.layoutManager = layoutManagerHelper.obtainLayoutManager(view.context)
    view.adapter = vm.adapter.apply { lateInit(titleView, subView, headerView, layoutManagerHelper) }
    layoutManagerHelper.itemDecoration?.apply { view.addItemDecoration(this) }
}