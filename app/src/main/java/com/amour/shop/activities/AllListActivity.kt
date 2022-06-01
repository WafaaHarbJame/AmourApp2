package com.amour.shop.activities


import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.classes.Constants
import com.amour.shop.classes.Constants.MAIN_ACTIVITY_CLASS
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.FavouriteResultModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.Models.MemberModel
import com.amour.shop.Models.ProductModel
import com.amour.shop.Models.request.ProductRequest
import com.amour.shop.R
import com.amour.shop.adapter.ProductCategoryAdapter
import com.amour.shop.classes.FilterModel
import com.amour.shop.classes.SortModel
import com.amour.shop.databinding.ActivityAllListBinding
import java.util.ArrayList
import com.amour.shop.BuildConfig


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
    private var sortType: String = ""
    private var kindId = 0
    private var sortList: ArrayList<SortModel>? = null
    private var filterList: ArrayList<FilterModel>? = null
    var productRequest: ProductRequest? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllListBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        list = ArrayList()
        gridLayoutManager = GridLayoutManager(activity, 2)
        binding.recycler.layoutManager = gridLayoutManager
        binding.recycler.setHasFixedSize(true)
        localModel = UtilityApp.getLocalData()
        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        user = UtilityApp.getUserData()
        binding.recycler.setHasFixedSize(true)
        binding.recycler.itemAnimator = null
        if (UtilityApp.isLogin() && user != null && user?.id != null) {
            userId = user?.id.toString()
        }
        getIntentExtra

        binding.swipeDataContainer.setOnRefreshListener {
            binding.swipeDataContainer.isRefreshing = false
            callGetProducts()
        }
        binding.failGetDataLY.refreshBtn.setOnClickListener { view1 ->
            callGetProducts()
        }
    }

    fun initAdapter() {
        adapter = ProductCategoryAdapter(
            kindId,
            sortType,
            activity,
            binding.recycler,
            list,
            0,
            countryId,
            cityId,
            userId,
            list?.size ?: 0,
            filter,
            this,
            Constants.twoRow,
            brandId, sortList,filterList
        )
        binding.recycler.adapter = adapter
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
                kindId = bundle.getInt(Constants.kind_id)
               callGetProducts()
            }
        }

    private fun getProductList(
        productRequest: ProductRequest?
    ) {
        list?.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false, object : DataFetcherCallBack {
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
                }
            }

        }).getProductList(productRequest)
    }

    override fun onBackPressed() {
        println("Log onBackPressed $isNotify")
        if (isNotify) {
            val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else super.onBackPressed()
    }

    override fun onItemClicked(position: Int, productModel: ProductModel?) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.DB_productModel, productModel)
        startActivity(intent)
    }
    private fun callGetProducts() {

        productRequest = ProductRequest(0, countryId, cityId, filter, brandId, 0, 10, kindId, sortList, filterList)

        getProductList(productRequest)
    }

}