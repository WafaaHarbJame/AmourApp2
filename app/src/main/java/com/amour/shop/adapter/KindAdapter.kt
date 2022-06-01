package com.amour.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.Models.CategoryModel
import com.amour.shop.Models.ChildCat
import com.amour.shop.Models.KindCategoryModel
import com.amour.shop.databinding.RowKindBinding
import kotlin.math.min


internal class KindAdapter(
    private val context: Context?,
    private var categoryDMS: List<KindCategoryModel>?,
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

//            GlobalData.GlideImg(
//                context, categoryModel?.image, R.drawable.holder_image, holder.binding.ivCatImage
//            )
            val childsList = mutableListOf<KindCategoryModel?>()
           /// childsList.add(categoryModel)
            categoryModel?.childCat?.let {
                childsList.addAll(it)
            }

            val spanCount = when (childsList.size) {
                0 -> 1
                1 -> 1
                2 -> 2
                3 -> 2
                4 -> 3
                5 -> 4
                6 -> 2
                else -> 2
            }
            val lln = GridLayoutManager(context, spanCount)
            holder.binding.subCatRecycler.isNestedScrollingEnabled = false
            holder.binding.subCatRecycler.setHasFixedSize(true)
            holder.binding.subCatRecycler.itemAnimator = null
//            holder.binding.subCatRecycler.isNestedScrollingEnabled = true
            holder.binding.subCatRecycler.layoutManager = lln

//            val lln: RecyclerView.LayoutManager =
//                object : LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {
//
//
//                    override fun canScrollHorizontally(): Boolean {
//                        return true
//                    }
//                }


            val subKindAdapter =
                KindSubAdapter(context, holder.binding.subCatRecycler, childsList, onKindClick)
            holder.binding.subCatRecycler.adapter = subKindAdapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
//        holder.binding.container.setOnClickListener { v ->
//            onKindClick.onKindClicked(
//                position,
//                categoryModel
//            )
//        }
    }


    override fun getItemCount(): Int {
        return if (limit != 0) min(categoryDMS?.size ?: 0, limit) else categoryDMS?.size ?: 0
    }

    interface OnKindClick {
        fun onKindClicked(position: Int, categoryModel: KindCategoryModel?)
    }

    inner class Holder(val binding: RowKindBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
//            itemView.setOnClickListener {
//                val categoryModel = categoryDMS?.get(bindingAdapterPosition)
//
//                onKindClick.onKindClicked(bindingAdapterPosition, categoryModel)
//            }

        }
    }


    init {
        this.limit = limit
        this.isHoriz = isHoriz
    }
}
