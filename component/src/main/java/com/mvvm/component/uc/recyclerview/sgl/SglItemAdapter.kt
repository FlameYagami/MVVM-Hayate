package com.mvvm.component.uc.recyclerview.sgl

import com.mvvm.component.uc.recyclerview.ItemType
import com.mvvm.component.uc.recyclerview.base.BaseItemAdapter

/**
 * Created by FlameYagami on 2018/1/3.
 */
open class SglItemAdapter<T> : BaseItemAdapter<T>() {

	override fun getItemViewType(position: Int): Int {
		return ItemType.FIRST.value
	}
}