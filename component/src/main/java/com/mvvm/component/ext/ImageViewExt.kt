package com.mvvm.component.ext

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

fun ImageView.load(res: Int, errorRes: Int? = null) {
    errorRes?.apply {
        Glide.with(this@load).load(res).apply(RequestOptions().error(this)).into(this@load)
    } ?: apply {
        Glide.with(this).load(res).apply(RequestOptions()).into(this)
    }
}

fun ImageView.load(res: String, errorRes: Int? = null) {
    errorRes?.apply {
        Glide.with(this@load).load(res).apply(RequestOptions().error(this)).into(this@load)
    } ?: apply {
        Glide.with(this).load(res).apply(RequestOptions()).into(this)
    }
}

fun ImageView.loadCircle(res: String, errorRes: Int? = null) {
    errorRes?.apply {
        Glide.with(this@loadCircle).load(res).apply(RequestOptions.circleCropTransform().error(errorRes)).into(this@loadCircle)
    } ?: apply {
        Glide.with(this).load(res).apply(RequestOptions.circleCropTransform()).into(this)
    }
}

fun ImageView.loadGif(res: Int) {
    Glide.with(this).asGif().load(res).into(this)
}

fun ImageView.clear() {
    Glide.with(this).clear(this)
}