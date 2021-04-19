package com.mvvm.hayate.ui.main

import com.mvvm.component.ext.*
import com.mvvm.component.uc.dialog.MaterialDialogList
import com.mvvm.component.view.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.contracts.AvatarCameraContracts
import com.mvvm.hayate.contracts.AvatarCropContracts
import com.mvvm.hayate.contracts.AvatarLocalContracts
import com.mvvm.hayate.contracts.ContractsConst
import com.mvvm.hayate.databinding.FragmentProfileBinding
import com.mvvm.hayate.manager.ProfileManager
import com.mvvm.hayate.model.event.AvatarChangedEvent
import com.mvvm.hayate.model.event.NicknameChangedEvent
import com.mvvm.hayate.ui.profile.AvatarVm
import org.greenrobot.eventbus.Subscribe

class ProfileFragment : BaseBindingFragment<FragmentProfileBinding>() {

	private val viewModel by obtainViewModel<ProfileVm>()
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
			MaterialDialogList(requireContext()).show {
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
		observerEvent(viewModel.checkAppUpdateEvent) {
			viewModel.dialogToastWarning(true, getString(R.string.version_latest))
		}
	}

	override fun initData() {

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
