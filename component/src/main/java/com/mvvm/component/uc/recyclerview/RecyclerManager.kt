package com.mvvm.component.uc.recyclerview

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.BR
import com.mvvm.component.uc.recyclerview.base.BaseItemAdapter
import com.mvvm.component.uc.recyclerview.sgl.SglDataSource
import com.mvvm.component.utils.DisplayUtils

class RecyclerManager<IT>(
	@RecyclerView.Orientation
	private val orientation: Int = RecyclerView.VERTICAL,
	private val reverseLayout: Boolean = false,
	val itemRatio: Float? = null
) {
	val layoutManager = LayoutHelper()
	val itemDataBinding = ItemDataBinding()
	var itemDecoration: ItemDecoration? = null

	lateinit var dataSource: SglDataSource<IT>
	lateinit var adapter: BaseItemAdapter<IT>

	internal fun attach() {
		dataSource.attach(this)
		adapter.attach(this)
	}

	internal fun detach() {
		dataSource.detach()
		adapter.detach()
	}

	fun applyDataSource(iDataSource: SglDataSource<IT>): RecyclerManager<IT> {
		this.dataSource = iDataSource
		return this
	}

	fun applyAdapter(adapter: BaseItemAdapter<IT>): RecyclerManager<IT> {
		this.adapter = adapter
		return this
	}

	fun applyGridLayout(spanCount: Int = 1): RecyclerManager<IT> {
		layoutManager.setupGridLayout(spanCount, reverseLayout)
		return this
	}

	fun applyLinearLayout(): RecyclerManager<IT> {
		layoutManager.setupLinearLayout(orientation, reverseLayout)
		return this
	}

	fun applyItemDataBinding(@LayoutRes layout: Int, itemType: Int = ItemType.FIRST.value, br: Int = BR.item): RecyclerManager<IT> {
		itemDataBinding.register(layout, itemType, br)
		return this
	}

	fun applyItemDecoration(
		spanCount: Int = 1,
		spanSpace: Int = 0,
		spanEdge: Boolean = true
	): RecyclerManager<IT> {
		applyItemDecoration(spanCount, spanSpace, spanSpace, spanEdge)
		return this
	}

	fun applyItemDecoration(
		spanCount: Int = 1,
		spanSpaceVertical: Int = 0,
		spanSpaceHorizontal: Int = 0,
		spanEdge: Boolean = true
	): RecyclerManager<IT> {
		itemDecoration = ItemDecoration(spanCount, spanSpacePx(spanSpaceVertical), spanSpacePx(spanSpaceHorizontal), spanEdge, orientation)
		return this
	}

	private fun spanSpacePx(spanSpace: Int) = DisplayUtils.dp2px(spanSpace)
}


