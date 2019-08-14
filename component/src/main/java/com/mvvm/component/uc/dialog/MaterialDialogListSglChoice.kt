package com.mvvm.component.uc.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.R
import kotlinx.android.synthetic.main.include_material_dialog_bottom.view.*
import kotlinx.android.synthetic.main.include_material_dialog_top.view.*
import kotlinx.android.synthetic.main.item_material_dialog_list_sglchoice.view.*
import kotlinx.android.synthetic.main.material_dialog_list_choice.view.*

class MaterialDialogListSglChoice(context: Context) : MaterialDialogBase(context) {

    private var sglChoiceAdapter: SglChoiceAdapter? = null
    private var selection: SglChoiceListener = null

    init {
        LayoutInflater.from(context).inflate(R.layout.material_dialog_list_choice, this)
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

    fun title(@StringRes res: Int): MaterialDialogListSglChoice {
        tvTitle.text = context.getString(res)
        return this
    }

    fun listItems(@ArrayRes res: Int, initialSelect: Int = -1, selection: SglChoiceListener = null): MaterialDialogListSglChoice {
        this.selection = selection
        with(recyclerView) {
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_material_dialog_list_sglchoice, parent, false)
            return SglChoiceViewHolder(view)
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

internal class SglChoiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var tvTitle: TextView = itemView.tvItemTitle
    var chkItem: AppCompatRadioButton = itemView.chkItem
    var viewItemContent: View = itemView.viewItemContent
}

internal typealias SglChoiceListener = ((dialog: MaterialDialogListSglChoice, index: Int, item: String) -> Unit)?
