package com.mvvm.hayate.vm.main

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.android.material.appbar.AppBarLayout
import com.mvvm.component.LiveDataEvent
import com.mvvm.component.vm.BaseVm
import com.mvvm.hayate.utils.SystemUtils
import com.mvvm.hayate.view.profile.FeedbackActivity
import com.mvvm.hayate.view.profile.ProfileDetailActivity
import kotlin.math.abs

class ProfileVm : BaseVm() {

    var nickname = ObservableField("")
    var username = ObservableField("")
    var versionName = ObservableField("")
    var titleEnabled = ObservableBoolean(false)

    val checkAppUpdateEvent = MutableLiveData<LiveDataEvent<Unit>>()

    init {
        versionName.set(SystemUtils.versionName)
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