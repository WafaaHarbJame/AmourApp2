package com.ramez.shopp.activities


import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.Constants.MAIN_ACTIVITY_CLASS
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.FavouriteResultModel
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.Models.MemberModel
import com.ramez.shopp.Models.ProductModel
import com.ramez.shopp.R
import com.ramez.shopp.adapter.ProductCategoryAdapter
import com.ramez.shopp.databinding.ActivityAllListBinding
import java.util.ArrayList


class AllListActivity : ActivityBase(), ProductCategoryAdapter.OnItemClick {
    lateinit var binding: ActivityAllListBinding
    var list: ArrayList<ProductModel>? = null
    var gridLayoutManager: GridLayoutManager? = null
    var name: String? = ""
    private var adapter: ProductCategoryAdapter? = null
    private var countryId = 0
    private var cityId = 0
    private var userId = "0"
    private var filter: String? = ""
    private var user: MemberModel? = null
    private var localModel: LocalModel? = null
    private var brandId = 0
    private var isNotify = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllListBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        list = ArrayList()
        gridLayoutManager = GridLayoutManager(activity, 2)
        binding.recycler.layoutManager = gridLayoutManager
        binding.recycler.setHasFixedSize(true)
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
        countryId = localModel?.countryId ?:Constants.default_country_id
        cityId = localModel?.cityId?.toInt()?:Constants.default_storeId.toInt()
        user = UtilityApp.getUserData()
        binding.recycler.setHasFixedSize(true)
        binding.recycler.itemAnimator = null
        if (UtilityApp.isLogin() && user != null && user?.id != null) {
            userId = user?.id.toString()
        }
        getIntentExtra
        binding.swipeDataContainer.setOnRefreshListener {
            binding.swipeDataContainer.isRefreshing = false
            getProductList(0, countryId, cityId, userId, filter, brandId, 0, 10)
        }
        binding.failGetDataLY.refreshBtn.setOnClickListener { view1 ->
            getProductList(
                0,
                countryId,
                cityId,
                userId,
                filter,
                brandId,
                0,
                10
            )
        }
    }

    fun initAdapter() {
        adapter = ProductCategoryAdapter(
            activity,
            binding.recycler,
            list,
            0,
            countryId,
            cityId,
            userId,
            list?.size?:0,
            filter,
            this,
            Constants.twoRow,
            brandId
        )
        binding.recycler.adapter = adapter
    }

    override fun onItemClicked(position: Int, productModel: ProductModel) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.DB_productModel, productModel)
        startActivity(intent)
    }

    private val getIntentExtra: Unit
        get() {
            val bundle = intent.extras
            if (bundle != null) {
                name = bundle.getString(Constants.LIST_MODEL_NAME)
                filter = bundle.getString(Constants.FILTER_NAME)
                brandId = bundle.getInt(Constants.brand_id)
                isNotify = bundle.getBoolean(Constants.isNotify)
                title = name
                getProductList(0, countryId, cityId, userId, filter, brandId, 0, 10)
            }
        }

    private fun getProductList(
        categoryId: Int,
        country_id: Int,
        city_id: Int,
        user_id: String?,
        filter: String?,
        brand_id: Int,
        pageNumber: Int,
        pageSize: Int
    ) {
        list?.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false,object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as FavouriteResultModel?
                var message: String? = getString(R.string.fail_to_get_data)
                binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                if (func == Constants.ERROR) {
                    if (result != null && result.message != null) {
                        message = result.message
                    }
                    binding.dataLY.visibility = View.GONE
                    binding.noDataLY.noDataLY.visibility = View.GONE
                    binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                    binding.failGetDataLY.failTxt.text = message
                } else if (func == Constants.FAIL) {
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
                        if (result?.data != null && result.data.size > 0) {
                            binding.dataLY.visibility = View.VISIBLE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                            list = result.data
                            Log.i(ContentValues.TAG, "Log productList" + list?.size)
                            initAdapter()
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
                }            }

        }).getFavorite(categoryId, country_id, city_id, user_id, filter, brand_id, pageNumber, pageSize)
    }

    override fun onBackPressed() {
        println("Log onBackPressed $isNotify")
        if (isNotify) {
            val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else super.onBackPressed()
    }
}