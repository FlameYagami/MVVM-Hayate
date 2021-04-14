package com.mvvm.component.uc.recyclerview

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.BR
import com.mvvm.component.uc.recyclerview.base.BasePagingAdapter
import com.mvvm.component.utils.DisplayUtils

class PagingManager<IT : Any>(
	@RecyclerView.Orientation
	private val orientation: Int = RecyclerView.VERTICAL,
	private val reverseLayout: Boolean = false,
	val itemRatio: Float? = null
) {
	val layoutManager = LayoutHelper()
	val itemDataBinding = ItemDataBinding()
	var itemDecoration: ItemDecoration? = null

	lateinit var adapter: BasePagingAdapter<IT>

	internal fun attach() {
		adapter.attach(this)
	}

	internal fun detach() {
		adapter.detach()
	}

	fun applyAdapter(adapter: BasePagingAdapter<IT>): PagingManager<IT> {
		this.adapter = adapter
		return this
	}

	fun applyGridLayout(spanCount: Int = 1): PagingManager<IT> {
		layoutManager.setupGridLayout(spanCount, reverseLayout)
		return this
	}

	fun applyLinearLayout(): PagingManager<IT> {
		layoutManager.setupLinearLayout(orientation, reverseLayout)
		return this
	}

	fun applyItemDataBinding(@LayoutRes layout: Int, itemType: Int = ItemType.FIRST.value, br: Int = BR.item): PagingManager<IT> {
		itemDataBinding.register(layout, itemType, br)
		return this
	}

	fun applyItemDecoration(
		spanCount: Int = 1,
		spanSpace: Int = 0,
		spanEdge: Boolean = true
	): PagingManager<IT> {
		applyItemDecoration(spanCount, spanSpace, spanSpace, spanEdge)
		return this
	}

	fun applyItemDecoration(
		spanCount: Int = 1,
		spanSpaceVertical: Int = 0,
		spanSpaceHorizontal: Int = 0,
		spanEdge: Boolean = true
	): PagingManager<IT> {
		itemDecoration = ItemDecoration(spanCount, spanSpacePx(spanSpaceVertical), spanSpacePx(spanSpaceHorizontal), spanEdge, orientation)
		return this
	}

	private fun spanSpacePx(spanSpace: Int) = DisplayUtils.dp2px(spanSpace)
}


