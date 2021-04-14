package com.mvvm.component.uc.recyclerview.multi

import com.mvvm.component.uc.recyclerview.RecyclerManager
import com.mvvm.component.uc.recyclerview.base.BaseItemVm
import com.mvvm.component.uc.recyclerview.sgl.SglDataSource

/**
 * Created by FlameYagami on 2018/1/3.
 */
abstract class MultiRecyclerVm<ITF, ITS> : BaseItemVm() {
	abstract val recyclerManager: RecyclerManager<MultiItem>
	var dataSource = SglDataSource<MultiItem>()
	var adapter = MultiRecyclerAdapter<ITF, ITS>()
}