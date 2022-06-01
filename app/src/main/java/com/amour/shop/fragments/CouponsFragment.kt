package com.amour.shop.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.Dialogs.CheckLoginDialog
import com.amour.shop.Dialogs.GenerateDialog
import com.amour.shop.Models.*
import com.amour.shop.R
import com.amour.shop.adapter.CouponsAdapter
import com.amour.shop.classes.Constants
import com.amour.shop.classes.Constants.MAIN_ACTIVITY_CLASS
import com.amour.shop.classes.DBFunction
import com.amour.shop.classes.GlobalData.REFRESH_CART
import com.amour.shop.classes.GlobalData.refresh_points
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.FragmentCouponsBinding


class CouponsFragment : FragmentBase(), CouponsAdapter.OnItemClick {
    var list: List<CouponsModel>? = null
    lateinit var binding: FragmentCouponsBinding
    private var adapter: CouponsAdapter? = null
    private var userId = 0
    private var countryId = 0
    var totalPointModel: TotalPointModel? = null
    var settingCouponsModel: SettingCouponsModel? = null
    var localModel: LocalModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCouponsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = ArrayList()
        localModel = UtilityApp.getLocalData()
        countryId = localModel?.countryId ?:Constants.default_country_id
        val linearLayoutManager = LinearLayoutManager(activity)
        binding.myOrderRecycler.layoutManager = linearLayoutManager
        data
        binding.failGetDataLY.refreshBtn.setOnClickListener { view1 -> data }
        binding.generateBut.setOnClickListener { v ->
            if (totalPointModel != null && totalPointModel!!.points > 0) {
                showGenerateDialog()
            } else {
                data
            }
            showGenerateDialog()
        }
        binding.noDataLY.btnBrowseProducts.setOnClickListener { view1 ->
            val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun showGenerateDialog() {
        val points: Double = if (totalPointModel != null) totalPointModel!!.points else 0.0
        if (settingCouponsModel == null) {
            couponSettings
        }
        val minimumPoints =
            if (settingCouponsModel != null && settingCouponsModel!!.minimumPoints > 0) settingCouponsModel!!.minimumPoints else 0
        val generateDialog = GenerateDialog(
            activityy,
            userId, points,
            minimumPoints,object :
            DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        REFRESH_CART = true
                        callGetTotalPoints()
                    }                }

            })
        generateDialog.show()
    }

    private fun initAdapter(list: List<CouponsModel>?) {
        adapter = CouponsAdapter(activity, list, list!!.size, this)
        binding.myOrderRecycler.adapter = adapter
    }

    private val data: Unit
        private get() {
            if (UtilityApp.getUserData() != null && UtilityApp.getUserData()?.id != null) {
                userId = UtilityApp.getUserData()?.id?:0
                totalPoint
                couponSettings
                callApi(true)
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
        }

    private fun callApi(loading: Boolean) {
        if (loading) {
            binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
            binding.noDataLY.noDataLY.visibility = View.GONE
            binding.dataLY.visibility = View.GONE
        }
        DataFeacher(false,object :
            DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                    if (IsSuccess) {
                        val result =
                            obj as ResultAPIModel<List<CouponsModel>?>?
                        if (result != null && result.status == 200) {
                            if (result.data != null && result.data!!.size > 0) {
                                binding.dataLY.visibility = View.VISIBLE
                                binding.noDataLY.noDataLY.visibility = View.GONE
                                val list = result.data
                                initAdapter(list)
                            } else {
                                binding.noDataLY.noDataLY.visibility = View.VISIBLE
                                binding.noDataLY.tvErrorMessage.text = getString(R.string.no_data)
                                binding.noDataLY.titleTv.text = getString(R.string.Coupons)
                                binding.noDataLY.btnBrowseProducts.visibility = View.GONE
                                binding.dataLY.visibility = View.GONE
                            }
                        }
                    } else {
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.dataLY.visibility = View.GONE
                    }
                }            }


        }).getCoupons(userId)
    }

    private val totalPoint: Unit
        private get() {
            totalPointModel = DBFunction.getTotalPoints()
            if (totalPointModel == null) {
                callGetTotalPoints()
            }
        }

    private fun callGetTotalPoints() {
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    val result = obj as ResultAPIModel<TotalPointModel?>?
                    if (result != null && result.isSuccessful && result.data != null) {
                        totalPointModel = result.data
                        Log.i(
                            javaClass.simpleName,
                            "Log  totalPointModel call " + totalPointModel!!.points
                        )
                        Log.i(
                            javaClass.simpleName,
                            "Log  totalPointModel call" + totalPointModel!!.value
                        )
                        DBFunction.setTotalPoints(totalPointModel)
                    }
                }            }

        }).getTotalPoint(userId)
    }

    private val couponSettings: Unit
        private get() {
            settingCouponsModel = DBFunction.getCouponSettings()
            if (settingCouponsModel == null) callGetCouponSettings()
        }

    private fun callGetCouponSettings() {
        DataFeacher(false,object :
            DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    val result =
                        obj as ResultAPIModel<SettingCouponsModel?>?
                    if (result?.data != null && result.status == 200) {
                        settingCouponsModel = result.data
                        DBFunction.setCouponSettings(settingCouponsModel)
                    }
                }            }

        }).getSettings(countryId)
    }

    override fun onItemClicked(position: Int, categoryModel: CouponsModel) {}
    override fun onResume() {
        super.onResume()
        if (refresh_points) {
            callGetTotalPoints()
            refresh_points = false
        }
    }
}