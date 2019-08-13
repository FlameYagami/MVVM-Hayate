package com.mvvm.hayate.view.profile

import android.content.Intent
import android.content.res.Configuration
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.mvvm.component.AppManager
import com.mvvm.component.ext.*
import com.mvvm.component.uc.dialog.DialogCircularProgressUtils
import com.mvvm.component.view.base.BaseBindingActivity
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityProfileDetailBinding
import com.mvvm.hayate.vm.main.AvatarVm
import com.mvvm.hayate.vm.profile.ProfileDetailVm

class ProfileDetailActivity : BaseBindingActivity<ActivityProfileDetailBinding>() {

    lateinit var viewModel: ProfileDetailVm
    lateinit var avatarViewModel: AvatarVm

    override val layoutId: Int
        get() = R.layout.activity_profile_detail

    override fun initViewAndData(binding: ActivityProfileDetailBinding) {
        binding.avatarVm = obtainViewModel<AvatarVm>().apply {
            avatarViewModel = this
            dialogToast(this)
            dialogCircularProgress(this)
            startActivity(this)
        }
        binding.vm = obtainViewModel<ProfileDetailVm>().apply {
            viewModel = this
            dialogToast(this)
            dialogCircularProgress(this)
            startActivity(this)
        }
        observerEvent()
    }

    private fun observerEvent() {
        observerEvent(avatarViewModel.selectProfileIconEvent) {
            MaterialDialog(this).show {
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
        observerEvent(viewModel.nicknameEvent) {

        }
        observerEvent(viewModel.logoutEvent) {

        }
    }

    private fun updateNickname(nickname: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        avatarViewModel.handleProfileIconRequestCode(requestCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 三星手机拍照问题，与AndroidManifest.xml的属性配合使用
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}