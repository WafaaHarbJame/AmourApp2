package com.amour.shop.fragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.classes.Constants
import com.amour.shop.classes.Constants.MAIN_ACTIVITY_CLASS
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Dialogs.CheckLoginDialog
import com.amour.shop.Models.OrderNewModel
import com.amour.shop.Models.OrderResultModel
import com.amour.shop.R
import com.amour.shop.adapter.MyOrdersAdapter
import com.amour.shop.databinding.FragmentPastOrderBinding
import java.util.ArrayList


class PastOrderFragment : FragmentBase() {
    var completeOrdersList: MutableList<OrderNewModel>? = null
    var linearLayoutManager: LinearLayoutManager? = null
    private  lateinit var binding: FragmentPastOrderBinding
    private var myOrdersAdapter: MyOrdersAdapter? = null
    private var userId = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPastOrderBinding.inflate(inflater, container, false)
        val view: View = binding.root
        completeOrdersList = ArrayList()
        linearLayoutManager = LinearLayoutManager(activity)
        binding.myOrderRecycler.layoutManager = linearLayoutManager
        if (UtilityApp.getUserData() != null && UtilityApp.getUserData()?.id != null) {
            userId = UtilityApp.getUserData()?.id?:0
            getOrders(userId, Constants.user_type, Constants.past_order)
        } else {
            val checkLoginDialog = CheckLoginDialog(
                activityy,
                R.string.please_login,
                R.string.account_data,
                R.string.ok,
                R.string.cancel,
                null,
                null
            )
            checkLoginDialog.show()
        }
        binding.swipe.setOnRefreshListener {
            binding.swipe.isRefreshing = false
            getOrders(
                userId,
                Constants.user_type,
                Constants.past_order
            )
        }
        binding.failGetDataLY.refreshBtn.setOnClickListener { view1 ->
            getOrders(
                userId,
                Constants.user_type,
                Constants.past_order
            )
        }
        binding.noDataLY.btnBrowseProducts.setOnClickListener { view1 ->
            val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        return view
    }

    private fun initOrdersAdapters(list: List<OrderNewModel>?) {
        myOrdersAdapter = MyOrdersAdapter(activity, binding.myOrderRecycler, list, userId)
        binding.myOrderRecycler.adapter = myOrdersAdapter
    }

    fun getOrders(user_id: Int, type: String?, filter: String?) {
        completeOrdersList!!.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false,object :
            DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    val result = obj as OrderResultModel?
                    var message: String? = ""
                    binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                    if (func == Constants.ERROR) {
                        message = if (result != null && result.message != null) {
                            result.message
                        } else {
                            getString(R.string.fail_to_get_data)
                        }
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                    } else if (func == Constants.FAIL) {
                        message = getString(R.string.fail_to_get_data)
                        if (result != null && result.message != null) {
                            message = result.message
                        }
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                    } else if (func == Constants.NO_CONNECTION) {
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection)
                        binding.failGetDataLY.noInternetIv.visibility = View.VISIBLE
                        binding.dataLY.visibility = View.GONE
                    } else {
                        if (IsSuccess) {
                            if (result!!.data != null && result.data.size > 0) {
                                binding.dataLY.visibility = View.VISIBLE
                                binding.noDataLY.noDataLY.visibility = View.GONE
                                binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                completeOrdersList = result.data
                                initOrdersAdapters(completeOrdersList)
                            } else {
                                binding.dataLY.visibility = View.GONE
                                binding.noDataLY.noDataLY.visibility = View.VISIBLE
                            }
                        } else {
                            binding.dataLY.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                            binding.failGetDataLY.failTxt.text = message
                        }
                    }
                }            }

        }).getOrders(user_id, type?:"", filter?:"")
    }
}