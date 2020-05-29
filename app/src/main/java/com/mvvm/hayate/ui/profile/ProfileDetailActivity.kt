package com.mvvm.hayate.ui.profile

import android.content.Intent
import android.content.res.Configuration
import com.mvvm.component.ext.*
import com.mvvm.component.uc.dialog.MaterialDialogEdit
import com.mvvm.component.uc.dialog.MaterialDialogList
import com.mvvm.component.uc.dialog.MaterialDialogText
import com.mvvm.component.view.BaseBindingActivity
import com.mvvm.hayate.ProfileManager
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.ActivityProfileDetailBinding
import com.mvvm.hayate.ui.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileDetailActivity : BaseBindingActivity<ActivityProfileDetailBinding>() {

    private val viewModel by viewModel<ProfileDetailVm>()
    private val avatarViewModel by viewModel<AvatarVm>()

    override val layoutId: Int
        get() = R.layout.activity_profile_detail

    override fun initViewAndData(binding: ActivityProfileDetailBinding) {
        binding.avatarVm = avatarViewModel.apply {
            dialogToast(this)
            dialogCircularProgress(this)
            startActivity(this)
        }
        binding.vm = viewModel.apply {
            dialogToast(this)
            dialogCircularProgress(this)
            startActivity(this)
        }
    }

    override fun observerViewModelEvent() {
        observerEvent(avatarViewModel.selectAvatarEvent) {
            MaterialDialogList(this).show {
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
        observerEvent(viewModel.nicknameEvent) {
            MaterialDialogEdit(this).show {
                title(R.string.nickname)
                message(it)
                positiveButton { _, message ->
                    ProfileManager.saveNickname(message)
                    viewModel.updateNickname()
                }
            }
        }
        observerEvent(viewModel.sexEvent) {
            MaterialDialogList(this).show {
                title(R.string.sex)
                listItems(R.array.profile_sex_operation) { _, _, text ->
                    ProfileManager.saveSex(text)
                    viewModel.updateSex()
                }
            }
        }
        observerEvent(viewModel.birthdayEvent) {

        }
        observerEvent(viewModel.logoutEvent) {
            MaterialDialogText(this).show {
                title(R.string.logout)
                message(R.string.logout_message)
                positiveButton {
                    ProfileManager.logout()
                    startActivity(intentFor<LoginActivity>().newTask().clearTask())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        avatarViewModel.handleAvatarRequestCode(requestCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 三星手机拍照问题，与AndroidManifest.xml的属性配合使用
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}