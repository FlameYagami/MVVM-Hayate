package com.mvvm.hayate.view.main

import android.annotation.SuppressLint
import android.content.Intent
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.mvvm.component.ext.*
import com.mvvm.component.view.base.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentProfileBinding
import com.mvvm.hayate.model.event.NicknameChangedEvent
import com.mvvm.hayate.model.event.ProfileIconChangedEvent
import com.mvvm.hayate.vm.main.AvatarVm
import com.mvvm.hayate.vm.main.ProfileVm
import org.greenrobot.eventbus.Subscribe

class ProfileFragment : BaseBindingFragment<FragmentProfileBinding>() {

    lateinit var viewModel: ProfileVm
    lateinit var avatarViewModel: AvatarVm

    override val layoutId: Int
        get() = R.layout.fragment_profile

    override fun initView(binding: FragmentProfileBinding) {
        binding.avatarVm = obtainViewModel<AvatarVm>().apply {
            avatarViewModel = this
            dialogToast(this)
            dialogCircularProgress(this)
        }
        binding.vm = obtainViewModel<ProfileVm>().apply {
            viewModel = this
            dialogToast(this)
            dialogCircularProgress(this)
            startActivity(this)
        }
        observerEvent()
    }

    private fun observerEvent() {
        observerEvent(avatarViewModel.selectProfileIconEvent) {
            MaterialDialog(context!!).show {
                title(R.string.profile_image_type)
                listItems(R.array.profile_image_type_operation) { _, index, _ ->
                    if (0 == index) avatarViewModel.takePhoto()
                    else avatarViewModel.chooseLocal()
                }
            }

        }
        observerEvent(avatarViewModel.uploadProfileIconEvent) {

        }
        observerEvent(avatarViewModel.startActivityForResultEvent) {
            startActivityForResult(it.first, it.second)
        }
        observerEvent(viewModel.checkAppUpdateEvent) {

        }
    }

    override fun initData(isViewVisible: Boolean) {
        if (isViewVisible && !isInitData) {
            isInitData = true
            // 延迟刷新
            initUserInfo()
        }
    }

    @SuppressLint("CheckResult")
    fun initUserInfo() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        avatarViewModel.handleProfileIconRequestCode(requestCode, data)
    }

    @Subscribe
    fun onProfileIconChangedEvent(event: ProfileIconChangedEvent) {
        avatarViewModel.setProfileIcon()
    }

    @Subscribe
    fun onNicknameChangedEvent(event: NicknameChangedEvent) {
//        viewModel.setNickname()
    }

    override fun applyEventBus(): Boolean {
        return true
    }
}
