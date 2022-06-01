package com.amour.shop.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.Models.AreasModel
import com.amour.shop.R
import com.amour.shop.classes.Constants
import com.amour.shop.databinding.RowCityBinding


class StatesAdapter(
    private val activity: Activity,
    private val list: List<AreasModel>?,
    private var selectedCity: Int,
    private val dataFetcherCallBack: DataFetcherCallBack?
) :
        RecyclerView.Adapter<StatesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = RowCityBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cityModel = list?.get(position)
        holder.binding.nameTxt.text = cityModel?.stateName ?: ""
        if (selectedCity == cityModel?.id ?: 0) {
            holder.binding.selectTxt.text = activity.getString(R.string.fa_check)
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(activity, R.color.green))
        } else {
            holder.binding.selectTxt.text = ""
            holder.binding.selectTxt.setTextColor(ContextCompat.getColor(activity, R.color.header3))
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ViewHolder(var binding: RowCityBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        init {
            itemView.setOnClickListener { v: View? ->
                val cityModel = list?.get(bindingAdapterPosition)
                selectedCity = cityModel?.id ?: 0
                notifyDataSetChanged()
                dataFetcherCallBack?.Result(
                    cityModel,
                    Constants.success,
                    true
                )

            }
        }
    }
}