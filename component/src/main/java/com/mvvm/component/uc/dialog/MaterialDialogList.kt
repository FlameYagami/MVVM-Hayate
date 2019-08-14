package com.mvvm.component.uc.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.R
import kotlinx.android.synthetic.main.include_material_dialog_top.view.*
import kotlinx.android.synthetic.main.item_material_dialog_list.view.*
import kotlinx.android.synthetic.main.material_dialog_list.view.*

class MaterialDialogList(context: Context) : MaterialDialogBase(context) {

    init {
        LayoutInflater.from(context).inflate(R.layout.material_dialog_list, this)
    }

    fun title(@StringRes res: Int): MaterialDialogList {
        tvTitle.text = context.getString(res)
        return this
    }

    fun listItems(res: List<String>, selection: ((dialog: MaterialDialogList, index: Int, text: String) -> Unit)? = null): MaterialDialogList {
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = ListAdapter(this@MaterialDialogList, res.toMutableList(), selection)
        }
        return this
    }

    fun listItems(@ArrayRes res: Int, selection: ((dialog: MaterialDialogList, index: Int, text: String) -> Unit)? = null): MaterialDialogList {
        return listItems(context.resources.getStringArray(res).toList(), selection)
    }

    fun show(block: MaterialDialogList.() -> Unit) {
        this.block()
        super.show()
    }

    inner class ListAdapter constructor(
        var materialDialog: MaterialDialogList,
        var list: MutableList<String>,
        private var itemListener: ItemListener = null
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemCount() = list.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_material_dialog_list, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            with((holder as ViewHolder)) {
                tvTitle.text = list[position]
                tvTitle.setOnClickListener {
                    itemListener?.invoke(materialDialog, holder.adapterPosition, list[position])
                    if (materialDialog.autoDismissEnabled) {
                        materialDialog.dismiss()
                    }
                }
            }
        }
    }
}

internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var tvTitle: TextView = itemView.tvItemTitle
}

internal typealias ItemListener = ((dialog: MaterialDialogList, index: Int, text: String) -> Unit)?
