package com.mvvm.hayate.view.main

import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.view.base.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentSceneBinding
import com.mvvm.hayate.vm.main.SceneVm

class SceneFragment : BaseBindingFragment<FragmentSceneBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_scene

    override fun initView(binding: FragmentSceneBinding) {
        obtainViewModel<SceneVm>().apply {

        }
    }

    override fun initData(isViewVisible: Boolean) {

    }
}
