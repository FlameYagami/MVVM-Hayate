package com.mvvm.component.uc.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(
    val spanCount: Int = 1,
    val spanSpaceVertical: Int = 0,
    val spanSpaceHorizontal: Int = 0,
    private val spanEdge: Boolean = true,
    private val orientation: Int = RecyclerView.VERTICAL
) : RecyclerView.ItemDecoration() {

	constructor(
        spanCount: Int = 1,
        spanSpace: Int = 0,
        spanEdge: Boolean = true,
        orientation: Int,
    ) : this(spanCount, spanSpace, spanSpace, spanEdge, orientation)

	override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
		when (orientation) {
            RecyclerView.VERTICAL -> {
                val position = parent.getChildAdapterPosition(view)
                val column = position % spanCount
                if (spanEdge) {
                    outRect.left = spanSpaceHorizontal - column * spanSpaceHorizontal / spanCount
                    outRect.right = (column + 1) * spanSpaceHorizontal / spanCount
                    if (position < spanCount) {
                        outRect.top = spanSpaceVertical
                    }
                    outRect.bottom = spanSpaceVertical
                } else {
                    outRect.left = column * spanSpaceHorizontal / spanCount
                    outRect.right = spanSpaceHorizontal - (column + 1) * spanSpaceHorizontal / spanCount
                    if (position >= spanCount) {
                        outRect.top = spanSpaceVertical
                    }
                }
            }
            RecyclerView.HORIZONTAL -> {

            }
		}
	}
}