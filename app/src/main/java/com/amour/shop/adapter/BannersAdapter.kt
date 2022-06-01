package com.amour.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.Models.Slider
import com.amour.shop.R
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData.GlideImgGif
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.RowBannersItemBinding


class BannersAdapter(
    private val context: Context,
    private val list: MutableList<Slider?>?,
    private val onBannersClick: OnBannersClick
) :
        RecyclerView.Adapter<BannersAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = RowBannersItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val slider = list?.get(position)
        val imageUrl: String
        imageUrl = if (UtilityApp.getLanguage() == Constants.English) {
            slider?.image ?: ""
        } else {
            slider?.image2 ?: ""
        }
        try {
            GlideImgGif(context, imageUrl, R.drawable.holder_image, holder.binding.ivCatImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.binding.container.setOnClickListener { v -> onBannersClick.onBannersClicked(position, slider) }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    interface OnBannersClick {
        fun onBannersClicked(position: Int, slider: Slider?)
    }

    class Holder(var binding: RowBannersItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}