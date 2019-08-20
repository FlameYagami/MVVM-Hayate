package com.mvvm.hayate.ui.main

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.android.material.appbar.AppBarLayout
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.view.BaseVm
import com.mvvm.hayate.ProfileManager
import com.mvvm.hayate.ui.profile.FeedbackActivity
import com.mvvm.hayate.ui.profile.ProfileDetailActivity
import com.mvvm.hayate.utils.SystemUtils
import kotlin.math.abs

class ProfileVm : BaseVm() {

    var nickname = ObservableField("")
    var username = ObservableField("")
    var versionName = ObservableField("")
    var titleEnabled = ObservableBoolean(false)

    val checkAppUpdateEvent = MutableLiveData<LiveDataEvent<Unit>>()

    init {
        username.set(ProfileManager.username ?: "")
        versionName.set(SystemUtils.versionName)
        updateNickname()
    }

    var onOffsetListener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        when {
            verticalOffset == 0 -> {
                titleEnabled.set(false) // 展开状态
            }
            abs(verticalOffset) >= appBarLayout.totalScrollRange -> {
                titleEnabled.set(true) // 折叠状态
            }
            else -> {
                titleEnabled.set(false) // 中间状态
            }
        }
    }

    fun updateNickname() {
        nickname.set(ProfileManager.nickname ?: "")
    }

    /**
     * 点击事件 -> 设置
     */
    var onSettingClick = View.OnClickListener {
        //        startActivity<SettingActivity>()
    }

    /**
     * 点击事件 -> 个人信息
     */
    var onProfileClick = View.OnClickListener {
        startActivity<ProfileDetailActivity>()
    }

    /**
     * 点击事件 -> 反馈
     */
    var onFeedbackClick = View.OnClickListener {
        startActivity<FeedbackActivity>()
    }

    /**
     * 点击事件 -> 版本升级
     */
    var onVersionUpdateClick = View.OnClickListener {
        checkAppUpdateEvent.value = LiveDataEvent(Unit)
    }
}