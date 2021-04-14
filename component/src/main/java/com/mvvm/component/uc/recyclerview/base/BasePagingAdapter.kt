package com.mvvm.component.uc.recyclerview.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.mvvm.component.ext.isNotNull
import com.mvvm.component.uc.recyclerview.ItemListener
import com.mvvm.component.uc.recyclerview.ItemType
import com.mvvm.component.uc.recyclerview.PagingManager
import com.mvvm.component.uc.recyclerview.ViewHolder
import com.mvvm.component.utils.DisplayUtils

open class BasePagingAdapter<IT : Any>(itemCallback: DiffUtil.ItemCallback<IT>) : PagingDataAdapter<IT, ViewHolder>(itemCallback) {

	private val itemListener = ItemListener<IT>()

	private lateinit var helper: PagingManager<IT>

	fun attach(helper: PagingManager<IT>) {
		this.helper = helper
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
			setVariable(helper.itemDataBinding.typeForBR(itemType), getItem(position))
			setItemClickListener(holder)
			executePendingBindings()
		}
	}

	private fun applyItemLayoutParams(oldLayoutParams: ViewGroup.LayoutParams) {
		isNotNull(helper.itemRatio, helper.itemDecoration, bothNotNull = { ratio, decoration ->
			val width = (DisplayUtils.screenWidth - decoration.spanSpaceHorizontal * (decoration.spanCount + 1)) / decoration.spanCount
			val height = (width / ratio).toInt()
			if (height > oldLayoutParams.height) oldLayoutParams.height = height
		})
	}

	private fun setItemClickListener(holder: ViewHolder) {
		with(holder){
			binding.root.setOnClickListener {
				itemListener.onItemClickListener?.invoke(binding.root, getItem(absoluteAdapterPosition)!!)
			}
			binding.root.setOnLongClickListener {
				itemListener.onItemLongClickListener?.invoke(binding.root, getItem(absoluteAdapterPosition)!!)
				true
			}
			itemListener.onChildViewClickListener?.invoke(binding.root, getItem(absoluteAdapterPosition)!!)
		}
	}

}