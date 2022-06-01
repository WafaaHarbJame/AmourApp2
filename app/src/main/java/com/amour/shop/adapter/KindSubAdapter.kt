package com.amour.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.Models.KindCategoryModel
import com.amour.shop.R
import com.amour.shop.classes.GlobalData.GlideImg
import com.amour.shop.databinding.RowSubkindBinding

internal class KindSubAdapter(
    private val context: Context?,
    private val rv: RecyclerView,
    private var categoryDMS: MutableList<KindCategoryModel?>?,
    val onKindClick: KindAdapter.OnKindClick
) :
        RecyclerView.Adapter<KindSubAdapter.Holder>() {
    private val limit = 6

    init {
        val layoutManager = rv.layoutManager as GridLayoutManager
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (categoryDMS?.size) {
                    3 -> when (position) {
                        0,1-> 1
                        2->layoutManager.spanCount
                        else -> 1
                    }
                    4 -> when (position) {
                        0 -> layoutManager.spanCount
                        else -> 1
                    }
                    5 -> when (position) {
                        0 -> layoutManager.spanCount
                        else -> 1
                    }
                    6 -> when (position) {
                        2 -> layoutManager.spanCount
                        else -> 1
                    }
                    else -> 1

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView: RowSubkindBinding =
            RowSubkindBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val childCat = categoryDMS?.get(position)
        try {
            GlideImg(
                context, childCat?.image, R.drawable.holder_image, holder.binding.ivCatImage
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        holder.binding.container.setOnClickListener { v ->
//            val bundle = Bundle()
//////            bundle.putSerializable(Constants.CAT_LIST, categoryDMS)
////            bundle.putInt(Constants.MAIN_CAT_ID, childCat?.parentId ?: 0)
////            bundle.putInt(Constants.SUB_CAT_ID, childCat?.id?:0)
////            bundle.putInt(Constants.position, position)
////            bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
////            EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
//            val intent = Intent(context, AllListActivity::class.java)
//            intent.putExtra(Constants.kind_id, childCat?.id)
//            context?.startActivity(intent)
//        }
    }


    override fun getItemCount(): Int {
        return if (limit != 0) Math.min(categoryDMS?.size ?: 0, limit) else categoryDMS?.size ?: 0
    }

    inner class Holder(val binding: RowSubkindBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

            itemView.setOnClickListener {
                val categoryModel = categoryDMS?.get(bindingAdapterPosition)

                onKindClick.onKindClicked(bindingAdapterPosition, categoryModel)
            }
        }
    }


}


