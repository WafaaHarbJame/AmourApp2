package com.amour.shop.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.CallBack.DataCallback
import com.amour.shop.Models.ProductChecker
import com.amour.shop.R
import com.amour.shop.databinding.RowProductCheckBinding


class ProductCheckAdapter(//    public int lastIndex = 0;
    var context: Context,
    private val list: MutableList<ProductChecker>?,
    var selectedPosition: Int,
    var dataCallback: DataCallback?
) :
        RecyclerView.Adapter<ProductCheckAdapter.ViewHolder>() {
    var isSelected = false
    private var lastCheckedRB: RadioButton? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView = RowProductCheckBinding.inflate(LayoutInflater.from(context), viewGroup, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val productChecker = list?.get(position)
        viewHolder.binding.productCheckTxt.text = productChecker?.name ?: ""
        viewHolder.binding.selectTxt.setOnCheckedChangeListener(null)
        if (selectedPosition == (productChecker?.id ?: 0)) {
            viewHolder.binding.selectTxt.isChecked = true
            viewHolder.binding.selectTxt.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimaryDark
                )
            )
        } else {
            viewHolder.binding.selectTxt.isChecked = false
            viewHolder.binding.selectTxt.setTextColor(ContextCompat.getColor(context, R.color.header3))
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    inner class ViewHolder @SuppressLint("NotifyDataSetChanged") constructor(var binding: RowProductCheckBinding) :
            RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val deliveryTime = list?.get(bindingAdapterPosition)
                selectedPosition = deliveryTime?.id ?: 0
                notifyDataSetChanged()
                if (dataCallback != null) {
                    dataCallback!!.dataResult(deliveryTime, "result", true)
                }
            }
            binding.selectTxt.setOnClickListener { v ->
                lastCheckedRB?.isChecked = false
                lastCheckedRB = binding.selectTxt
                val deliveryTime = list?.get(bindingAdapterPosition)
                selectedPosition = deliveryTime?.id ?: 0
                notifyDataSetChanged()
                if (dataCallback != null) {
                    dataCallback!!.dataResult(deliveryTime, "result", true)
                }
            }
        }
    }

    companion object {
        private const val TAG = "DeliveryTimeAdapter"
    }
}