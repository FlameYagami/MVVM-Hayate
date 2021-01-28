package com.mvvm.component.uc.recyclerview.multi

import com.mvvm.component.uc.recyclerview.sgl.SglDataSource

open class MultiDataSource<MultiItem> internal constructor(
	initialData: List<MultiItem> = mutableListOf()
) : SglDataSource<MultiItem>(initialData) {

}