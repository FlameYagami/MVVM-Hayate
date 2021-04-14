package com.mvvm.hayate.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.ext.collectEx
import com.mvvm.component.uc.recyclerview.PagingManager
import com.mvvm.component.uc.recyclerview.sgl.SglPagingVm
import com.mvvm.hayate.R
import com.mvvm.hayate.model.main.Repo
import com.mvvm.hayate.model.repository.RepoRepository

class SecondVm : SglPagingVm<Repo>(RepoRepository.itemCallback) {

	var menuEvent = MutableLiveData<LiveDataEvent<Int>>()

	open var onMenuClick = {
		menuEvent.value = LiveDataEvent(R.array.menu_operation_test)
	}

	override val pagingManager = PagingManager<Repo>()
		.applyLinearLayout()
		.applyItemDataBinding(R.layout.item_repo)
		.applyItemDecoration(spanSpace = 16)

	override fun startViewModel() {
		super.startViewModel()
		launchUI {
			RepoRepository.getPagingData().cachedIn(viewModelScope).collectEx {
				adapter.submitData(it)
			}
		}
	}
}