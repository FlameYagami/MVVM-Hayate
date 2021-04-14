package com.mvvm.component.uc.recyclerview.multi

import com.mvvm.component.uc.recyclerview.base.BaseItemAdapter

/**
 * Created by FlameYagami on 2018/1/3.
 */
open class MultiRecyclerAdapter<ITF, ITS> : BaseItemAdapter<MultiItem>() {

	override fun getItemViewType(position: Int): Int {
		return dataSource[position].itemType.value
	}
}