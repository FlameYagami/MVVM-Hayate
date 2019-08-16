package com.mvvm.hayate.ui.main

import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.view.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentFirstBinding

class FirstFragment : BaseBindingFragment<FragmentFirstBinding>() {

    lateinit var viewModel: FirstVm

    override val layoutId: Int
        get() = R.layout.fragment_first

    override fun initView(binding: FragmentFirstBinding) {
        obtainViewModel<FirstVm>().apply {
            binding.vm = this
            viewModel = this
        }
        observerEvent()
    }

    override fun initData(isViewVisible: Boolean) {

    }

    private fun observerEvent() {

    }
}
