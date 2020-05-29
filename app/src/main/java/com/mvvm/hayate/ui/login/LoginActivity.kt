package com.mvvm.hayate.ui.login

import com.mvvm.component.ext.dialogCircularProgress
import com.mvvm.component.ext.dialogToast
import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.ext.startActivity
import com.mvvm.component.view.BaseBindingActivity
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityLoginBinding

class LoginActivity : BaseBindingActivity<ActivityLoginBinding>() {

    private val viewModel by obtainViewModel<LoginVm>()

    override val layoutId: Int
        get() = R.layout.activity_login

    override fun initViewAndData(binding: ActivityLoginBinding) {
        binding.vm = viewModel.apply {
            dialogToast(this)
            dialogCircularProgress(this)
            startActivity(this)
        }
    }
}