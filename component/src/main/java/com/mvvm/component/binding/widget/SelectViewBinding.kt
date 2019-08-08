package com.mvvm.component.binding.widget

import android.graphics.drawable.GradientDrawable
import androidx.databinding.BindingAdapter
import com.mvvm.component.uc.widget.SelectView
import com.mvvm.component.utils.ColorUtils

@BindingAdapter("textMessage")
fun setTextMessage(view: SelectView, message: String) {
    view.setMessage(message)
}

@BindingAdapter("subCirclePath", "subCircleErrorSrc", requireAll = false)
fun setSubCirclePath(view: SelectView, path: String, errorSrc: Int? = null) {
    view.setSubCircleImage(path, errorSrc)
}

@BindingAdapter("subDrawable")
fun setSubDrawable(view: SelectView, color: String?) {
    if (color.isNullOrBlank()) return
    val colorInt = ColorUtils.rgb2Int(color)
    val drawable = ColorUtils.getGradientDrawable(colorInt, GradientDrawable.RECTANGLE, view.context)
    view.setSubDrawable(drawable)
}