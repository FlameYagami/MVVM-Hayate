package com.mvvm.hayate.ui.login

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.mvvm.component.ext.onPropertyChanged
import com.mvvm.component.utils.SpUtils
import com.mvvm.component.view.BaseVm
import com.mvvm.hayate.R
import com.mvvm.hayate.manager.ProfileManager
import com.mvvm.hayate.model.login.LoginResp
import com.mvvm.hayate.model.repository.LoginRepository
import com.mvvm.hayate.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

/**
 * 登入界面ViewModel
 */
class LoginVm(val loginRepository: LoginRepository) : BaseVm() {

	var avatarPath = ObservableField("")
	var avatarErrorRes = ObservableInt(R.drawable.ic_avatar)
	var username = ObservableField("15870682997")
	var password = ObservableField("123456")
	var loginEnabled = ObservableBoolean(false)

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
		launchUI {
			launchFlowDialog {
				delay(1000)
			}.collect {
				SpUtils.putString(ProfileManager.USERNAME_LAST, username.get().toString())
				ProfileManager.login(LoginResp(userId = 1, username = "Flame"))
				startActivity<MainActivity>()
			}
		}
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