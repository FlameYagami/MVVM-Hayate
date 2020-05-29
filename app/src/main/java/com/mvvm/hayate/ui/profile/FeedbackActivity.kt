package com.mvvm.hayate.ui.profile

import com.mvvm.component.ext.dialogCircularProgress
import com.mvvm.component.ext.dialogToast
import com.mvvm.component.ext.observerEvent
import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.view.BaseBindingActivity
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityFeedbackBinding

class FeedbackActivity : BaseBindingActivity<ActivityFeedbackBinding>() {

    private val viewModel by obtainViewModel<FeedbackVm>()

    override val layoutId: Int
        get() = R.layout.activity_feedback

    override fun initViewAndData(binding: ActivityFeedbackBinding) {
        binding.vm = viewModel.apply {
            dialogToast(this)
            dialogCircularProgress(this)
        }

    }

    override fun observerViewModelEvent() {
        super.observerViewModelEvent()
        observerEvent(viewModel.submitFeedbackEvent) {

        }
    }
}