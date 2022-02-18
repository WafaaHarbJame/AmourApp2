package com.ramez.shopp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ramez.shopp.CallBack.DataCallback
import com.ramez.shopp.Models.DeliveryTypeModel
import com.ramez.shopp.R
import com.ramez.shopp.databinding.RowDeliveryTypeBinding

    class DeliveryTypeAdapter(
        var context: Context,
        var paymentMethods: MutableList<DeliveryTypeModel>?,
        dataCallback: DataCallback?
    ) :
            RecyclerView.Adapter<DeliveryTypeAdapter.MyHolder>() {
        var inflater: LayoutInflater
        var selectedIndex = 0
        var dataCallback: DataCallback?
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            val view: RowDeliveryTypeBinding =
                RowDeliveryTypeBinding.inflate(LayoutInflater.from(context), parent, false)
            return MyHolder(view)
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            val paymentMethod = paymentMethods?.get(position)
            holder.binding.deliveryNameTv.setText(paymentMethod?.getName())
            holder.binding.paymentIv.setImageResource(paymentMethod?.image!!)
            if (selectedIndex == paymentMethod.id) {
                holder.binding.cardView.setBackground(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.round_medium_corner_selected
                    )
                )
                holder.binding.rbDeliveryType.isChecked = true

            } else {
                holder.binding.cardView.setBackground(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.round_medium_corner_unselected_gray
                    )
                )
                holder.binding.rbDeliveryType.isChecked = false
            }
            holder.binding.cardView.setOnClickListener { v ->
                selectedIndex = paymentMethod.id
                Log.i("tag", "Log 1" + paymentMethod.id)
                notifyDataSetChanged()
                if (dataCallback != null) {
                    dataCallback!!.dataResult(paymentMethod, "success", true)
                }
            }
        }

        override fun getItemCount(): Int {
            return paymentMethods?.size?:0
        }

        inner class MyHolder(itemView: RowDeliveryTypeBinding) :
                RecyclerView.ViewHolder(itemView.getRoot()) {
            var binding: RowDeliveryTypeBinding

            init {
                binding = itemView
            }
        }

        init {
            inflater = LayoutInflater.from(context)
            this.dataCallback = dataCallback
            //        this.selectedIndex=selectedIndex;
        }
    }


