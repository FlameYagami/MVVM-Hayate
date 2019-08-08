package com.mvvm.component.binding.androidx

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("errorHint")
fun setErrorHint(view: TextInputLayout, error: String) {
    view.error = error
}