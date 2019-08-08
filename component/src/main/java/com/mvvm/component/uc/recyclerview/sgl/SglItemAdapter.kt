package com.mvvm.component.uc.recyclerview.sgl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.ext.isNotNull
import com.mvvm.component.ext.onPropertyChanged
import com.mvvm.component.utils.DisplayUtils
import com.mvvm.component.uc.recyclerview.DataVm
import com.mvvm.component.uc.recyclerview.ItemView
import com.mvvm.component.uc.recyclerview.LayoutManagerHelper
import com.mvvm.component.uc.recyclerview.ViewHolder

/**
 * Created by FlameYagami on 2018/1/3.
 */
open class SglItemAdapter<T> : RecyclerView.Adapter<ViewHolder>() {

    var dataVm: DataVm<T> = DataVm()
    private lateinit var subView: ItemView
    private lateinit var layoutManagerHelper: LayoutManagerHelper

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            setNewLayoutParams(root.layoutParams)
            setVariable(subView.bindingVariable(), dataVm.data[position])
            executePendingBindings()
        }
        setItemClickListener(holder)
    }

    override fun getItemCount(): Int {
        return dataVm.data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(DataBindingUtil.inflate(inflater, subView.layoutRes(), parent, false), viewType)
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

    open fun lateInit(subView: ItemView, layoutManagerHelper: LayoutManagerHelper) {
        this.subView = subView
        this.layoutManagerHelper = layoutManagerHelper
    }

    private fun setNewLayoutParams(oldLayoutParams: ViewGroup.LayoutParams) {
        isNotNull(layoutManagerHelper.itemRatio, layoutManagerHelper.itemDecoration, bothNotNull = { ratio, decoration ->
            val width = (DisplayUtils.screenWidth - decoration.spanSpace * (decoration.spanCount + 1)) / decoration.spanCount
            val height = (width / ratio).toInt()
            if (height > oldLayoutParams.height) oldLayoutParams.height = height
        })
    }

    open fun onDataPropertyChanged(listener: (list: ObservableList<T>) -> Unit) {
        dataVm.data.onPropertyChanged {
            listener.invoke(it)
        }
    }

    open fun updateData(data: List<T>) {
        dataVm.data.clear()
        dataVm.data.addAll(data)
        notifyDataSetChanged()
    }

    open fun addData(data: T) {
        dataVm.data.add(data)
        notifyDataSetChanged()
    }

    open fun addData(data: List<T>) {
        dataVm.data.addAll(data)
        notifyDataSetChanged()
    }

    open fun removeData(position: Int) {
        dataVm.data.removeAt(position)
        notifyDataSetChanged()
    }

    open fun removeData(data: T) {
        dataVm.data.remove(data)
        notifyDataSetChanged()
    }

    open fun replaceData(data: T, position: Int) {
        dataVm.data.removeAt(position)
        dataVm.data.add(position, data)
        notifyDataSetChanged()
    }

    open fun clearData() {
        dataVm.data.clear()
        notifyDataSetChanged()
    }

    open fun isEmpty(): Boolean {
        return dataVm.data.isEmpty()
    }
}