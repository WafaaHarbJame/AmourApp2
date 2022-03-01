package com.ramez.shopp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramez.shopp.Models.CategoryModel
import com.ramez.shopp.R
import com.ramez.shopp.classes.GlobalData.GlideImg
import com.ramez.shopp.databinding.RowNewCategoryBinding

internal class CategoryNewAdapter(
    private val context: Context?,
    private var categoryDMS: List<CategoryModel>?,
    limit: Int,
    private val onItemClick: OnItemClick,
    isHoriz: Boolean
) :
        RecyclerView.Adapter<CategoryNewAdapter.Holder>() {
    private var limit = 6
    private val isHoriz: Boolean
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView: RowNewCategoryBinding =
            RowNewCategoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val categoryModel = categoryDMS?.get(position)
        try {
            GlideImg(
                context, categoryModel?.newCat, R.drawable.holder_image, holder.binding.ivCatImage
            )
            val lln: RecyclerView.LayoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
            holder.binding.subCatRecycler.layoutManager = lln
            val subCategoryNewAdapter = SubCategoryNewAdapter(
                context, categoryModel?.childCat)


            holder.binding.subCatRecycler.adapter = subCategoryNewAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.binding.container.setOnClickListener { v ->
            onItemClick.onItemClicked(
                position,
                categoryModel
            )
        }
    }


    override fun getItemCount(): Int {
        return if (limit != 0) Math.min(categoryDMS?.size ?: 0, limit) else categoryDMS?.size ?: 0
    }

    interface OnItemClick {
        fun onItemClicked(position: Int, categoryModel: CategoryModel?)
    }

    internal class Holder(view: RowNewCategoryBinding) : RecyclerView.ViewHolder(view.getRoot()) {
        var binding: RowNewCategoryBinding = view

    }


    init {
        this.limit = limit
        this.isHoriz = isHoriz
    }
}
