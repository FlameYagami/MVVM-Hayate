package com.mvvm.component.binding.androidx

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.binding.androidx.RecyclerViewBinding.Companion.TAG
import com.mvvm.component.ext.applyItemDecoration
import com.mvvm.component.ext.onAttach
import com.mvvm.component.ext.onDetach
import com.mvvm.component.uc.recyclerview.RecyclerManager
import com.mvvm.component.uc.recyclerview.multi.MultiItem
import com.mvvm.component.uc.recyclerview.multi.MultiItemVm
import com.mvvm.component.uc.recyclerview.sgl.SglItemVm

/**
 * Created by FlameYagami on 2018/1/15.
 */
open class RecyclerViewBinding {
	companion object {
		val TAG: String = RecyclerViewBinding::class.java.simpleName
	}
}

@BindingAdapter("sglVm", "layoutManagerHelper", requireAll = false)
fun <IT> setSglVm(
	view: RecyclerView,
	vm: SglItemVm<IT>?,
	recyclerManager: RecyclerManager<IT>?
) {
	if (null == vm) {
		throw IllegalArgumentException("$TAG => SglItemVm is null")
	}
	if (null == recyclerManager) {
		throw IllegalArgumentException("$TAG => layoutManagerHelper is null")
	}
	recyclerManager
		.applyDataSource(vm.dataSource)
		.applyAdapter(vm.adapter)
	with(view) {
		layoutManager = recyclerManager.layoutManager.build(view.context)
		adapter = vm.adapter
		onAttach { recyclerManager.attach() }
		onDetach { recyclerManager.detach() }
		applyItemDecoration(recyclerManager.itemDecoration)
	}
}

@BindingAdapter("multiVm", "layoutManagerHelper", requireAll = false)
fun <ITF, ITS> setMultiVm(
	view: RecyclerView,
	vm: MultiItemVm<ITF, ITS>?,
	recyclerManager: RecyclerManager<MultiItem>?
) {
	if (null == vm) {
		throw IllegalArgumentException("$TAG => MultiItemVm is null")
	}
	if (null == recyclerManager) {
		throw IllegalArgumentException("$TAG => LayoutManagerHelper is null")
	}
	recyclerManager
		.applyDataSource(vm.dataSource)
		.applyAdapter(vm.adapter)
	with(view) {
		layoutManager = recyclerManager.layoutManager.build(view.context)
		adapter = vm.adapter
		onAttach { recyclerManager.attach() }
		onDetach { recyclerManager.detach() }
		applyItemDecoration(recyclerManager.itemDecoration)
	}
}