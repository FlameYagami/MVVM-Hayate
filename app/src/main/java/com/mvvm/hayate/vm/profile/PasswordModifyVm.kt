package com.mvvm.hayate.vm.profile

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.ext.onPropertyChanged
import com.mvvm.component.vm.BaseVm
import com.mvvm.hayate.model.profile.PasswordModify
import com.mvvm.hayate.utils.AccountUtils

class PasswordModifyVm : BaseVm() {

    var model: PasswordModify = PasswordModify("")

    var passwordOldErrorEnabled = ObservableBoolean(false)
    var passwordNewErrorEnabled = ObservableBoolean(false)
    var passwordCfmErrorEnabled = ObservableBoolean(false)
    var passwordOldError = ObservableField("")
    var passwordNewError = ObservableField("")
    var passwordCfmError = ObservableField("")
    var completeEnabled = ObservableBoolean(false)

    var modifyPasswordEvent = MutableLiveData<LiveDataEvent<Unit>>()

    init {
        setPasswordError()
    }

    private fun setPasswordError() {
        with(model) {
            oldPassword.onPropertyChanged { _, _ ->
                val errorHint = AccountUtils.checkPassword(oldPassword.get().toString())
                passwordOldErrorEnabled.set(errorHint.isNotBlank())
                passwordOldError.set(errorHint)
                completeEnabled.set(isAllPasswordEnabled())
            }
            newPassword.onPropertyChanged { _, _ ->
                if (cfmPassword.get().toString().isBlank()) {
                    AccountUtils.checkPassword(newPassword.get().toString()).apply {
                        passwordNewErrorEnabled.set(this.isNotBlank())
                        passwordNewError.set(this)
                    }
                } else {
                    AccountUtils.checkConfirmPassword(newPassword.get().toString(), cfmPassword.get().toString())
                        .apply {
                            passwordCfmErrorEnabled.set(this.isNotBlank())
                            passwordCfmError.set(this)
                        }
                }
                completeEnabled.set(isAllPasswordEnabled())
            }
            cfmPassword.onPropertyChanged { _, _ ->
                val errorHint =
                    AccountUtils.checkConfirmPassword(newPassword.get().toString(), cfmPassword.get().toString())
                passwordCfmErrorEnabled.set(errorHint.isNotBlank())
                passwordCfmError.set(errorHint)
                completeEnabled.set(isAllPasswordEnabled())
            }
        }
    }

    private fun isAllPasswordEnabled(): Boolean {
        return model.let {
            !passwordOldErrorEnabled.get() && !passwordNewErrorEnabled.get() && !passwordCfmErrorEnabled.get()
        }
    }

    /**
     * 点击事件 -> 完成
     */
    var onCompleteClick = View.OnClickListener {
        modifyPasswordEvent.value = LiveDataEvent(Unit)
    }
}
