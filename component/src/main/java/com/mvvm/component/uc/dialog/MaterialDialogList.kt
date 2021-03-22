package com.mvvm.component.uc.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mvvm.component.databinding.IncludeMaterialDialogTopBinding
import com.mvvm.component.databinding.ItemMaterialDialogListBinding
import com.mvvm.component.databinding.MaterialDialogListBinding

class MaterialDialogList(context: Context) : MaterialDialogBase(context) {

    private var _binding: MaterialDialogListBinding? = null
    private var _topBinding: IncludeMaterialDialogTopBinding? = null
    private val binding get() = _binding!!
    private val topBinding get() = _topBinding!!

    init {
        _binding = MaterialDialogListBinding.inflate(LayoutInflater.from(context), this)
        _topBinding = IncludeMaterialDialogTopBinding.bind(binding.root)
    }

    fun title(@StringRes res: Int): MaterialDialogList {
        topBinding.tvTitle.text = context.getString(res)
        return this
    }

    fun listItems(res: List<String>, selection: ((dialog: MaterialDialogList, index: Int, text: String) -> Unit)? = null): MaterialDialogList {
        with(binding.recyclerView) {
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
            val itemBinding = ItemMaterialDialogListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(itemBinding)
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

internal class ViewHolder(itemBinding: ItemMaterialDialogListBinding) : RecyclerView.ViewHolder(itemBinding.root) {
    var tvTitle = itemBinding.tvItemTitle
}

internal typealias ItemListener = ((dialog: MaterialDialogList, index: Int, text: String) -> Unit)?
