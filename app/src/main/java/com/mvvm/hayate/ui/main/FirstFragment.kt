package com.mvvm.hayate.ui.main

import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.view.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentFirstBinding

class FirstFragment : BaseBindingFragment<FragmentFirstBinding>() {

	private val viewModel by obtainViewModel<FirstVm>()

	override val layoutId: Int
		get() = R.layout.fragment_first

	override fun initView(binding: FragmentFirstBinding) {
		binding.vm = viewModel.apply {
			startViewModel()
		}
	}

	override fun initData() {

	}

	override fun observerViewModelEvent() {

	}
}
