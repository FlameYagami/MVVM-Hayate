package com.mvvm.component.api

data class PagingResp<T>(
	val errCode: Int = 0,
	val errMsg: String? = null,
	val result: List<T>? = null
)