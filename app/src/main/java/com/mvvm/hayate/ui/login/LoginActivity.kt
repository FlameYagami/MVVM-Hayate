package com.mvvm.hayate.ui.login

import com.mvvm.component.api.HttpCoroutine
import com.mvvm.component.ext.*
import com.mvvm.component.view.BaseBindingActivity
import com.mvvm.hayate.PathManager
import com.mvvm.hayate.ProfileManager
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityLoginBinding
import com.mvvm.hayate.model.login.LoginResp
import com.mvvm.hayate.ui.main.MainActivity
import kotlinx.coroutines.delay

class LoginActivity : BaseBindingActivity<ActivityLoginBinding>() {

    lateinit var viewModel: LoginVm

    override val layoutId: Int
        get() = R.layout.activity_login

    override fun initViewAndData(binding: ActivityLoginBinding) {
        binding.vm = obtainViewModel<LoginVm>().apply {
            viewModel = this
            dialogToast(this)
            dialogCircularProgress(this)
            startActivity(this)
        }
        observerEvent()
    }

    private fun observerEvent() {
        observerEvent(viewModel.onLoginEvent) {
            HttpCoroutine(this) { delay(5000) }
                .applyDialog(this, viewModel)
                .onJoin({
                    val httpResp = LoginResp(
                        4985216,
                        "394198188@qq.com",
                        "Flame",
                        "",
                        "",
                        "FlameYagami@gmail.com",
                        PathManager.avatarPath
                    )
                    ProfileManager.login(httpResp)
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                }, {

                })
        }
    }
}