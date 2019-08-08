package com.mvvm.component.binding.androidx

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import com.google.android.material.appbar.AppBarLayout
import com.mvvm.component.R

@BindingAdapter(value = ["onOffsetListener", "verticalOffsetAttrChanged"], requireAll = false)
fun setOffsetListener(view: AppBarLayout,
                      listener: AppBarLayout.OnOffsetChangedListener?,
                      verticalOffsetAttrChanged: InverseBindingListener?) {

    val newValue = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        listener?.apply {
            verticalOffsetAttrChanged?.onChange()
            onOffsetChanged(appBarLayout, verticalOffset)
        }
    }

    ListenerUtil.trackListener(view, newValue, R.id.appBarLayout)?.apply {
        view.addOnOffsetChangedListener(null)
    }
    view.addOnOffsetChangedListener(newValue)
}
