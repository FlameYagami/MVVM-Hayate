package com.mvvm.hayate.vm.profile

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.vm.BaseVm
import com.mvvm.hayate.view.profile.PasswordModifyActivity

open class ProfileDetailVm : BaseVm() {

    var nickname = ObservableField("")

    val nicknameEvent = MutableLiveData<LiveDataEvent<String>>()
    val logoutEvent = MutableLiveData<LiveDataEvent<Unit>>()

    init {
        setNickname()
    }

    fun setNickname() {

    }

    /**
     * 点击事件 -> 密码修改
     */
    var onPasswordModifyClick = View.OnClickListener {
        startActivity<PasswordModifyActivity>()
    }

    /**
     * 点击事件 -> 昵称修改
     */
    var onNicknameClick = View.OnClickListener {
        nicknameEvent.value = LiveDataEvent(nickname.get().toString())
    }

    /**
     * 点击事件 -> 退出登录
     */
    var onLogout = View.OnClickListener {
        logoutEvent.value = LiveDataEvent(Unit)
    }
}
