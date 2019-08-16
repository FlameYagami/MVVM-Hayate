package com.mvvm.hayate.ui.main

import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.view.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentScendBinding

class SecondFragment : BaseBindingFragment<FragmentScendBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_scend

    override fun initView(binding: FragmentScendBinding) {
        obtainViewModel<SecondVm>().apply {

        }
    }

    override fun initData(isViewVisible: Boolean) {

    }
}
