package com.mvvm.hayate

import com.mvvm.component.utils.JsonUtils
import com.mvvm.component.utils.SpUtils
import com.mvvm.hayate.model.login.LoginResp

class ProfileManager {

    var loginResp: LoginResp? = null

    init {
        loginResp = JsonUtils.deserializer<LoginResp>(SpUtils.getString(PROFILE_INFO))
    }

    companion object {

        const val PROFILE_INFO = "PROFILE_INFO"
        const val USERNAME_LAST = "USERNAME_LAST"

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            ProfileManager()
        }

        val userId get() = instance.loginResp?.userId.toString()

        val username get() = instance.loginResp?.username

        val nickname get() = instance.loginResp?.nickname

        fun saveNickname(nickname: String) {
            instance.loginResp?.apply {
                this.nickname = nickname
                saveProfile()
            }
        }

        private fun saveProfile() {
            instance.loginResp?.apply { SpUtils.putString(PROFILE_INFO, JsonUtils.serializer(this)) }
        }

        /**
         * 登录需要保存的数据
         */
        fun login(loginResp: LoginResp) {
            instance.loginResp = loginResp
            SpUtils.putString(USERNAME_LAST, loginResp.username.toString())

        }

        /**
         * 退出需要清理的数据
         */
        fun logout() {
            instance.loginResp = null
            SpUtils.remove(PROFILE_INFO)//清除用户信息
        }
    }
}