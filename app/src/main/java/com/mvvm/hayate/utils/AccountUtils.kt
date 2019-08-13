package com.mvvm.hayate.utils

import com.mvvm.component.BaseApplication.Companion.context
import com.mvvm.hayate.R

object AccountUtils {

    /**
     * 验证密码合法性
     */
    fun checkPassword(password: String): String {
        return if (6 > password.length || 20 < password.length) context.getString(R.string.password_input_correct) else ""
    }

    /**
     * 验证密码合法性
     */
    fun checkConfirmPassword(newPassword: String, cfmPassword: String): String {
        return if (newPassword != cfmPassword) context.getString(R.string.password_confirm_error) else ""
    }
}