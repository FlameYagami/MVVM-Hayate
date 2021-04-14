package com.mvvm.hayate.ui.main

import android.widget.Button
import com.mvvm.component.ext.find
import com.mvvm.component.uc.recyclerview.RecyclerManager
import com.mvvm.component.uc.recyclerview.sgl.SglRecyclerVm
import com.mvvm.component.utils.launchMainDelay
import com.mvvm.hayate.R
import com.mvvm.hayate.model.main.First
import com.orhanobut.logger.Logger

class FirstVm : SglRecyclerVm<First>() {

	override val recyclerManager = RecyclerManager<First>()
		.applyLinearLayout()
		.applyItemDataBinding(R.layout.item_first)
		.applyItemDecoration(spanSpace = 16)

	init {
		with(dataSource.itemListener) {
			onItemClick { view, item ->
				Logger.e("Item Click")
			}
			onItemLongClick { view, item ->
				Logger.e("Item Long Click")
			}
			onChildViewClick { view, item ->
				view.find<Button>(R.id.btnTest).setOnClickListener {
					Logger.e("Button Click")
				}
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