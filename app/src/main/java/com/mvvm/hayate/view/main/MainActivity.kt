package com.mvvm.hayate.view.main

import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.view.base.BaseBindingActivity
import com.mvvm.component.view.base.BaseFragmentAdapter
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityMainBinding
import com.mvvm.hayate.vm.main.MainVm
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_main

    private val tabFragmentClass = arrayOf(
        FirstFragment::class.java,
        SecondFragment::class.java,
        ThirdFragment::class.java,
        ProfileFragment::class.java
    )

    override fun initViewAndData(binding: ActivityMainBinding) {
        binding.vm = obtainViewModel<MainVm>().apply {
            adapter = BaseFragmentAdapter(supportFragmentManager, tabFragmentClass.toList())
        }
        with(bottomNavigation) {
            enableAnimation(false)
            labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
            isItemHorizontalTranslationEnabled = false
            setTextSize(12F)
            setupWithViewPager(viewPager)
        }
    }
}
