package com.mvvm.hayate.ui.login

import com.mvvm.component.ext.dialogCircularProgress
import com.mvvm.component.ext.dialogToast
import com.mvvm.component.ext.startActivity
import com.mvvm.component.view.BaseBindingActivity
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityLoginBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseBindingActivity<ActivityLoginBinding>() {

    private val viewModel by viewModel<LoginVm>()

    override val layoutId: Int
        get() = R.layout.activity_login

    override fun initViewAndData(binding: ActivityLoginBinding) {
        binding.vm = viewModel.apply {
            dialogToast(this)
            dialogCircularProgress(this)
            startActivity(this)
        }
    }

    override fun observerViewModelEvent() {

    }
}