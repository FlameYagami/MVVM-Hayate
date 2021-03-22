package com.mvvm.hayate.ui.main

import androidx.lifecycle.MutableLiveData
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.view.BaseVm
import com.mvvm.hayate.R

class SecondVm : BaseVm() {

	var menuEvent = MutableLiveData<LiveDataEvent<Int>>()

	open var onMenuClick = {
		menuEvent.value = LiveDataEvent(R.array.menu_operation_test)
	}
}