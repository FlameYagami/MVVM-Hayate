package com.mvvm.hayate.model.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import com.mvvm.hayate.model.main.Repo
import com.mvvm.hayate.model.pagingsource.RepoPagingSource
import kotlinx.coroutines.flow.Flow

object RepoRepository {

	private const val PAGE_SIZE = 15

	fun getPagingData(): Flow<PagingData<Repo>> {
		return Pager(
			config = PagingConfig(PAGE_SIZE),
			pagingSourceFactory = { RepoPagingSource() }
		).flow
	}

	val itemCallback = object : DiffUtil.ItemCallback<Repo>() {
		override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
			return oldItem == newItem
		}

		override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
			return oldItem.id == newItem.id
		}
	}
}