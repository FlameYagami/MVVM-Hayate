package com.mvvm.component.uc.recyclerview

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.utils.DisplayUtils

class LayoutManagerHelper {

    enum class LayoutManagerType {
        DEFAULT, LINEAR_VERTICAL, LINEAR_HORIZONTAL
    }

    private var layoutManagerType: LayoutManagerType = LayoutManagerType.DEFAULT
    private var orientation = RecyclerView.VERTICAL
    private var reverseLayout: Boolean = false
    private var spanCount = 1

    var itemDecoration: MyItemDecoration? = null
    var itemRatio: Float? = null

    /**
     * GridLayoutManager默认构造函数
     * @spanCount 行项个数
     * @spanSpace 项距
     * @itemRatio 项宽高比
     * @spanEdge 项距是否一样
     */
    constructor(spanCount: Int,
                spanSpace: Int = 0,
                itemRatio: Float? = null,
                spanEdge: Boolean = true
    ) {
        this.layoutManagerType = LayoutManagerType.DEFAULT
        this.spanCount = spanCount
        this.itemRatio = itemRatio
        this.itemDecoration = MyItemDecoration(spanCount, spanSpacePx(spanSpace), spanEdge)
    }

    /**
     * LinearLayoutManager默认构造函数
     * @spanSpace 项距
     * @spanEdge 项距是否一样
     */
    constructor(spanSpace: Int = 0,
                spanEdge: Boolean = true,
                @RecyclerView.Orientation orientation: Int = RecyclerView.VERTICAL,
                reverseLayout: Boolean = false
    ) {
        this.orientation = orientation
        this.reverseLayout = reverseLayout
        when (orientation) {
            RecyclerView.VERTICAL -> {
                this.layoutManagerType = LayoutManagerType.LINEAR_VERTICAL
                this.itemDecoration = MyItemDecoration(1, spanSpacePx(spanSpace), spanEdge)
            }
            RecyclerView.HORIZONTAL -> {
                this.layoutManagerType = LayoutManagerType.LINEAR_HORIZONTAL
                this.itemDecoration = MyItemDecoration(-1, spanSpacePx(spanSpace), spanEdge)
            }
        }
    }

    private fun spanSpacePx(spanSpace: Int) = DisplayUtils.dp2px(spanSpace)

    fun obtainLayoutManager(context: Context): RecyclerView.LayoutManager? {
        return when (layoutManagerType) {
            LayoutManagerType.DEFAULT -> {
                GridLayoutManager(context, spanCount)
            }
            LayoutManagerType.LINEAR_VERTICAL -> {
                LinearLayoutManager(context, orientation, reverseLayout)
            }
            LayoutManagerType.LINEAR_HORIZONTAL -> {
                LinearLayoutManager(context, orientation, reverseLayout)
            }
        }
    }
}