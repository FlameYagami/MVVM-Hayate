package com.mvvm.component.uc.recyclerview

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LayoutHelper {

	@RecyclerView.Orientation
	private var orientation = RecyclerView.VERTICAL
	private var reverseLayout = false
	private var spanCount = 1
	private var layoutManagerType = LayoutManagerType.GRID

	enum class LayoutManagerType {
		GRID, LINEAR
	}

	fun setupLinearLayout(@RecyclerView.Orientation orientation: Int, reverseLayout: Boolean = false) {
		this.orientation = orientation
		this.reverseLayout = reverseLayout
		this.layoutManagerType = LayoutManagerType.LINEAR
	}

	fun setupGridLayout(spanCount: Int, reverseLayout: Boolean = false) {
		this.spanCount = spanCount
		this.reverseLayout = reverseLayout
		this.layoutManagerType = LayoutManagerType.GRID
	}

	fun build(context: Context): RecyclerView.LayoutManager {
		return when (layoutManagerType) {
			LayoutManagerType.GRID -> {
				GridLayoutManager(context, spanCount)
			}
			LayoutManagerType.LINEAR -> {
				LinearLayoutManager(context, orientation, reverseLayout)
			}
		}
	}
}