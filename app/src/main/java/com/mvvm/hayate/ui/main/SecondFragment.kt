package com.mvvm.hayate.ui.main

import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.view.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentScendBinding

class SecondFragment : BaseBindingFragment<FragmentScendBinding>() {

    private val viewModel by obtainViewModel<SecondVm>()

    override val layoutId: Int
        get() = R.layout.fragment_scend

    override fun initView(binding: FragmentScendBinding) {
        binding.vm = viewModel.apply {

        }
    }

    override fun initData() {

    }

    override fun observerViewModelEvent() {

    }
}
