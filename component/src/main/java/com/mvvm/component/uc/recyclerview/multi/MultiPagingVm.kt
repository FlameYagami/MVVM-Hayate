package com.mvvm.component.uc.recyclerview.multi

import androidx.recyclerview.widget.DiffUtil
import com.mvvm.component.uc.recyclerview.base.BaseItemVm
import com.mvvm.component.uc.recyclerview.PagingManager

/**
 * Created by FlameYagami on 2018/1/3.
 */
abstract class MultiPagingVm<ITF, ITS>(itemCallback: DiffUtil.ItemCallback<MultiItem>) : BaseItemVm() {
	abstract val pagingManager: PagingManager<MultiItem>
	var adapter = MultiPagingAdapter<ITF, ITS>(itemCallback)
}