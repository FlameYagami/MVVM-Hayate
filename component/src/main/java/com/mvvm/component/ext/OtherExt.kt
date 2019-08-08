package com.mvvm.component.ext

import android.view.View

fun <T1, T2> isNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> Unit) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}

val Int.isVisible: Boolean
    get() = this == View.VISIBLE

val Int.isGone: Boolean
    get() = this == View.GONE

val Int.isInVisible: Boolean
    get() = this == View.INVISIBLE
