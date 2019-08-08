package com.mvvm.component.binding.widget

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import com.mvvm.component.R
import com.mvvm.component.uc.refresh.RefreshLayout
import com.mvvm.component.uc.widget.RefreshStatus

@InverseBindingAdapter(attribute = "isHeaderRefreshing", event = "headerRefreshingAttrChanged")
fun isHeaderRefreshing(view: RefreshLayout): Boolean {
    return view.isHeaderRefreshing
}

@InverseBindingAdapter(attribute = "isFooterRefreshing", event = "footerRefreshingAttrChanged")
fun isFooterRefreshing(view: RefreshLayout): Boolean {
    return view.isFooterRefreshing
}

@BindingAdapter("isHeaderRefreshing")
fun setHeaderRefreshing(view: RefreshLayout, refreshing: Boolean) {
    if (refreshing != view.isHeaderRefreshing) {
        view.isHeaderRefreshing = refreshing
    }
}

@BindingAdapter("isFooterRefreshing")
fun setFooterRefreshing(view: RefreshLayout, refreshing: Boolean) {
    if (refreshing != view.isFooterRefreshing) {
        view.isFooterRefreshing = refreshing
    }
}

@BindingAdapter("headerColorSchemeColors")
fun setHeaderColorSchemeColors(view: RefreshLayout, colors: IntArray) {
    view.setHeaderColorSchemeResources(*colors)
}

@BindingAdapter("refreshStatus")
fun setRefreshStatus(view: RefreshLayout, refreshStatus: RefreshStatus) {
    with(view) {
        when (refreshStatus) {
            RefreshStatus.NA -> {
                isHeaderRefreshing = false
                isFooterRefreshing = false
            }
            RefreshStatus.HEADER_REFRESHING -> isHeaderRefreshing = true
            RefreshStatus.FOOTER_REFRESHING -> isFooterRefreshing = true
            RefreshStatus.HEADER_SUCCESS -> {
                isHeaderRefreshing = false
            }
            RefreshStatus.HEADER_FAILURE -> {
                isHeaderRefreshing = false
            }
            RefreshStatus.FOOTER_SUCCESS -> {
                isFooterRefreshing = false
            }
            RefreshStatus.FOOTER_FAILURE -> {
                isFooterRefreshing = false
            }
        }
    }
}

@BindingAdapter(value = ["onRefresh", "headerRefreshingAttrChanged", "footerRefreshingAttrChanged"], requireAll = false)
fun setRefreshListener(view: RefreshLayout,
                       listener: RefreshLayout.OnRefreshListener?,
                       headerRefreshingAttrChanged: InverseBindingListener?,
                       footerRefreshingAttrChanged: InverseBindingListener?) {

    val newValue = object : RefreshLayout.OnRefreshListener {

        override fun onHeaderRefresh() {
            listener?.apply {
                headerRefreshingAttrChanged?.onChange()
                onHeaderRefresh()
            }
        }

        override fun onFooterRefresh() {
            listener?.apply {
                footerRefreshingAttrChanged?.onChange()
                onFooterRefresh()
            }
        }
    }

    ListenerUtil.trackListener<RefreshLayout.OnRefreshListener>(view, newValue, R.id.refreshLayout)?.apply {
        view.setOnRefreshListener(null)
    }
    view.setOnRefreshListener(newValue)
}

