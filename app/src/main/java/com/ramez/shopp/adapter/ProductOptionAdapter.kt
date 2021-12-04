package com.ramez.shopp.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ramez.shopp.CallBack.DataCallback
import com.ramez.shopp.Models.ProductBarcode
import com.ramez.shopp.R
import com.ramez.shopp.databinding.RowProductOptionBinding


class ProductOptionAdapter(
    var context: Context,
    var productBarcodes: List<ProductBarcode>,
    var dataCallback: DataCallback?
) :
        RecyclerView.Adapter<ProductOptionAdapter.MyHolder>() {
    var inflater: LayoutInflater = LayoutInflater.from(context)
    var selectedIndex = -1
    private var lastIndex = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view =
            RowProductOptionBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val productOptionModel = productBarcodes[position]
        val wightName = productOptionModel.productUnits?.unitName?:""
        holder.binding.btnCategory.text = wightName
        if (lastIndex == position) {
            holder.binding.btnCategory.background =
                ContextCompat.getDrawable(context, R.drawable.round_corner_green)
            holder.binding.btnCategory.setTextColor(ContextCompat.getColor(context, R.color.green3))
        } else {
            holder.binding.btnCategory.background =
                ContextCompat.getDrawable(context, R.drawable.round_corner_grey)
            holder.binding.btnCategory.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }

    override fun getItemCount(): Int {
        return productBarcodes.size
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class MyHolder(var binding: RowProductOptionBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        init {
            binding.btnCategory.setOnClickListener { v ->
                val position = bindingAdapterPosition
                selectedIndex = position
                lastIndex = position
                if (position >= 0 && position < productBarcodes.size) {
                    val productBarcode = productBarcodes[position]
                    Log.i("tag", "Log 1" + productBarcode.id)
                    notifyDataSetChanged()
                    if (dataCallback != null) {
                        dataCallback?.dataResult(productBarcode, "success", true)
                    }
                }
            }
        }
    }

}

