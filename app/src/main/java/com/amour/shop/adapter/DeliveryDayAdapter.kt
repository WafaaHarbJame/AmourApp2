package com.amour.shop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.CallBack.DataCallback
import com.amour.shop.Models.DeliveryTime
import com.amour.shop.R
import com.amour.shop.Utils.DateHandler.FormatDate4
import com.amour.shop.Utils.DateHandler.GetDateNowString
import com.amour.shop.classes.Constants
import com.amour.shop.classes.UtilityApp.getLanguage
import com.amour.shop.databinding.RowDayItemBinding


class DeliveryDayAdapter(
    var context: Context,
    private val deliveryDayList: MutableList<DeliveryTime>,
    var dataCallback: DataCallback?
) :
        RecyclerView.Adapter<DeliveryDayAdapter.ViewHolder>() {
    var lastIndex = 0
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView = RowDayItemBinding.inflate(LayoutInflater.from(context), viewGroup, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val deliveryTimes = deliveryDayList[position]
        val dayName: String
        var monthName = ""
        val lang = getLanguage()
        val today = GetDateNowString()
        val day = FormatDate4(deliveryTimes.date, "yyyy-MM-dd", "dd", lang)
        if (getLanguage() == Constants.Arabic) {
            monthName = FormatDate4(deliveryTimes.date, "yyyy-MM-dd", "MMMM", lang)
            dayName = if (today == deliveryTimes.date) {
                context.getString(R.string.today)
            } else {
                FormatDate4(deliveryTimes?.date?:"", "yyyy-MM-dd", "EEEE")
            }
        } else {
            monthName = FormatDate4(deliveryTimes.date, "yyyy-MM-dd", "MMM", lang)
            dayName = if (today == deliveryTimes.date) {
                context.getString(R.string.today)
            } else {
                FormatDate4(deliveryTimes.date, "yyyy-MM-dd", "EEEE").substring(0, 3)
            }
        }
        viewHolder.binding.dateTxt.text = "$day $monthName"
        viewHolder.binding.dayTxt.text = dayName
        if (lastIndex == position) {
            viewHolder.binding.cardView.background =
                ContextCompat.getDrawable(context, R.drawable.round_medium_corner_red)
            viewHolder.binding.dayTxt.setTextColor(ContextCompat.getColor(context, R.color.white))
            viewHolder.binding.dateTxt.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            viewHolder.binding.cardView.background =
                ContextCompat.getDrawable(context, R.drawable.round_medium_corner_unselected_gray)
            viewHolder.binding.dayTxt.setTextColor(ContextCompat.getColor(context, R.color.black))
            viewHolder.binding.dateTxt.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }

    override fun getItemCount(): Int {
        return deliveryDayList.size
    }

    inner class ViewHolder(var binding: RowDayItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        init {
            itemView.setOnClickListener { view1: View? ->
                val deliveryDay = deliveryDayList[bindingAdapterPosition]
                lastIndex = bindingAdapterPosition
                notifyDataSetChanged()
                if (dataCallback != null) {
                    dataCallback!!.dataResult(deliveryDay, "result", true)
                }
            }
        }
    }
}