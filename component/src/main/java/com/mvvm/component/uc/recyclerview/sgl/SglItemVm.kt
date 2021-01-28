package com.mvvm.component.uc.recyclerview.sgl

import com.mvvm.component.uc.recyclerview.RecyclerManager
import com.mvvm.component.uc.recyclerview.base.BaseItemVm

/**
 * Created by FlameYagami on 2018/1/3.
 */
abstract class SglItemVm<IT> : BaseItemVm() {
	abstract val recyclerManager: RecyclerManager<IT>
	var dataSource = SglDataSource<IT>()
	var adapter = SglItemAdapter<IT>()
}