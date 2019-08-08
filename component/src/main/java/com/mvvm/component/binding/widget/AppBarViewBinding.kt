package com.mvvm.component.binding.widget

import androidx.databinding.BindingAdapter
import com.mvvm.component.uc.widget.AppBarView

@BindingAdapter("textTitle")
fun setTextTitle(view: AppBarView, textTitle: String) {
    view.setTitleText(textTitle)
}

@BindingAdapter("onNavigationClick")
fun setNavigationListener(view: AppBarView, listener: () -> Unit) {
    view.onNavigationClick(listener)
}

@BindingAdapter("onMenuClick")
fun setMenuListener(view: AppBarView, listener: () -> Unit) {
    view.onMenuClick(listener)
}

@BindingAdapter("visibleMenu")
fun setMenuVisible(view: AppBarView, visible: Boolean) {
    view.setMenuVisible(visible)
}