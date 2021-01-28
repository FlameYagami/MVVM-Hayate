package com.mvvm.hayate.ui.main

import android.widget.Button
import com.mvvm.component.ext.find
import com.mvvm.component.uc.recyclerview.RecyclerManager
import com.mvvm.component.uc.recyclerview.sgl.SglItemVm
import com.mvvm.component.utils.launchMainDelay
import com.mvvm.hayate.R
import com.mvvm.hayate.model.main.First
import com.orhanobut.logger.Logger

class FirstVm : SglItemVm<First>() {

	override val recyclerManager = RecyclerManager<First>()
		.applyLinearLayout()
		.applyItemDataBinding(R.layout.item_first)
		.applyItemDecoration(spanSpace = 16)

	init {
		dataSource.onItemClick { view, data, position ->
			Logger.e("Item Click")
		}
		dataSource.onItemLongClick { view, data, position ->
			Logger.e("Item Long Click")
		}
		dataSource.onChildViewClick { view, data, position ->
			view.find<Button>(R.id.btnTest).setOnClickListener {
				Logger.e("Button Click")
			}
		}
	}

	override fun startViewModel() {
		super.startViewModel()
		launchUI {
			launchMainDelay {
				(0..10).toList().map {
					First(it.toString(), it.toString())
				}.apply {
					dataSource.addAll(this)
				}
			}
		}
	}
}