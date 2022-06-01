package com.amour.shop.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.Models.BrandModel
import com.amour.shop.R
import com.amour.shop.databinding.RowBandFilterItemBinding

internal class BrandsFilterAdapter(

    private val context: Context,
    private var list: ArrayList<BrandModel>?,
    private val onBrandClick: OnBrandClick,
    private val limit: Int
) :
        RecyclerView.Adapter<BrandsFilterAdapter.Holder>() {
    var brandsSets: MutableSet<String>? = null

    init {
        brandsSets= mutableSetOf()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView: RowBandFilterItemBinding =
            RowBandFilterItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val brandModel = list?.get(position)

        holder.binding.brandNameTV.text = brandModel?.brandsName

        if (brandsSets?.contains(brandModel?.id.toString()) == true) {
            holder.binding.chBrandName.text = context.getString(R.string.fa_check);
            holder.binding.chBrandName.setTextColor(ContextCompat.getColor(context, R.color.green5));

        } else {
            holder.binding.chBrandName.text = ""

        }


    }


    override fun getItemCount(): Int {
        return if (limit != 0) list?.size?.coerceAtMost(limit) ?: 0 else list?.size ?: 0
    }

    interface OnBrandClick {
        fun onBrandClicked(position: Int,brandsSets: MutableSet<String>?)
    }

    inner class Holder(view: RowBandFilterItemBinding) : RecyclerView.ViewHolder(view.root) {
        var binding: RowBandFilterItemBinding = view


        init {

            itemView.setOnClickListener {
                val brandModel = list?.get(bindingAdapterPosition)
                if (brandsSets?.contains(brandModel?.id.toString()) == true) {

                    brandsSets?.remove(brandModel?.id.toString())

                } else {
                    brandsSets?.add(brandModel?.id.toString())

                }

                notifyDataSetChanged()
                onBrandClick.onBrandClicked(bindingAdapterPosition, brandsSets)
                Log.i(javaClass.name, "Log brandsSets ${brandsSets.toString()}")


            }
        }


    }
}

