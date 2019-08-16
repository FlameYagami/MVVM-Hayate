package com.mvvm.hayate.view.login

import com.mvvm.component.ext.*
import com.mvvm.component.view.base.BaseBindingActivity
import com.mvvm.hayate.PathManager
import com.mvvm.hayate.ProfileManager
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityLoginBinding
import com.mvvm.hayate.model.login.LoginResp
import com.mvvm.hayate.view.main.MainActivity
import com.mvvm.hayate.vm.login.LoginVm
import io.reactivex.Observable

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
            Observable.create<LoginResp> { subscriber ->
                // 做一个2s的延迟模拟网络请求
                val resp = LoginResp(4985216, "394198188@qq.com", "Flame", "", PathManager.profileIconPath)
                delayThread(2000) { subscriber.onNext(resp) }
            }
                .applyDialogCircleProgress(this, viewModel)
                .applyIoMain(this)
                .subscribe {
                    ProfileManager.login(it)
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                }
        }
    }
}