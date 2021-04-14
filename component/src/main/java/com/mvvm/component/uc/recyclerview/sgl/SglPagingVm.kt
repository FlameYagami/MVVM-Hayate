package com.mvvm.component.uc.recyclerview.sgl

import androidx.recyclerview.widget.DiffUtil
import com.mvvm.component.uc.recyclerview.base.BaseItemVm
import com.mvvm.component.uc.recyclerview.PagingManager

/**
 * Created by FlameYagami on 2018/1/3.
 */
abstract class SglPagingVm<IT : Any>(itemCallback: DiffUtil.ItemCallback<IT>) : BaseItemVm() {
	abstract val pagingManager: PagingManager<IT>
	val adapter = SglPagingAdapter(itemCallback)
}