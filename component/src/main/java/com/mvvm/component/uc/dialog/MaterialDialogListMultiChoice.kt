package com.mvvm.component.uc.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.databinding.*

class MaterialDialogListMultiChoice(context: Context) : MaterialDialogBase(context) {

	private var multiChoiceAdapter: MultiChoiceAdapter? = null
	private var selection: MultiChoiceListener = null

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
				multiChoiceAdapter?.apply {
					selection?.invoke(this@MaterialDialogListMultiChoice, currentSelection, getSelectedItems())
				}
			}
		}
	}

	fun title(@StringRes res: Int): MaterialDialogListMultiChoice {
		topBinding.tvTitle.text = context.getString(res)
		return this
	}

	fun listItemsMultiChoice(
        @ArrayRes res: Int,
        initialSelection: IntArray = IntArray(0),
        selection: MultiChoiceListener = null
    ): MaterialDialogListMultiChoice {
		this.selection = selection
		with(binding.recyclerView) {
			layoutManager = LinearLayoutManager(context)
			adapter = MultiChoiceAdapter(context.resources.getStringArray(res).toMutableList(), initialSelection).apply {
				multiChoiceAdapter = this
			}
		}
		return this
	}

	fun show(block: MaterialDialogListMultiChoice.() -> Unit) {
		this.block()
		super.show()
	}

	inner class MultiChoiceAdapter constructor(
        var list: MutableList<String>,
        initialSelection: IntArray
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

		var currentSelection: IntArray = initialSelection
			set(value) {
				val previousSelection = field
				field = value
				for (previous in previousSelection) {
					if (!value.contains(previous)) {
						// This value was unselected
						notifyItemChanged(previous)
					}
				}
				for (current in value) {
					if (!previousSelection.contains(current)) {
						// This value was selected
						notifyItemChanged(current)
					}
				}
			}

		override fun getItemCount() = list.size

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
			val itemBinding = ItemMaterialDialogListMultichoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
			return MultiChoiceViewHolder(itemBinding)
		}

		override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
			with((holder as MultiChoiceViewHolder)) {
				chkItem.isChecked = currentSelection.contains(position)
				tvTitle.text = list[position]
				viewItemContent.setOnClickListener {
					itemClicked(position)
				}
			}
		}

		private fun itemClicked(index: Int) {
			val newSelection = this.currentSelection.toMutableList()
			if (newSelection.contains(index)) {
				newSelection.remove(index)
			} else {
				newSelection.add(index)
			}
			this.currentSelection = newSelection.toIntArray()
		}

		fun getSelectedItems(): List<String> {
			val result = mutableListOf<String>()
			for (index in currentSelection) {
				result.add(list[index])
			}
			return result
		}
	}
}

internal class MultiChoiceViewHolder(itemView: ItemMaterialDialogListMultichoiceBinding) : RecyclerView.ViewHolder(itemView.root) {
	var tvTitle = itemView.tvItemTitle
	var chkItem = itemView.chkItem
	var viewItemContent = itemView.viewItemContent
}

internal typealias MultiChoiceListener = ((dialog: MaterialDialogListMultiChoice, indices: IntArray, items: List<String>) -> Unit)?
