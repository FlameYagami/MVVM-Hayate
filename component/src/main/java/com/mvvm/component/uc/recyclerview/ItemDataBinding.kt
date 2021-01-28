package com.mvvm.component.uc.recyclerview

import androidx.annotation.LayoutRes
import com.mvvm.component.BR
import com.orhanobut.logger.Logger

class ItemDataBinding {

	private val itemTypeToLayout = mutableMapOf<Int, Int>()
	private val itemTypeToBR = mutableMapOf<Int, Int>()

	fun register(@LayoutRes layout: Int, itemType: Int = ItemType.FIRST.value, br: Int = BR.item) {
		itemTypeToLayout[itemType] = layout
		itemTypeToBR[itemType] = br
	}

	fun typeForLayout(type: Int): Int {
		return itemTypeToLayout[type] ?: error(
			"Didn't find layout for type $type"
		)
	}

	fun typeForBR(type: Int): Int {
		return itemTypeToBR[type] ?: error(
			"Didn't find BR for type $type"
		)
	}
}
