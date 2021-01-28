package com.mvvm.component.ext

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.applyItemDecoration(itemDecoration: RecyclerView.ItemDecoration? = null) {
	itemDecoration?.apply { addItemDecoration(this) }
}