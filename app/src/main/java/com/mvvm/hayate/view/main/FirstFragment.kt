package com.mvvm.hayate.view.main

import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.view.base.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentFirstBinding
import com.mvvm.hayate.vm.main.DeviceVm

class FirstFragment : BaseBindingFragment<FragmentFirstBinding>() {

    lateinit var viewModel: DeviceVm

    override val layoutId: Int
        get() = R.layout.fragment_first

    override fun initView(binding: FragmentFirstBinding) {
        obtainViewModel<DeviceVm>().apply {
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
