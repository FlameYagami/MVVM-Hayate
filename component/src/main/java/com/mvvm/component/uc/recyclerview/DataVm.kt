package com.mvvm.component.uc.recyclerview

import android.view.View
import androidx.databinding.ObservableArrayList

/**
 * Created by FlameYagami on 2018/1/3.
 */
open class DataVm<T> {

    var data: ObservableArrayList<T> = ObservableArrayList()

    var onItemClickListener: ((view: View, data: List<T>, position: Int) -> Unit)? = null
    var onItemLongClickListener: ((view: View, data: List<T>, position: Int) -> Unit)? = null
    var onViewClickListener: ((view: View, data: List<T>, position: Int) -> Unit)? = null
    var onHeaderClickListener: ((view: View) -> Unit)? = null

    fun onItemClick(listener: (view: View, data: List<T>, position: Int) -> Unit) {
        onItemClickListener = listener
    }

    fun onItemLongClick(listener: (view: View, data: List<T>, position: Int) -> Unit) {
        onItemLongClickListener = listener
    }

    fun onHeaderClick(listener: (view: View) -> Unit) {
        onHeaderClickListener = listener
    }

    fun onViewClick(listener: (view: View, data: List<T>, position: Int) -> Unit) {
        this.onViewClickListener = listener
    }
}