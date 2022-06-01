package com.amour.shop.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.BuildConfig
import com.amour.shop.CallBack.DataCallback
import com.amour.shop.Models.DeliveryTypeModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.R
import com.amour.shop.Utils.NumberHandler
import com.amour.shop.classes.Constants
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.RowDeliveryTypeBinding

class DeliveryTypeAdapter(
    var context: Context,
    var paymentMethods: MutableList<DeliveryTypeModel>?,
    dataCallback: DataCallback?
) :
        RecyclerView.Adapter<DeliveryTypeAdapter.MyHolder>() {
    var inflater: LayoutInflater = LayoutInflater.from(context)
    var selectedIndex = 0
    var fraction = 2
    var localModel: LocalModel? = null
    private var currency = "BHD"
    var dataCallback: DataCallback? = dataCallback
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view: RowDeliveryTypeBinding =
            RowDeliveryTypeBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val paymentMethod = paymentMethods?.get(position)
        localModel= UtilityApp.getLocalData()

        Log.i(javaClass.simpleName, "Log deliveryFeesonBindViewHolder ${paymentMethod?.deliveryPrice}")

        currency = localModel?.currencyCode ?: Constants.BHD
        fraction = localModel?.fractional ?: Constants.three

        holder.binding.deliveryTime.text = paymentMethod?.deliveryTime
        holder.binding.deliveryNameTv.text = paymentMethod?.methodName

        val deliveryPrice: Double = paymentMethod?.deliveryPrice ?: 0.0

        if (deliveryPrice == 0.0 /*|| deliveryPrice == 0.00 || deliveryPrice == 0.000*/) {
            holder.binding.deliveryPrice.text = context.getString(R.string.free)

        } else {

            holder.binding.deliveryPrice.text = NumberHandler.formatDouble(paymentMethod?.deliveryPrice ?: 0.0,fraction).plus(" $currency")

        }

        holder.binding.deliveryIcon.setImageResource(paymentMethod!!.unselectedImage)



        if (paymentMethod.id == 1) {
            holder.binding.deliveryTime.visibility = View.GONE
            holder.binding.deliveryPrice.visibility = View.VISIBLE


        } else {
            holder.binding.deliveryPrice.visibility = View.VISIBLE
            holder.binding.deliveryTime.visibility = View.VISIBLE

        }



        if (selectedIndex == paymentMethod.id) {
            holder.binding.cardView.background = ContextCompat.getDrawable(
                context,
                R.drawable.round_medium_corner_selected
            )
            holder.binding.rbDeliveryType.isChecked = true
            holder.binding.cardView.background = ContextCompat.getDrawable(
                context,
                R.drawable.round_medium_corner_selected
            )

            holder.binding.deliveryIcon.setImageResource(paymentMethod.selectedImage)


        } else {
            holder.binding.cardView.background = ContextCompat.getDrawable(
                context,
                R.drawable.round_medium_corner_unselected_gray
            )
            holder.binding.rbDeliveryType.isChecked = false
            holder.binding.deliveryIcon.setImageResource(paymentMethod?.unselectedImage!!)

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
        return paymentMethods?.size ?: 0
    }

    inner class MyHolder(itemView: RowDeliveryTypeBinding) :
            RecyclerView.ViewHolder(itemView.getRoot()) {
        var binding: RowDeliveryTypeBinding = itemView

    }

    init {
        //        this.selectedIndex=selectedIndex;
    }
}


