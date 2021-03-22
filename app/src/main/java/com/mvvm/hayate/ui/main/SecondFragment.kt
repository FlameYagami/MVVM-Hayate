package com.mvvm.hayate.ui.main

import com.mvvm.component.ext.observerEvent
import com.mvvm.component.ext.obtainViewModel
import com.mvvm.component.uc.dialog.MaterialDialogEdit
import com.mvvm.component.uc.dialog.MaterialDialogList
import com.mvvm.component.uc.dialog.MaterialDialogText
import com.mvvm.component.view.BaseBindingFragment
import com.mvvm.hayate.R
import com.mvvm.hayate.databinding.FragmentScendBinding

class SecondFragment : BaseBindingFragment<FragmentScendBinding>() {

	private val viewModel by obtainViewModel<SecondVm>()

	override val layoutId: Int
		get() = R.layout.fragment_scend

	override fun initView(binding: FragmentScendBinding) {
		binding.vm = viewModel.apply {

		}
	}

	override fun initData() {

	}

	override fun observerViewModelEvent() {
		observerEvent(viewModel.menuEvent) {
			MaterialDialogList(requireContext())
				.title(R.string.main_second)
				.listItems(it) { _, _, text ->
					when (text) {
                        getString(R.string.dialog_edit) -> showDialogEdit()
                        getString(R.string.dialog_text) -> showDialogText()
					}
				}.show()
		}
	}

	private fun showDialogEdit() {
		MaterialDialogEdit(requireContext())
			.title(R.string.dialog_edit)
			.message(R.string.dialog_edit_message)
			.messageHint(R.string.dialog_edit_hint)
			.messageLength(32)
			.positiveButton { _, _ -> }
			.show()
	}

	private fun showDialogText() {
		MaterialDialogText(requireContext())
			.title(R.string.dialog_text)
			.message(R.string.dialog_text_message)
			.positiveButton { }
			.show()
	}
}
