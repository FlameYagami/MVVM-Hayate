package com.mvvm.component.binding.androidx

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("selected")
fun setSelected(view: View, isSelected: Boolean) {
    view.isSelected = isSelected
}

@BindingAdapter("enabled")
fun setEnabled(view: View, isEnabled: Boolean) {
    view.isEnabled = isEnabled
}