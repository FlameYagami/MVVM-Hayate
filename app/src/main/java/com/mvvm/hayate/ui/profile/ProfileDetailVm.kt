package com.mvvm.hayate.ui.profile

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.mvvm.component.BaseApplication
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.view.BaseVm
import com.mvvm.hayate.ProfileManager
import com.mvvm.hayate.R

open class ProfileDetailVm : BaseVm() {

    var userId = ObservableField("")
    var nickname = ObservableField("")
    var sex = ObservableField("")
    var birthday = ObservableField("")

    val nicknameEvent = MutableLiveData<LiveDataEvent<String>>()
    val sexEvent = MutableLiveData<LiveDataEvent<String>>()
    val birthdayEvent = MutableLiveData<LiveDataEvent<String>>()
    val logoutEvent = MutableLiveData<LiveDataEvent<Unit>>()

    init {
        userId.set(ProfileManager.userId)
        updateNickname()
        updateSex()
        updateBirthday()
    }

    fun updateNickname() {
        nickname.set(ProfileManager.nickname ?: "")
    }

    fun updateSex() {
        sex.set(ProfileManager.sex ?: BaseApplication.context.getString(R.string.secret))
    }

    fun updateBirthday() {
        birthday.set(ProfileManager.birthday ?: BaseApplication.context.getString(R.string.secret))
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
     * 点击事件 -> 性别修改
     */
    var onSexClick = View.OnClickListener {
        sexEvent.value = LiveDataEvent(sex.get().toString())
    }

    /**
     * 点击事件 -> 生日修改
     */
    var onBirthdayClick = View.OnClickListener {
        birthdayEvent.value = LiveDataEvent(birthday.get().toString())
    }

    /**
     * 点击事件 -> 退出登录
     */
    var onLogout = View.OnClickListener {
        logoutEvent.value = LiveDataEvent(Unit)
    }
}
