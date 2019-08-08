package com.mvvm.component.uc.recyclerview.annotation

import androidx.annotation.LayoutRes
import com.mvvm.component.uc.recyclerview.ItemView

object AdapterResUtils {

    const val BR_NONE = -1
    const val BR_ITEM = 128

    fun getItemView(subRes: SubResHolder?): ItemView? {
        return subRes?.let {
            ItemView.of(if (it.br != BR_NONE) it.br else BR_ITEM, it.layout)
        }
    }

    fun getItemView(titleRes: TitleResHolder?): ItemView? {
        return titleRes?.let {
            ItemView.of(if (it.br != BR_NONE) it.br else BR_ITEM, it.layout)
        }
    }

    fun getItemView(headerRes: HeaderResHolder?): ItemView? {
        return headerRes?.let {
            ItemView.of(if (it.br != BR_NONE) it.br else BR_ITEM, it.layout)
        }
    }

    fun getItemView(@LayoutRes layout: Int, br: Int): ItemView? {
        return ItemView.of(if (br != BR_NONE) br else BR_ITEM, layout)
    }
}
