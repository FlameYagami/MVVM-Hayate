package com.mvvm.hayate.view.main

import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.view.base.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentDeviceBinding
import com.mvvm.hayate.vm.main.DeviceVm

class DeviceFragment : BaseBindingFragment<FragmentDeviceBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_device

    override fun initView(binding: FragmentDeviceBinding) {
        obtainViewModel<DeviceVm>().apply {
            binding.vm = this
        }
        observerEvent()
    }

    override fun initData(isViewVisible: Boolean) {

    }

    private fun observerEvent() {

    }
}
