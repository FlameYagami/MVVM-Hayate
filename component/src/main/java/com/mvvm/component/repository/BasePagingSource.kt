package com.mvvm.component.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState

abstract class BasePagingSource<T : Any> : PagingSource<Int, T>() {

	abstract suspend fun result(pageIndex: Int, pageSize: Int): List<T>

	override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
		return try {
			val page = params.key ?: 1
			val pageSize = params.loadSize
			val result = result(page, pageSize)
			val prevKey = if (page > 1) page - 1 else null
			val nextKey = if (result.isNullOrEmpty()) null else page + 1
			LoadResult.Page(result, prevKey, nextKey)
		} catch (e: Exception) {
			LoadResult.Error(e)
		}
	}

	override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

}