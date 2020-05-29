package com.mvvm.hayate.ui.main

import android.content.Intent
import com.mvvm.component.ext.dialogCircularProgress
import com.mvvm.component.ext.dialogToast
import com.mvvm.component.ext.observerEvent
import com.mvvm.component.ext.startActivity
import com.mvvm.component.uc.dialog.MaterialDialogList
import com.mvvm.component.view.BaseBindingFragment
import com.mvvm.hayate.ProfileManager
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentProfileBinding
import com.mvvm.hayate.model.event.AvatarChangedEvent
import com.mvvm.hayate.model.event.NicknameChangedEvent
import com.mvvm.hayate.ui.profile.AvatarVm
import org.greenrobot.eventbus.Subscribe
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : BaseBindingFragment<FragmentProfileBinding>() {

    private val viewModel by viewModel<ProfileVm>()
    private val avatarViewModel by viewModel<AvatarVm>()

    override val layoutId: Int
        get() = R.layout.fragment_profile

    override fun initView(binding: FragmentProfileBinding) {
        binding.avatarVm = avatarViewModel.apply {
            dialogToast(this)
            dialogCircularProgress(this)
        }
        binding.vm = viewModel.apply {
            dialogToast(this)
            dialogCircularProgress(this)
            startActivity(this)
        }
    }

    override fun observerViewModelEvent() {
        observerEvent(avatarViewModel.selectAvatarEvent) {
            MaterialDialogList(context!!).show {
                title(R.string.profile_image_type)
                listItems(R.array.profile_image_type_operation) { _, index, _ ->
                    if (0 == index) avatarViewModel.takePhoto()
                    else avatarViewModel.chooseLocal()
                }
            }
        }
        observerEvent(avatarViewModel.uploadAvatarEvent) {
            ProfileManager.saveAvatar(it)
            avatarViewModel.updateAvatar()
        }
        observerEvent(avatarViewModel.startActivityForResultEvent) {
            startActivityForResult(it.first, it.second)
        }
        observerEvent(viewModel.checkAppUpdateEvent) {
            viewModel.dialogToastWarning(true, getString(R.string.version_latest))
        }
    }

    override fun initData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        avatarViewModel.handleAvatarRequestCode(requestCode, data)
    }

    @Subscribe
    fun onAvatarChangedEvent(event: AvatarChangedEvent) {
        avatarViewModel.updateAvatar()
    }

    @Subscribe
    fun onNicknameChangedEvent(event: NicknameChangedEvent) {
        viewModel.updateNickname()
    }

    override fun applyEventBus(): Boolean {
        return true
    }
}
