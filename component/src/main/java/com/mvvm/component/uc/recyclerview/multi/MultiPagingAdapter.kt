package com.mvvm.component.uc.recyclerview.multi

import androidx.recyclerview.widget.DiffUtil
import com.mvvm.component.uc.recyclerview.base.BasePagingAdapter

/**
 * Created by FlameYagami on 2018/1/3.
 */
open class MultiPagingAdapter<ITF, ITS>(itemCallback: DiffUtil.ItemCallback<MultiItem>) : BasePagingAdapter<MultiItem>(itemCallback) {

	override fun getItemViewType(position: Int): Int {
		return getItem(position)!!.itemType.value
	}
}