package com.mvvm.hayate.ui.profile

import com.mvvm.component.ext.dialogCircularProgress
import com.mvvm.component.ext.dialogToast
import com.mvvm.component.ext.observerEvent
import com.mvvm.component.view.BaseBindingActivity
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityPasswrodModifyBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PasswordModifyActivity : BaseBindingActivity<ActivityPasswrodModifyBinding>() {

    private val viewModel by viewModel<PasswordModifyVm>()

    override val layoutId: Int
        get() = R.layout.activity_passwrod_modify

    override fun initViewAndData(binding: ActivityPasswrodModifyBinding) {
        binding.vm = viewModel.apply {
            dialogToast(this)
            dialogCircularProgress(this)
        }
    }

    override fun observerViewModelEvent() {
        observerEvent(viewModel.modifyPasswordEvent) {

        }
    }
}