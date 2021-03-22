package com.mvvm.component.binding.widget

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import com.mvvm.component.R
import com.mvvm.component.uc.recyclerview.RecyclerManager
import com.mvvm.component.binding.androidx.setMultiVm
import com.mvvm.component.binding.androidx.setSglVm
import com.mvvm.component.uc.recyclerview.multi.MultiItem
import com.mvvm.component.uc.recyclerview.multi.MultiItemVm
import com.mvvm.component.uc.recyclerview.sgl.SglItemVm
import com.mvvm.component.uc.widget.ContentLayout
import com.mvvm.component.uc.widget.RefreshStatus

@BindingAdapter("isHeaderEnabled")
fun setHeaderEnabled(view: ContentLayout, isHeaderEnabled: Boolean) {
    view.setHeaderEnabled(isHeaderEnabled)
}

@BindingAdapter("isFooterEnabled")
fun setFooterEnabled(view: ContentLayout, isFooterEnabled: Boolean) {
    view.setFooterEnabled(isFooterEnabled)
}

@BindingAdapter("refreshStatus")
fun setRefreshStatus(view: ContentLayout, refreshStatus: RefreshStatus) {
    view.setRefreshStatus(refreshStatus)
}

@BindingAdapter(value = ["onRefresh", "headerRefreshingAttrChanged", "footerRefreshingAttrChanged"], requireAll = false)
fun setRefreshListener(
        view: ContentLayout,
        listener: ContentLayout.OnRefreshListener?,
        headerRefreshingAttrChanged: InverseBindingListener?,
        footerRefreshingAttrChanged: InverseBindingListener?
) {
    val newValue = object : ContentLayout.OnRefreshListener {

        override fun onHeaderRefresh(pageIndex: Int) {
            listener?.apply {
                headerRefreshingAttrChanged?.onChange()
                onHeaderRefresh(pageIndex)
            }
        }

        override fun onFooterRefresh(pageIndex: Int) {
            listener?.apply {
                footerRefreshingAttrChanged?.onChange()
                onFooterRefresh(pageIndex)
            }
        }
    }

    ListenerUtil.trackListener<ContentLayout.OnRefreshListener>(view, newValue, R.id.contentLayout)?.apply {
        view.setOnRefreshListener(null)
    }
    view.setOnRefreshListener(newValue)
}

@BindingAdapter("sglVm", "layoutManagerHelper", requireAll = false)
fun <IT> setSglVm(
    view: ContentLayout,
    vm: SglItemVm<IT>?,
    recyclerManager: RecyclerManager<IT>?
) {
    setSglVm(view.binding.recyclerView, vm, recyclerManager)
}

@BindingAdapter("multiVm", "layoutManagerHelper", requireAll = false)
fun <ITF, ITS> setMultiVm(
    view: ContentLayout,
    vm: MultiItemVm<ITF, ITS>?,
    recyclerManager: RecyclerManager<MultiItem>?
) {
    setMultiVm(view.binding.recyclerView, vm, recyclerManager)
}
