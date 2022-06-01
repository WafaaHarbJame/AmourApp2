package com.amour.shop.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.Models.*
import com.amour.shop.Models.request.ProductRequest
import com.amour.shop.R
import com.amour.shop.activities.ProductDetailsActivity
import com.amour.shop.adapter.MainCategoryAdapter
import com.amour.shop.adapter.MainCategoryAdapter.OnMainCategoryItemClicked
import com.amour.shop.adapter.OfferProductAdapter
import com.amour.shop.classes.Constants
import com.amour.shop.classes.MessageEvent
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.FragmentOfferBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class OfferFragment : FragmentBase(), OfferProductAdapter.OnItemClick,
        OnMainCategoryItemClicked {
    private lateinit var binding: FragmentOfferBinding
    var productOffersList: MutableList<ProductModel?>? = null
    var gridLayoutManager: GridLayoutManager? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var category_id = 0
    var country_id = 0
    var city_id = 0
    var mainCategoryDMS: ArrayList<CategoryModel>? = null
    private var localModel: LocalModel? = null
    private var categoryAdapter: MainCategoryAdapter? = null
    private var productOfferAdapter: OfferProductAdapter? = null
    private val kind_id = 0
    private val sortType = ""
    var numColumn = 2
    var user_id = "0"
    private val brand_id = 0
    private var productRequest: ProductRequest? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOfferBinding.inflate(inflater, container, false)
        val view: View = binding.root
        productOffersList = ArrayList()
        mainCategoryDMS = ArrayList()
        localModel = UtilityApp.getLocalData()
        country_id = localModel?.countryId ?: UtilityApp.getLocalData().countryId
        city_id = localModel?.cityId?.toInt() ?: UtilityApp.getLocalData().cityId.toInt()
        linearLayoutManager = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        binding.catRecycler.setHasFixedSize(true)
        binding.catRecycler.layoutManager = linearLayoutManager
        if (UtilityApp.isLogin()) {
            val memberModel = UtilityApp.getUserData()
            if (memberModel != null && memberModel.id != null) {
                user_id = memberModel.id.toString()
            }
        }

        gridLayoutManager = GridLayoutManager(activityy, numColumn)
        binding.offerRecycler.layoutManager = gridLayoutManager
        binding.offerRecycler.setHasFixedSize(true)
        binding.offerRecycler.itemAnimator = null
        if (UtilityApp.getCategories()?.isNotEmpty() == true) {
            mainCategoryDMS = arrayListOf()
            mainCategoryDMS?.addAll(UtilityApp.getCategories() ?: mutableListOf())
//            mainCategoryDMS?.add(CategoryModel(0))
            val all = CategoryModel()
            all.id = 0
            all.sethName(getString(R.string.all))
            all.name = getString(R.string.all)
            mainCategoryDMS?.add(0, all)
            initCateAdapter()
        } else {
            getCategories(city_id)
        }
        productsList
        binding.dataLY.setOnRefreshListener { productsList }
        binding.failGetDataLY.refreshBtn.setOnClickListener { view1 -> productsList }
        return view
    }

    fun initAdapter() {

//        binding.offerRecycler.invalidate();
//        productOfferAdapter = null;
        productOfferAdapter = OfferProductAdapter(
            activityy, productOffersList, category_id, 0, country_id, city_id, user_id,
            productOffersList!!.size, binding.offerRecycler, Constants.offered_filter, this,
            { obj: Any?, func: String?, IsSuccess: Boolean -> }, numColumn
        )
        binding.offerRecycler.adapter = productOfferAdapter
        //        productOfferAdapter.setAdapterData(productOffersList, category_id, 0, country_id, city_id, user_id, Constants.offered_filter, numColumn);
//        productOfferAdapter.notifyDataSetChanged();
    }

    override fun onItemClicked(position: Int, productModel: ProductModel?) {
        val intent = Intent(activityy, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.DB_productModel, productModel)
        Log.i(javaClass.simpleName, "Log offer  IsSpecial  " + productModel!!.firstProductBarcodes.isSpecial)
        Log.i(
            javaClass.simpleName,
            "Log offer SpecialPrice  " + productModel.firstProductBarcodes.specialPrice
        )
        Log.i(javaClass.simpleName, "Log offer  price  " + productModel.firstProductBarcodes.price)
        Log.i(javaClass.simpleName, "Log offer Id  " + productModel.firstProductBarcodes.id)
        startActivity(intent)
    }

    fun getOfferList(productRequest: ProductRequest?) {

//        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE

        binding.shimmerLayout.visibility = View.VISIBLE
        binding.shimmerLayout.startShimmer()

        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE

        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        productOffersList?.clear()

        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {

//                    binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = gone

                    var message: String? = getString(R.string.fail_to_get_data)
                    if (binding.dataLY.isRefreshing) binding.dataLY.isRefreshing = false
                    if (func == Constants.ERROR) {
                        val result = obj as FavouriteResultModel?
                        if (result != null) {
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
                            val result = obj as FavouriteResultModel?

                            if (result?.data?.isNotEmpty() == true) {
                                binding.dataLY.visibility = View.VISIBLE
                                binding.noDataLY.noDataLY.visibility = View.GONE
                                binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                productOffersList = result.data
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
            }
        }).getProductList(productRequest)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.type == MessageEvent.TYPE_SORT) {
            //getOfferList(category_id, country_id, city_id, user_id, Constants.offered_filter, brand_id, 0, 10);
        } else if (event.type == MessageEvent.TYPE_view) {
            numColumn = event.data as Int
            try {
                gridLayoutManager!!.spanCount = numColumn
                binding.offerRecycler.layoutManager = gridLayoutManager
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (productOfferAdapter != null) productOfferAdapter!!.notifyDataSetChanged()

//            initAdapter();
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    fun getCategories(storeId: Int) {
//        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE

        binding.shimmerLayout.visibility = View.VISIBLE
        binding.shimmerLayout.startShimmer()

        binding.noDataLY.noDataLY.visibility = View.GONE

        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = gone
//                    binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE

                    val result = obj as CategoryResultModel?
                    var message: String? = activityy.getString(R.string.fail_to_get_data)
                    if (func == Constants.ERROR) {
                        if (result != null) {
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
                            if (result != null && result.data != null && result.data.size > 0) {
                                binding.dataLY.visibility = View.VISIBLE
                                binding.noDataLY.noDataLY.visibility = View.GONE
                                binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                mainCategoryDMS = arrayListOf()
                                mainCategoryDMS?.addAll(result.data)
                                Log.i(
                                    ContentValues.TAG,
                                    "Log productBestList" + mainCategoryDMS?.size
                                )
                                UtilityApp.setCategoriesData(mainCategoryDMS)
                                initCateAdapter()
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
            }
        }).GetAllCategories(storeId)
    }

    private fun initCateAdapter() {
        categoryAdapter = MainCategoryAdapter(activityy, mainCategoryDMS, this, category_id)
        binding.catRecycler.adapter = categoryAdapter
    }


    private val productsList: Unit
        private get() {
            productRequest = ProductRequest(
                category_id,
                country_id,
                city_id,
                Constants.offered_filter,
                brand_id,
                0,
                10,
                kind_id,
                null,
                null
            )
            getOfferList(productRequest)
        }

    override fun OnMainCategoryItemClicked(mainCategoryDM: CategoryModel?, position: Int) {
        category_id = mainCategoryDM?.id ?: 0
        Log.i(ContentValues.TAG, "Log MainItem category_id $category_id")
        productsList
    }
}
