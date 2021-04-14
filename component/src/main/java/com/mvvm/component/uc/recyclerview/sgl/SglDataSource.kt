package com.mvvm.component.uc.recyclerview.sgl

import com.mvvm.component.uc.recyclerview.ItemListener
import com.mvvm.component.uc.recyclerview.RecyclerManager
import com.mvvm.component.uc.recyclerview.base.AdapterBlock

open class SglDataSource<IT> internal constructor(
	initialData: List<IT> = mutableListOf()
) {

	private var items = initialData.toMutableList()
	private var helper: RecyclerManager<IT>? = null

	val itemListener = ItemListener<IT>()

	operator fun get(index: Int): IT = items[index]

	operator fun contains(item: IT): Boolean = items.contains(item)

	operator fun iterator(): Iterator<IT> = items.iterator()

	fun attach(helper: RecyclerManager<IT>) {
		this.helper ?: apply {
			this.helper = helper
			invalidateAll()
		}
	}

	fun detach() {
		this.helper = null
	}

	fun add(vararg newItems: IT) {
		val startPosition = this.items.size
		this.items.addAll(newItems)
		invalidateList {
			notifyItemRangeInserted(startPosition, newItems.size)
		}
	}

	fun addAll(newItems: Collection<IT>) {
		val startPosition = this.items.size
		this.items.addAll(newItems)
		invalidateList {
			notifyItemRangeInserted(startPosition, newItems.size)
		}
	}

	fun insert(
		index: Int,
		item: IT
	) {
		items.add(index, item)
		invalidateList { notifyItemInserted(index) }
	}

	fun removeAt(index: Int) {
		items.removeAt(index)
		invalidateList { notifyItemRemoved(index) }
	}

	fun remove(item: IT) {
		val index = items.indexOf(item)
		if (index == -1) return
		removeAt(index)
	}

	fun swap(
		left: Int,
		right: Int
	) {
		val leftItem = items[left]
		items[left] = items[right]
		items[right] = leftItem
		invalidateList {
			notifyItemChanged(left)
			notifyItemChanged(right)
		}
	}

	fun move(
		from: Int,
		to: Int
	) {
		val item = items[from]
		items.removeAt(from)
		items.add(to, item)
		invalidateList { notifyItemMoved(from, to) }
	}

	fun clear() {
		items.clear()
		invalidateAll()
	}

	fun size(): Int = items.size

	fun isEmpty(): Boolean = items.isEmpty()

	fun isNotEmpty(): Boolean = items.isNotEmpty()

	fun forEach(block: (IT) -> Unit) = items.forEach(block)

	fun indexOfFirst(predicate: (IT) -> Boolean): Int = items.indexOfFirst(predicate)

	fun indexOfLast(predicate: (IT) -> Boolean): Int = items.indexOfLast(predicate)

	fun indexOf(item: IT): Int = items.indexOf(item)

	fun toList(): List<IT> = items.toList()

	fun invalidateAt(index: Int) = invalidateList { notifyItemChanged(index) }

	fun invalidateAll() = invalidateList { notifyDataSetChanged() }

	private fun invalidateList(block: AdapterBlock) {
		helper?.adapter?.apply {
			block.invoke(this)
		}
	}
}