package com.mvvm.hayate.view.main

import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.view.base.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentScendBinding
import com.mvvm.hayate.vm.main.SceneVm

class SecondFragment : BaseBindingFragment<FragmentScendBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_scend

    override fun initView(binding: FragmentScendBinding) {
        obtainViewModel<SceneVm>().apply {

        }
    }

    override fun initData(isViewVisible: Boolean) {

    }
}
