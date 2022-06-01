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
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.FavouriteResultModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.Models.MemberModel
import com.amour.shop.Models.ProductModel
import com.amour.shop.Models.request.ProductRequest
import com.amour.shop.R
import com.amour.shop.adapter.OfferProductAdapter
import com.amour.shop.databinding.ActivityFavoriteBinding
import java.util.ArrayList
import com.amour.shop.BuildConfig


class FavoriteActivity : ActivityBase(), OfferProductAdapter.OnItemClick {
    lateinit var binding: ActivityFavoriteBinding
    var productList: ArrayList<ProductModel>? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private val categoryId1 = 0
    private var countryId = 0
    private var cityId = 0
    private var userId: String? = null
    private var filter: String? = null
    private var user: MemberModel? = null
    private var localModel: LocalModel? = null
    private val brandId1 = 0
    private var kind_id = 0
    private var sortType:String = ""
    var productRequest: ProductRequest? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        user = UtilityApp.getUserData()
        if (user != null) {
            userId = user?.id.toString()
        }
        productList = ArrayList()
        gridLayoutManager = GridLayoutManager(activity, 2)
        binding.favoriteRecycler.layoutManager = gridLayoutManager
        filter = Constants.favourite_filter
        localModel = UtilityApp.getLocalData()
        setTitle(R.string.fav_products)
        countryId = localModel?.countryId?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        binding.favoriteRecycler.setHasFixedSize(true)
        binding.favoriteRecycler.itemAnimator = null
        productRequest = ProductRequest(categoryId1, countryId, cityId, filter, brandId1, 0, 10, kind_id, null, null)

        getFavoriteProducts(productRequest)

        binding.failGetDataLY.refreshBtn.setOnClickListener {
            getFavoriteProducts(
               productRequest
            )
        }
    }


    fun initAdapter() {
        val productFavAdapter = OfferProductAdapter(
            activity, productList, 0, 0, countryId, cityId, userId, productList?.size?:0,
            binding.favoriteRecycler, filter, this, { obj, func, IsSuccess ->
                val size = obj as Int
                if (size == 0) {
                    binding.dataLY.visibility = View.GONE
                    binding.noDataLY.noDataLY.visibility = View.VISIBLE
                    binding.noDataLY.noDataTxt.setText(R.string.no_products_fav)
                }
            }, 2
        )
        binding.favoriteRecycler.adapter = productFavAdapter
    }

    private fun getFavoriteProducts(
        productRequest: ProductRequest?
    ) {
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false, object :DataFetcherCallBack {
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
                            productList = result.data
                            Log.i(ContentValues.TAG, "Log getFavoriteProducts $productList?.size?:0 ")
                            initAdapter()
                        } else {
                            binding.dataLY.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.VISIBLE
                            binding.noDataLY.noDataTxt.setText(R.string.no_products_fav)
                        }
                    } else {
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                    }
                }            }

        }).getProductList(productRequest)
    }

    override fun onItemClicked(position: Int, productModel: ProductModel?) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.DB_productModel, productModel)
        startActivity(intent)    }
}