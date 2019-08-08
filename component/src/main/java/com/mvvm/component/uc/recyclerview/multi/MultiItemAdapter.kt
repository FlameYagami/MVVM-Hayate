package com.mvvm.component.uc.recyclerview.multi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.ext.isNotNull
import com.mvvm.component.utils.DisplayUtils
import com.mvvm.component.uc.recyclerview.DataVm
import com.mvvm.component.uc.recyclerview.ItemView
import com.mvvm.component.uc.recyclerview.LayoutManagerHelper
import com.mvvm.component.uc.recyclerview.ViewHolder

/**
 * Created by FlameYagami on 2018/1/3.
 */
open class MultiItemAdapter<R, T> : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_TITLE = 1
        const val TYPE_SUB = 2
    }

    var dataVm: DataVm<MultiItem> = DataVm()

    private lateinit var titleView: ItemView
    private lateinit var subView: ItemView
    private var headerView: ItemView? = null
    private lateinit var layoutManagerHelper: LayoutManagerHelper

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemType = getItemViewType(position)
        with(holder.binding) {
            when (itemType) {
                TYPE_SUB -> {
                    setSubNewLayoutParams(root.layoutParams)
                    setVariable(subView.bindingVariable(), dataVm.data[getRealItemPosition(position)])
                    setItemClickListener(holder)
                }
                TYPE_TITLE -> {
                    setVariable(titleView.bindingVariable(), dataVm.data[getRealItemPosition(position)])
                    setItemClickListener(holder)
                }
                TYPE_HEADER -> {
                    dataVm.onHeaderClickListener?.invoke(root)
                }
            }
            executePendingBindings()
        }
    }

    private fun getRealItemPosition(position: Int): Int {
        return headerView?.let { position - 1 } ?: position
    }

    override fun getItemCount(): Int {
        return headerView?.let { dataVm.data.size + 1 } ?: dataVm.data.size
    }

    override fun getItemViewType(position: Int): Int {
        if (null != headerView && 0 == position) return TYPE_HEADER
        return dataVm.data[getRealItemPosition(position)].itemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_SUB -> subView.layoutRes()
            TYPE_TITLE -> titleView.layoutRes()
            TYPE_HEADER -> headerView?.layoutRes() ?: throw IllegalArgumentException("HeaderView is null, maybe you forget @HeaderResHolder(R.layout.XXX)")
            else -> throw IllegalArgumentException("Unknown ViewType")
        }.let {
            val inflater = LayoutInflater.from(parent.context)
            ViewHolder(DataBindingUtil.inflate(inflater, it, parent, false), viewType)
        }
    }

    private fun setItemClickListener(holder: ViewHolder) {
        holder.binding.root.setOnClickListener {
            dataVm.onItemClickListener?.invoke(holder.binding.root, dataVm.data, holder.adapterPosition)
        }
        holder.binding.root.setOnLongClickListener {
            dataVm.onItemLongClickListener?.invoke(holder.binding.root, dataVm.data, holder.adapterPosition)
            false
        }
        dataVm.onViewClickListener?.invoke(holder.binding.root, dataVm.data, holder.adapterPosition)
    }

    open fun lateInit(titleView: ItemView, subView: ItemView, headerView: ItemView?, layoutManagerHelper: LayoutManagerHelper) {
        this.titleView = titleView
        this.subView = subView
        this.layoutManagerHelper = layoutManagerHelper
    }

    private fun setSubNewLayoutParams(oldLayoutParams: ViewGroup.LayoutParams) {
        isNotNull(layoutManagerHelper.itemRatio, layoutManagerHelper.itemDecoration, bothNotNull = { ratio, decoration ->
            val width = (DisplayUtils.screenWidth - decoration.spanSpace * (decoration.spanCount + 1)) / decoration.spanCount
            val height = (width / ratio).toInt()
            if (height > oldLayoutParams.height) oldLayoutParams.height = height
        })
    }

    open fun updateData(multiData: List<MultiItem>) {
        this.dataVm.data.clear()
        addData(multiData)
    }

    open fun addData(multiData: List<MultiItem>) {
        dataVm.data.addAll(multiData)
        notifyDataSetChanged()
    }

    open fun addData(multiData: List<MultiItem>, position: Int) {
        dataVm.data.addAll(position, multiData)
        notifyItemInserted(position)
    }

    open fun getFirstTitleItem(): R? {
        return dataVm.data.firstOrNull { it.itemType == TYPE_TITLE }?.let { it as R }
    }

    open fun getLastTitleItem(): R? {
        return dataVm.data.lastOrNull { it.itemType == TYPE_TITLE }?.let { it as R }
    }

    open fun isEmpty(): Boolean {
        return dataVm.data.isEmpty()
    }
}