package com.mvvm.hayate.ui.login

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.ext.onPropertyChanged
import com.mvvm.component.utils.SpUtils
import com.mvvm.component.view.BaseVm
import com.mvvm.hayate.ProfileManager
import com.mvvm.hayate.R
import com.mvvm.hayate.model.login.LoginReq

/**
 * 登入界面ViewModel
 */
class LoginVm : BaseVm() {

    var avatarPath = ObservableField("")
    var avatarErrorRes = ObservableInt(R.drawable.ic_avatar)
    var username = ObservableField("")
    var password = ObservableField("")
    var loginEnabled = ObservableBoolean(false)

    var onLoginEvent = MutableLiveData<LiveDataEvent<LoginReq>>()

    init {
        val lastUsername = SpUtils.getString(ProfileManager.USERNAME_LAST)
        username.set(lastUsername)
        setLoginEnabled()
    }

    private fun setLoginEnabled() {
        username.onPropertyChanged { _, _ ->
            loginEnabled.set(!username.get().toString().isBlank() && !password.get().toString().isBlank())
        }
        password.onPropertyChanged { _, _ ->
            loginEnabled.set(!username.get().toString().isBlank() && !password.get().toString().isBlank())
        }
    }

    /**
     * 点击事件 -> 登录
     */
    var onLoginClick = View.OnClickListener {
        val req = LoginReq(username.get().toString(), password.get().toString())
        onLoginEvent.value = LiveDataEvent(req)
    }

    /**
     * 点击事件 -> 注册用户
     */
    var onRegisterClick = View.OnClickListener {
        //        startActivity<RegisterActivity>()
    }

    /**
     * 点击事件 -> 忘记密码
     */
    var onPasswordForgetClick = View.OnClickListener {
        //        startActivity<PasswordForgetActivity>()
    }
}