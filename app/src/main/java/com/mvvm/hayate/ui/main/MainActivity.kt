package com.mvvm.hayate.ui.main

import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.mvvm.component.view.BaseBindingActivity
import com.mvvm.component.view.BaseFragmentAdapter
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

    private val viewModel by viewModel<MainVm>()

    override val layoutId: Int
        get() = R.layout.activity_main

    private val tabFragmentClass = arrayOf(
        FirstFragment::class.java,
        SecondFragment::class.java,
        ThirdFragment::class.java,
        ProfileFragment::class.java
    )

    override fun initViewAndData(binding: ActivityMainBinding) {
        binding.vm = viewModel.apply {
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

    override fun observerViewModelEvent() {

    }
}
