package com.mvvm.hayate.ui.profile

import android.content.res.Configuration
import com.mvvm.component.ext.*
import com.mvvm.component.uc.dialog.MaterialDialogEdit
import com.mvvm.component.uc.dialog.MaterialDialogList
import com.mvvm.component.uc.dialog.MaterialDialogText
import com.mvvm.component.view.BaseBindingActivity
import com.mvvm.hayate.R
import com.mvvm.hayate.contracts.AvatarCameraContracts
import com.mvvm.hayate.contracts.AvatarCropContracts
import com.mvvm.hayate.contracts.AvatarLocalContracts
import com.mvvm.hayate.contracts.ContractsConst
import com.mvvm.hayate.databinding.ActivityProfileDetailBinding
import com.mvvm.hayate.manager.ProfileManager
import com.mvvm.hayate.ui.login.LoginActivity

class ProfileDetailActivity : BaseBindingActivity<ActivityProfileDetailBinding>() {

	private val viewModel by obtainViewModel<ProfileDetailVm>()
	private val avatarViewModel by obtainViewModel<AvatarVm>()

	private val avatarLocalContracts = registerForActivityResult(AvatarLocalContracts()) {
		avatarViewModel.handleAvatarActivityResult(it)
	}

	private val avatarCameraContracts = registerForActivityResult(AvatarCameraContracts()) {
		avatarViewModel.handleAvatarActivityResult(it)
	}

	private val avatarCropContracts = registerForActivityResult(AvatarCropContracts()) {
		avatarViewModel.handleAvatarActivityResult(it)
	}

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
		super.observerViewModelEvent()
		observerEvent(avatarViewModel.selectAvatarEvent) {
			MaterialDialogList(this).show {
				title(R.string.profile_image_type)
				listItems(R.array.profile_image_type_operation) { _, index, _ ->
					if (0 == index) avatarViewModel.takePhoto()
					else avatarViewModel.pickImage()
				}
			}
		}
		observerEvent(avatarViewModel.uploadAvatarEvent) {
			ProfileManager.saveAvatar(it)
			avatarViewModel.updateAvatar()
		}
		observerEvent(avatarViewModel.registerActivityResultEvent) {
			when (it.second) {
                ContractsConst.AVATAR_CAMERA -> avatarCameraContracts.launch(it.first)
                ContractsConst.AVATAR_CROP -> avatarCropContracts.launch(it.first)
                ContractsConst.AVATAR_LOCAL -> avatarLocalContracts.launch(it.first)
			}
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

	/**
	 * 三星手机拍照问题，与AndroidManifest.xml的属性配合使用
	 */
	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
	}
}