package com.mvvm.component.uc.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.R
import kotlinx.android.synthetic.main.include_material_dialog_bottom.view.*
import kotlinx.android.synthetic.main.include_material_dialog_top.view.*
import kotlinx.android.synthetic.main.item_material_dialog_list_multichoice.view.*
import kotlinx.android.synthetic.main.material_dialog_list_choice.view.*

class MaterialDialogListMultiChoice(context: Context) : MaterialDialogBase(context) {

    private var multiChoiceAdapter: MultiChoiceAdapter? = null
    private var selection: MultiChoiceListener = null

    init {
        LayoutInflater.from(context).inflate(R.layout.material_dialog_list_choice, this)
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

    fun title(@StringRes res: Int): MaterialDialogListMultiChoice {
        tvTitle.text = context.getString(res)
        return this
    }

    fun listItemsMultiChoice(@ArrayRes res: Int, initialSelection: IntArray = IntArray(0), selection: MultiChoiceListener = null): MaterialDialogListMultiChoice {
        this.selection = selection
        with(recyclerView) {
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_material_dialog_list_multichoice, parent, false)
            return MultiChoiceViewHolder(view)
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

internal class MultiChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var tvTitle: TextView = itemView.tvItemTitle
    var chkItem: AppCompatCheckBox = itemView.chkItem
    var viewItemContent: View = itemView.viewItemContent
}

internal typealias MultiChoiceListener = ((dialog: MaterialDialogListMultiChoice, indices: IntArray, items: List<String>) -> Unit)?
