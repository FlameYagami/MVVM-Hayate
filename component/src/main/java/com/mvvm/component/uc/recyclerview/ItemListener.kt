package com.mvvm.component.uc.recyclerview

import android.view.View

class ItemListener<IT> {

	var onItemClickListener: onItemClickListener<IT> = null
	var onItemLongClickListener: onItemLongClickListener<IT> = null
	var onChildViewClickListener: onChildViewClickListener<IT> = null

	fun onItemClick(listener: onItemClickListener<IT>) {
		onItemClickListener = listener
	}

	fun onItemLongClick(listener: onItemLongClickListener<IT>) {
		onItemLongClickListener = listener
	}

	fun onChildViewClick(listener: onChildViewClickListener<IT>) {
		this.onChildViewClickListener = listener
	}
}

internal typealias onItemClickListener<IT> = ((view: View, item: IT) -> Unit)?
internal typealias onItemLongClickListener<IT> = ((view: View, item: IT) -> Unit)?
internal typealias onChildViewClickListener<IT> = ((view: View, item: IT) -> Unit)?