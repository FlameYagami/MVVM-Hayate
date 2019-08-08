package com.mvvm.component.binding.widget

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import com.mvvm.component.R
import com.mvvm.component.uc.recyclerview.LayoutManagerHelper
import com.mvvm.component.binding.androidx.setMultiVm
import com.mvvm.component.binding.androidx.setSglVm
import com.mvvm.component.uc.recyclerview.multi.MultiItemVm
import com.mvvm.component.uc.recyclerview.sgl.SglItemVm
import com.mvvm.component.uc.widget.ContentLayout
import com.mvvm.component.uc.widget.RefreshStatus
import kotlinx.android.synthetic.main.widget_content_layout.view.*

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
fun <T> setSglVm(
    view: ContentLayout,
    vm: SglItemVm<T>?,
    layoutManagerHelper: LayoutManagerHelper?
) {
    setSglVm(view.recyclerView, vm, layoutManagerHelper)
}

@BindingAdapter("multiVm", "layoutManagerHelper", requireAll = false)
fun <R, T> setMultiVm(
    view: ContentLayout,
    vm: MultiItemVm<R, T>?,
    layoutManagerHelper: LayoutManagerHelper?
) {
    setMultiVm(view.recyclerView, vm, layoutManagerHelper)
}
