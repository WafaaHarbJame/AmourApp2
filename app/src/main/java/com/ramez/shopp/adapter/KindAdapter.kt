package com.ramez.shopp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramez.shopp.Models.CategoryModel
import com.ramez.shopp.R
import com.ramez.shopp.classes.GlobalData
import com.ramez.shopp.databinding.RowKindBinding

internal class KindAdapter(
    private val context: Context?,
    private var categoryDMS: List<CategoryModel>?,
    limit: Int,
    private val onKindClick: OnKindClick,
    isHoriz: Boolean
) :
        RecyclerView.Adapter<KindAdapter.Holder>() {
    private var limit = 6
    private val isHoriz: Boolean
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView: RowKindBinding =
            RowKindBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val categoryModel = categoryDMS?.get(position)
        try {
            holder.binding.categoryNameTv.text = categoryModel?.categoryName
            GlobalData.GlideImg(
                context, categoryModel?.image, R.drawable.holder_image, holder.binding.ivCatImage
            )
            val lln: RecyclerView.LayoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            holder.binding.subCatRecycler.layoutManager = lln
            val subKindAdapter = SubKindAdapter(
                context, categoryModel?.childCat
            )


            holder.binding.subCatRecycler.adapter = subKindAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.binding.container.setOnClickListener { v ->
            onKindClick.onKindClicked(
                position,
                categoryModel
            )
        }
    }


    override fun getItemCount(): Int {
        return if (limit != 0) Math.min(categoryDMS?.size ?: 0, limit) else categoryDMS?.size ?: 0
    }

    interface OnKindClick {
        fun onKindClicked(position: Int, categoryModel: CategoryModel?)
    }

    internal class Holder(view: RowKindBinding) : RecyclerView.ViewHolder(view.getRoot()) {
        var binding: RowKindBinding = view

    }


    init {
        this.limit = limit
        this.isHoriz = isHoriz
    }
}
