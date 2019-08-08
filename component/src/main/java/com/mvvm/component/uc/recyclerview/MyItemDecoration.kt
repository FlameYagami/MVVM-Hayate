package com.mvvm.component.uc.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MyItemDecoration(val spanCount: Int,
                       val spanSpace: Int,
                       private val includeEdge: Boolean = true,
                       @RecyclerView.Orientation val orientation: Int = RecyclerView.VERTICAL
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        when (orientation) {
            RecyclerView.VERTICAL -> {
                val position = parent.getChildAdapterPosition(view)
                val column = position % spanCount
                if (includeEdge) {
                    outRect.left = spanSpace - column * spanSpace / spanCount
                    outRect.right = (column + 1) * spanSpace / spanCount
                    if (position < spanCount) {
                        outRect.top = spanSpace
                    }
                    outRect.bottom = spanSpace
                } else {
                    outRect.left = column * spanSpace / spanCount
                    outRect.right = spanSpace - (column + 1) * spanSpace / spanCount
                    if (position >= spanCount) {
                        outRect.top = spanSpace
                    }
                }
            }
            RecyclerView.HORIZONTAL -> {

            }
        }
    }
}