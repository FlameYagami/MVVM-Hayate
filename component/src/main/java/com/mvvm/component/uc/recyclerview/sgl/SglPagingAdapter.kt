package com.mvvm.component.uc.recyclerview.sgl

import androidx.recyclerview.widget.DiffUtil
import com.mvvm.component.uc.recyclerview.ItemType
import com.mvvm.component.uc.recyclerview.base.BasePagingAdapter

/**
 * Created by FlameYagami on 2018/1/3.
 */
open class SglPagingAdapter<IT : Any>(itemCallback: DiffUtil.ItemCallback<IT>) : BasePagingAdapter<IT>(itemCallback) {

	override fun getItemViewType(position: Int): Int {
		return ItemType.FIRST.value
	}
}