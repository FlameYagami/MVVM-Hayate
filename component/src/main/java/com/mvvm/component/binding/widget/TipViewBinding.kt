package com.mvvm.component.binding.widget

import androidx.databinding.BindingAdapter
import com.mvvm.component.uc.widget.ContentLayout

@BindingAdapter("tipImage")
fun setTipImage(view: ContentLayout, res: Int) {
    view.tipImage(res)
}

@BindingAdapter("tipText")
fun setTipText(view: ContentLayout, res: Int) {
    view.tipText(res)
}