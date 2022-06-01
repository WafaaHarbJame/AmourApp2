package com.amour.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.Models.BrandModel
import com.amour.shop.R
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData.GlideImg
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.RowBandItemBinding


 class BrandsAdapter(
    private val context: Context,
    private var list: MutableList<BrandModel>?,
    private val onBrandClick: OnBrandClick,
    private val limit: Int
) :
        RecyclerView.Adapter<BrandsAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = RowBandItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val brandModel = list?.get(position)
        var imageUrl: String? = ""
        imageUrl = if (UtilityApp.getLanguage() == Constants.English) {
            brandModel?.image
        } else {
            brandModel?.image2
        }
        try {
            GlideImg(
                context, imageUrl, R.drawable.holder_image, holder.binding.ivCatImage
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.binding.container.setOnClickListener { v -> onBrandClick.onBrandClicked(position, brandModel) }
    }

    fun setCategoriesList(list: MutableList<BrandModel>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (limit != 0) list?.size?.coerceAtMost(limit)?:0 else list?.size?:0
    }

    interface OnBrandClick {
        fun onBrandClicked(position: Int, brandModel: BrandModel?)
    }

    class Holder(var binding: RowBandItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
