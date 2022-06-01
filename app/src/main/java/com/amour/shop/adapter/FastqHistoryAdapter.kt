package com.amour.shop.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.classes.OnLoadMoreListener
import com.amour.shop.Models.FastqHistoryModel
import com.amour.shop.activities.FastqSummaryActivity
import com.amour.shop.databinding.RowFastqHistoryBinding
import com.amour.shop.databinding.RowLoadingBinding


class FastqHistoryAdapter(
    val activity: Activity,
    rv: RecyclerView,
    private val list: MutableList<FastqHistoryModel?>?,
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isLoading = false
    var nextPage = 1
    var show_loading = true
    var visibleThreshold = 5

    private var lastVisibleItem = 0
    private var totalItemCount = 0
    private var mOnLoadMoreListener: OnLoadMoreListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var vh: RecyclerView.ViewHolder? = null
        if (viewType == VIEW_TYPE_ITEM) {
            val itemView = RowFastqHistoryBinding.inflate(
                LayoutInflater.from(activity), parent, false
            )
            vh = ItemHolder(itemView)
        } else if (viewType == VIEW_TYPE_LOADING) {
            val itemView = RowLoadingBinding.inflate(LayoutInflater.from(activity), parent, false)
            vh = LoadingViewHolder(itemView)
        }
        return vh!!
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ItemHolder) {
            val ordersDM = list!![position]
//            holder.binding.tvInvID.setText(ordersDM!!.orderCode)
//            holder.binding.tvShopName.setText(ordersDM.storeName)

        } else if (holder is LoadingViewHolder) {

        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (list!![position] == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener?) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    fun setLoaded() {
        isLoading = false
    }

    private fun setOnloadListener() {
        setOnLoadMoreListener {
            println("Log add loading item")
            if (list?.contains(null) == false) {
                list.add(null)
                notifyItemInserted(list.size - 1)
                LoadData()
            }
        }
    }

    fun LoadData() {
        Log.d(TAG, "Log LoadData " + "LoadData")
    }

    internal class LoadingViewHolder(val binding: RowLoadingBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    inner class ItemHolder(val binding: RowFastqHistoryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

            binding.shoppingSummaryBtn.setOnClickListener {

                val ordersDM = list?.get(bindingAdapterPosition)

                val intent = Intent(activity, FastqSummaryActivity::class.java)
                activity.startActivity(intent)

            }
        }
    }

    companion object {
        const val VIEW_TYPE_ITEM = 1
        const val VIEW_TYPE_LOADING = 3
        private const val TAG = "MyOrdersAdapter"
    }

    init {
        val linearLayoutManager = rv.layoutManager as LinearLayoutManager?
        rv.layoutManager = linearLayoutManager
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItemCount = linearLayoutManager!!.itemCount
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
//
                if (show_loading) {
                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener?.onLoadMore();
                            isLoading = true
                        }
                    }
                }
                setOnloadListener()
            }
        })
    }
}