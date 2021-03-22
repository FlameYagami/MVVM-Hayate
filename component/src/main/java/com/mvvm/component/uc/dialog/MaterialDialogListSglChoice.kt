package com.mvvm.component.uc.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.databinding.*

class MaterialDialogListSglChoice(context: Context) : MaterialDialogBase(context) {

	private var sglChoiceAdapter: SglChoiceAdapter? = null
	private var selection: SglChoiceListener = null

	private var _binding: MaterialDialogListChoiceBinding? = null
	private var _topBinding: IncludeMaterialDialogTopBinding? = null
	private var _bottomBinding: IncludeMaterialDialogBottomBinding? = null
	private val binding get() = _binding!!
	private val topBinding get() = _topBinding!!
	private val bottomBinding get() = _bottomBinding!!

	init {
		_binding = MaterialDialogListChoiceBinding.inflate(LayoutInflater.from(context), this)
		_topBinding = IncludeMaterialDialogTopBinding.bind(binding.root)
        _bottomBinding = IncludeMaterialDialogBottomBinding.bind(binding.root)

		with(bottomBinding) {
			btnCancel.setOnClickListener {
				dismiss()
			}
			btnConfirm.setOnClickListener {
				dismiss()
				sglChoiceAdapter?.apply {
					selection?.invoke(this@MaterialDialogListSglChoice, currentSelect, getSelectedItem())
				}
			}
		}
	}

	fun title(@StringRes res: Int): MaterialDialogListSglChoice {
		topBinding.tvTitle.text = context.getString(res)
		return this
	}

	fun listItems(@ArrayRes res: Int, initialSelect: Int = -1, selection: SglChoiceListener = null): MaterialDialogListSglChoice {
		this.selection = selection
		with(binding.recyclerView) {
			layoutManager = LinearLayoutManager(context)
			adapter = SglChoiceAdapter(context.resources.getStringArray(res).toMutableList(), initialSelect).apply {
				sglChoiceAdapter = this
			}
		}
		return this
	}

	fun show(block: MaterialDialogListSglChoice.() -> Unit) {
		this.block()
		super.show()
	}

	inner class SglChoiceAdapter constructor(
        var list: MutableList<String>,
        initialSelect: Int
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

		var currentSelect = initialSelect
			set(value) {
				val previousSelection = field
				field = value
				if (-1 != previousSelection) {
					notifyItemChanged(previousSelection)
				}
				if (-1 != value) {
					notifyItemChanged(value)
				}
			}

		override fun getItemCount() = list.size

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
			val itemBinding = ItemMaterialDialogListSglchoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
			return SglChoiceViewHolder(itemBinding)
		}

		override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
			with((holder as SglChoiceViewHolder)) {
				chkItem.isChecked = currentSelect == position
				tvTitle.text = list[position]
				viewItemContent.setOnClickListener {
					itemClicked(position)
				}
			}
		}

		private fun itemClicked(index: Int) {
			this.currentSelect = index
		}

		fun getSelectedItem(): String {
			return list[currentSelect]
		}
	}
}

internal class SglChoiceViewHolder(itemBinding: ItemMaterialDialogListSglchoiceBinding) : RecyclerView.ViewHolder(itemBinding.root) {
	val tvTitle = itemBinding.tvItemTitle
	val chkItem = itemBinding.chkItem
	val viewItemContent = itemBinding.viewItemContent
}

internal typealias SglChoiceListener = ((dialog: MaterialDialogListSglChoice, index: Int, item: String) -> Unit)?
