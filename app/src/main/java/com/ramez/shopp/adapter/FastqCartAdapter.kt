package com.ramez.shopp.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ramez.shopp.Classes.CartModel
import com.ramez.shopp.databinding.RowFastqCartItemBinding


class FastqCartAdapter(
    private val activity: Activity,
    private val list: MutableList<CartModel>?,
) :
        RecyclerView.Adapter<FastqCartAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = RowFastqCartItemBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val cartModel = list!![position]

//        holder.binding.nameTxt.text = languageModel.name

    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    inner class ItemViewHolder internal constructor(val binding: RowFastqCartItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {

            }
        }
    }

}
