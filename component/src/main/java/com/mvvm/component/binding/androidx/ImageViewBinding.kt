package com.mvvm.component.binding.androidx

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.mvvm.component.ext.clear
import com.mvvm.component.ext.load
import com.mvvm.component.ext.loadCircle
import com.mvvm.component.ext.loadGif

@BindingAdapter("glideSrc")
fun setGlideSrc(view: ImageView, src: Int) {
    if (-1 == src) view.clear()
    else view.load(src)
}

@BindingAdapter("profileIconPath", "profileIconError")
fun setProfileIconPath(view: ImageView, path: String, src: Int) {
    view.loadCircle(path, src)
}

@BindingAdapter("glideGifSrc")
fun setGlideGifSrc(view: ImageView, src: Int) {
    if (-1 == src) view.clear()
    else view.loadGif(src)
}