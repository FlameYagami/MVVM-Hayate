package com.mvvm.hayate.view

import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.view.base.BaseBindingActivity
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityMainBinding
import com.mvvm.hayate.vm.MainVm

class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initViewAndData(binding: ActivityMainBinding) {
        obtainViewModel<MainVm>().apply {

        }
    }
}
