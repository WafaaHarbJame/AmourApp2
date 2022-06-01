package com.amour.shop.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.Models.ChildCat
import com.amour.shop.R
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData.GlideImg
import com.amour.shop.classes.MessageEvent
import com.amour.shop.databinding.RowCategoryBinding
import com.amour.shop.databinding.RowNewSubcatBinding
import org.greenrobot.eventbus.EventBus

internal class SubCategoryNewAdapter(
    private val context: Context?,
    private var categoryDMS: List<ChildCat>?
) :
        RecyclerView.Adapter<SubCategoryNewAdapter.Holder>() {
    private val limit = 6
    private val isHoriz = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView: RowNewSubcatBinding =
            RowNewSubcatBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val childCat = categoryDMS?.get(position)
        try {
            GlideImg(
                context, childCat?.image, R.drawable.holder_image, holder.binding.ivCatImage
            )
            holder.binding.categoryNameTv.text = childCat?.catName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.binding.container.setOnClickListener { v ->
            val bundle = Bundle()
//            bundle.putSerializable(Constants.CAT_LIST, categoryDMS)
            bundle.putInt(Constants.MAIN_CAT_ID, childCat?.parentId ?: 0)
            bundle.putInt(Constants.SUB_CAT_ID, childCat?.id?:0)
            bundle.putInt(Constants.position, position)
            bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
            EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
        }
    }


    override fun getItemCount(): Int {
        return if (limit != 0) Math.min(categoryDMS?.size ?: 0, limit) else categoryDMS?.size ?: 0
    }

    interface OnItemClick {
        fun onItemClicked(position: Int, childCat: ChildCat?)
    }

    internal class Holder(view: RowNewSubcatBinding) : RecyclerView.ViewHolder(view.getRoot()) {
        var binding: RowNewSubcatBinding = view

    }

    internal class HorizontalHolder(view: RowCategoryBinding) : RecyclerView.ViewHolder(view.getRoot()) {
        var binding: RowCategoryBinding = view

    }
}


