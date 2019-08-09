package com.mvvm.hayate.view.profile

import com.mvvm.component.AppManager
import com.mvvm.component.ext.*
import com.mvvm.component.view.base.BaseBindingActivity
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityFeedbackBinding
import com.mvvm.hayate.vm.profile.FeedbackVm

class FeedbackActivity : BaseBindingActivity<ActivityFeedbackBinding>() {

    lateinit var viewModel: FeedbackVm

    override val layoutId: Int
        get() = R.layout.activity_feedback

    override fun initViewAndData(binding: ActivityFeedbackBinding) {
        binding.vm = obtainViewModel<FeedbackVm>().apply {
            viewModel = this
            dialogToast(this)
            dialogProgress(this)
        }
        observerEvent(viewModel.submitFeedbackEvent) {

        }
    }
}