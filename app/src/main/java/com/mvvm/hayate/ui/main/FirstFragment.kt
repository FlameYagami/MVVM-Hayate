package com.mvvm.hayate.ui.main

import com.mvvm.component.view.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentFirstBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class FirstFragment : BaseBindingFragment<FragmentFirstBinding>() {

    private val viewModel by viewModel<FirstVm>()

    override val layoutId: Int
        get() = R.layout.fragment_first

    override fun initView(binding: FragmentFirstBinding) {
        binding.vm = viewModel.apply {

        }
    }

    override fun initData() {

    }

    override fun observerViewModelEvent() {

    }
}
