package com.mvvm.component.uc.recyclerview.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.ext.isNotNull
import com.mvvm.component.uc.recyclerview.*
import com.mvvm.component.uc.recyclerview.sgl.SglDataSource
import com.mvvm.component.utils.DisplayUtils

typealias AdapterBlock = RecyclerView.Adapter<*>.() -> Unit

open class BaseItemAdapter<IT> : RecyclerView.Adapter<ViewHolder>() {

	private lateinit var helper: RecyclerManager<IT>
	protected lateinit var dataSource: SglDataSource<IT>

	fun attach(helper: RecyclerManager<IT>) {
		this.helper = helper.also {
			dataSource = it.dataSource
		}
	}

	fun detach() {

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return when (viewType) {
			ItemType.FIRST.value, ItemType.SECOND.value -> helper.itemDataBinding.typeForLayout(viewType)
			else -> throw IllegalArgumentException("Unknown ViewType")
		}.let {
			ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), it, parent, false), viewType)
		}
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val itemType = getItemViewType(position)
		with(holder.binding) {
			setVariable(helper.itemDataBinding.typeForBR(itemType), dataSource[position])
			setItemClickListener(holder)
			executePendingBindings()
		}
	}

	override fun getItemCount(): Int {
		return dataSource.size()
	}

	private fun applyItemLayoutParams(oldLayoutParams: ViewGroup.LayoutParams) {
		isNotNull(helper.itemRatio, helper.itemDecoration, bothNotNull = { ratio, decoration ->
			val width = (DisplayUtils.screenWidth - decoration.spanSpaceHorizontal * (decoration.spanCount + 1)) / decoration.spanCount
			val height = (width / ratio).toInt()
			if (height > oldLayoutParams.height) oldLayoutParams.height = height
		})
	}

	private fun setItemClickListener(holder: ViewHolder) {
		holder.binding.root.setOnClickListener {
			dataSource.onItemClickListener?.invoke(holder.binding.root, dataSource.toList(), holder.adapterPosition)
		}
		holder.binding.root.setOnLongClickListener {
			dataSource.onItemLongClickListener?.invoke(holder.binding.root, dataSource.toList(), holder.adapterPosition)
			true
		}
		dataSource.onChildViewClickListener?.invoke(holder.binding.root, dataSource.toList(), holder.adapterPosition)
	}
}