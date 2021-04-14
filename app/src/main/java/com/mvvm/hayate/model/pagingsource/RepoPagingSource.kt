package com.mvvm.hayate.model.pagingsource

import com.mvvm.component.repository.BasePagingSource
import com.mvvm.hayate.api.Api
import com.mvvm.hayate.model.main.Repo

class RepoPagingSource : BasePagingSource<Repo>() {

	override suspend fun result(pageIndex: Int, pageSize: Int): List<Repo> {
		return Api.searchRepos(pageIndex, pageSize).items
	}

}