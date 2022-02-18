package com.ramez.shopp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.analytics.FirebaseAnalytics
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.classes.AnalyticsHandler
import com.ramez.shopp.classes.Constants
import com.ramez.shopp.classes.Constants.MAIN_ACTIVITY_CLASS
import com.ramez.shopp.classes.GlobalData
import com.ramez.shopp.classes.UtilityApp
import com.ramez.shopp.Dialogs.AddRateDialog
import com.ramez.shopp.Dialogs.CheckLoginDialog
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.ActivityHandler
import com.ramez.shopp.Utils.DateHandler
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.adapter.ProductOptionAdapter
import com.ramez.shopp.adapter.ReviewAdapter
import com.ramez.shopp.adapter.SuggestedProductAdapter
import com.ramez.shopp.databinding.ActivityProductDeatilsBinding
import com.ramez.shopp.fragments.ImageFragment
import com.ramez.shopp.fragments.SuggestedProductsFragment
import es.dmoral.toasty.Toasty
import java.text.DecimalFormat
import java.util.ArrayList

class ProductDetailsActivity : ActivityBase(), SuggestedProductAdapter.OnItemClick {
    private lateinit var binding: ActivityProductDeatilsBinding
    var userId1 = 0
    var productList: ArrayList<ProductModel>? = null
    var reviewList: ArrayList<ReviewModel>? = null
    var productName: String? = null
    var productModel: ProductModel? = null
    var currency: String? = null
    var isNotify = false
    var addCommentDialog: AddRateDialog? = null
    private var countryId = 0
    private var cityId = 0
    private var productId1 = 0
    private var productOfferAdapter: SuggestedProductAdapter? = null
    private var reviewAdapter: ReviewAdapter? = null
    private var productLayoutManager: LinearLayoutManager? = null
    private var reviewManger: LinearLayoutManager? = null
    private var storeId = 0
    private var isFavorite = false
    private var fromBrousher = false
    private var selectedProductBarcode: ProductBarcode? = null
    private var selectedProductPos = 0
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    var localModel: LocalModel? = null
    private var fraction = 2
    private var categoryId = 0
    var filter: String? = null
    private var kind_id = 0
    private var sortType:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDeatilsBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        val memberModel = UtilityApp.getUserData()
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        productList = ArrayList()
        reviewList = ArrayList()
        currency = localModel?.currencyCode
        fraction = localModel?.fractional ?: Constants.three
        storeId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        productLayoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.offerRecycler.layoutManager = productLayoutManager
        binding.offerRecycler.setHasFixedSize(true)
        binding.offerRecycler.itemAnimator = null
        val productOptionLayoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        binding.productOptionRv.layoutManager = productOptionLayoutManager
        binding.productOptionRv.setHasFixedSize(true)
        binding.offerRecycler.setHasFixedSize(true)
        binding.reviewRecycler.setHasFixedSize(true)
        binding.productOptionRv.itemAnimator = null
        binding.offerRecycler.itemAnimator = null
        binding.reviewRecycler.itemAnimator = null
        reviewManger = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.reviewRecycler.layoutManager = reviewManger
        binding.reviewRecycler.setHasFixedSize(true)
        if (UtilityApp.isLogin()) {
            if (memberModel != null) {
                userId1 = memberModel.id.toString().toInt()
            }
        }
        intentExtra
        initListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        binding.backBtn.setOnClickListener { view1 ->
            GlobalData.REFRESH_CART = true
            if (isNotify) {
                val intent =
                    Intent(activity, MAIN_ACTIVITY_CLASS)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            } else onBackPressed()
        }
        binding.moreBoughtBut.setOnClickListener { v ->
            val intent = Intent(activity, AllListActivity::class.java)
            intent.putExtra(Constants.LIST_MODEL_NAME, getString(R.string.best_sell))
            intent.putExtra(
                Constants.FILTER_NAME,
                Constants.quick_filter
            )
            startActivity(intent)
        }
        binding.showAllBut.setOnClickListener { v ->
            binding.showAllBut.setText(if (binding.productDesc1Tv.isExpanded) R.string.ShowAll else R.string.Show_less)
            binding.productDesc1Tv.toggle()
        }
        binding.addRateBut.setOnClickListener { view ->
            if (!UtilityApp.isLogin()) {
                val checkLoginDialog = CheckLoginDialog(
                    activity,
                    R.string.LoginFirst,
                    R.string.to_add_comment,
                    R.string.ok,
                    R.string.cancel_label,
                    null,
                    null
                )
                checkLoginDialog.show()
            } else {
                val reviewModel = ReviewModel()
                val okClick: AddRateDialog.Click = object : AddRateDialog.Click() {
                    override fun click() {
                        if (!UtilityApp.isLogin()) {
                            val checkLoginDialog = CheckLoginDialog(
                                activity,
                                R.string.LoginFirst,
                                R.string.to_add_comment,
                                R.string.ok,
                                R.string.cancel_label,
                                null,
                                null
                            )
                            checkLoginDialog.show()
                        } else {
                            val note = addCommentDialog!!.findViewById<EditText>(R.id.rateEt)
                            val ratingBar =
                                addCommentDialog!!.findViewById<RatingBar>(R.id.ratingBar)
                            val notes = note.text.toString()
                            reviewModel.comment = notes
                            reviewModel.productId = productId1
                            reviewModel.storeId = storeId
                            reviewModel.user_id = userId1
                            reviewModel.rate = ratingBar.rating.toInt()
                            when {
                                ratingBar.rating == 0f -> {
                                    Toasty.error(
                                        activity,
                                        R.string.please_fill_rate,
                                        Toast.LENGTH_SHORT,
                                        true
                                    ).show()
                                    YoYo.with(Techniques.Shake).playOn(ratingBar)
                                    ratingBar.requestFocus()
                                }
                                note.text.toString().isEmpty() -> {
                                    note.requestFocus()
                                    note.error = getString(R.string.please_fill_comment)
                                }
                                else -> {
                                    addComment(view, reviewModel)
                                }
                            }
                        }
                    }
                }
                val cancelClick: AddRateDialog.Click = object : AddRateDialog.Click() {
                    override fun click() {
                        addCommentDialog!!.dismiss()
                    }
                }
                addCommentDialog = AddRateDialog(
                    activity,
                    getString(R.string.add_comment),
                    R.string.ok,
                    R.string.cancel_label,
                    okClick,
                    cancelClick
                )
                addCommentDialog!!.show()
            }
        }
        binding.shareBtn.setOnClickListener { v ->
            ActivityHandler.shareTextUrlDeep(
                activity,
                getString(R.string.share_note) + "  https://ramezshopping.com/product/" + productId1 + "/store/" + storeId,
                null, null
            )
        }
        binding.addToFavBut.setOnClickListener { v ->
            Log.i("tag", "Log isFavorite$isFavorite")
            if (!UtilityApp.isLogin()) {
                val checkLoginDialog = CheckLoginDialog(
                    activity,
                    R.string.LoginFirst,
                    R.string.to_add_favorite,
                    R.string.ok,
                    R.string.cancel_label,
                    null,
                    null
                )
                checkLoginDialog.show()
            } else {
                if (isFavorite) {
                    removeFromFavorite(v, productId1, userId1, storeId)
                } else {
                    addToFavorite(v, productId1, userId1, storeId)
                }
            }
        }
        binding.ratingBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (!UtilityApp.isLogin()) {
                    val checkLoginDialog = CheckLoginDialog(
                        activity,
                        R.string.LoginFirst,
                        R.string.to_add_comment,
                        R.string.ok,
                        R.string.cancel_label,
                        null,
                        null
                    )
                    checkLoginDialog.show()
                } else {
                    val reviewModel = ReviewModel()
                    val okClick: AddRateDialog.Click = object : AddRateDialog.Click() {
                        override fun click() {
                            if (!UtilityApp.isLogin()) {
                                val checkLoginDialog = CheckLoginDialog(
                                    activity,
                                    R.string.LoginFirst,
                                    R.string.to_add_comment,
                                    R.string.ok,
                                    R.string.cancel_label,
                                    null,
                                    null
                                )
                                checkLoginDialog.show()
                            } else {
                                val note = addCommentDialog!!.findViewById<EditText>(R.id.rateEt)
                                val ratingBar =
                                    addCommentDialog!!.findViewById<RatingBar>(R.id.ratingBar)
                                val notes = note.text.toString()
                                reviewModel.comment = notes
                                reviewModel.productId = productId1
                                reviewModel.storeId = storeId
                                reviewModel.user_id = userId1
                                reviewModel.rate = ratingBar.rating.toInt()
                                when {
                                    ratingBar.rating == 0f -> {
                                        Toasty.error(
                                            activity,
                                            R.string.please_fill_rate,
                                            Toast.LENGTH_SHORT,
                                            true
                                        ).show()
                                        YoYo.with(Techniques.Shake).playOn(ratingBar)
                                        ratingBar.requestFocus()
                                    }
                                    note.text.toString().isEmpty() -> {
                                        note.requestFocus()
                                        note.error = getString(R.string.please_fill_comment)
                                    }
                                    else -> {
                                        addComment(v, reviewModel)
                                    }
                                }
                            }
                        }
                    }
                    val cancelClick: AddRateDialog.Click = object : AddRateDialog.Click() {
                        override fun click() {
                            addCommentDialog!!.dismiss()
                        }
                    }
                    addCommentDialog = AddRateDialog(
                        activity,
                        getString(R.string.add_comment),
                        R.string.ok,
                        R.string.cancel_label,
                        okClick,
                        cancelClick
                    )
                    addCommentDialog!!.show()
                }
            }
            true
        }
        binding.cartBut.setOnClickListener { view1 ->
            if (!UtilityApp.isLogin()) {
                val checkLoginDialog = CheckLoginDialog(
                    activity,
                    R.string.LoginFirst,
                    R.string.to_add_cart,
                    R.string.ok,
                    R.string.cancel_label,
                    null,
                    null
                )
                checkLoginDialog.show()
            } else {
                val message: String
                val cartId1 = selectedProductBarcode?.cartId?:0
                if (cartId1 > 0) {
                    val count = binding.productCartQTY.text.toString().toInt()
                    val stock = selectedProductBarcode?.stockQty?:0
                    val userId = UtilityApp.getUserData()?.id?:0
                    val storeId = localModel?.cityId ?.toInt()?:Constants.default_storeId.toInt()
                    val productId = productModel?.id ?:0
                    val cartId = selectedProductBarcode!!.cartId
                    val limit = selectedProductBarcode!!.limitQty
                    Log.i("limit", "Log limit  $limit")
                    Log.i("limit", "Log limit  $limit")
                    Log.i("stock", "Log cartId  $cartId")
                    if (limit == 0) {
                        if (count + 1 <= stock) {
                            updateCart(
                                view1,
                                productId,
                                selectedProductBarcode?.id?:0,
                                count + 1,
                                userId,
                                storeId,
                                cartId,
                                "quantity"
                            )
                        } else {
                            message = getString(R.string.stock_empty)
                            GlobalData.errorDialogWithButton(activity, getString(R.string.error), message)
                        }
                    } else {
                        if (count + 1 <= stock && count + 1 <= limit) {
                            updateCart(
                                view1,
                                productId,
                                selectedProductBarcode?.id?:0,
                                count + 1,
                                userId,
                                storeId,
                                cartId,
                                "quantity"
                            )
                        } else {
                            message = if (count + 1 > stock) {
                                getString(R.string.stock_empty)
                            } else if (stock == 0) {
                                getString(R.string.stock_empty)
                            } else {
                                getString(R.string.limit) + "" + limit
                            }
                            GlobalData.errorDialogWithButton(activity, getString(R.string.error), message)
                        }
                    }
                } else {
                    val count = selectedProductBarcode?.cartQuantity?:0
                    if (UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) {
                        val userId = UtilityApp.getUserData().id
                        val storeId = localModel?.cityId?.toInt()?:Constants.default_storeId.toInt()
                        val productId = productModel?.id?:0
                        val stock = selectedProductBarcode?.stockQty?:0
                        val limit = selectedProductBarcode?.limitQty?:0
                        Log.i("limit", "Log limit  $limit")
                        Log.i("stock", "Log stock  $stock")
                        if (limit == 0) {
                            if (count + 1 <= stock) {
                                addToCart(
                                    productId,
                                    selectedProductBarcode!!.id,
                                    count + 1,
                                    userId,
                                    storeId
                                )
                            } else {
                                message = getString(R.string.stock_empty)
                                GlobalData.errorDialogWithButton(
                                    activity,
                                    getString(R.string.error),
                                    message
                                )
                            }
                        } else {
                            if (count + 1 <= stock && count + 1 <= limit) {
                                addToCart(
                                    productId,
                                    selectedProductBarcode!!.id,
                                    count + 1,
                                    userId,
                                    storeId
                                )
                            } else {
                                message = if (count + 1 > stock) {
                                    getString(R.string.stock_empty)
                                } else {
                                    getString(R.string.limit) + "" + limit
                                }
                                GlobalData.errorDialogWithButton(
                                    activity,
                                    getString(R.string.error),
                                    message
                                )
                            }
                        }
                    }
                }
            }
        }
        binding.plusCartBtn.setOnClickListener { v ->
            if (!UtilityApp.isLogin()) {
                val checkLoginDialog = CheckLoginDialog(
                    activity,
                    R.string.LoginFirst,
                    R.string.to_add_cart,
                    R.string.ok,
                    R.string.cancel_label,
                    null,
                    null
                )
                checkLoginDialog.show()
            } else {
                if (UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) {
                    var message = ""
                    val count = binding.productCartQTY.text.toString().toInt()
                    val stock = selectedProductBarcode?.stockQty?:0
                    val userId = UtilityApp.getUserData()?.id ?:0
                    val storeId = localModel?.cityId?.toInt()?:Constants.default_storeId.toInt()
                    val productId = productModel?.id?:0
                    val cartId = selectedProductBarcode?.cartId?:0
                    val limit = selectedProductBarcode?.limitQty?:0
                    Log.i("limit", "Log limit  $limit")
                    Log.i("limit", "Log limit  $limit")
                    Log.i("stock", "Log cartId  $cartId")
                    if (cartId > 0) {
                        // increase cart quantity
                        if (limit == 0) {
                            if (count + 1 <= stock) {
                                updateCart(
                                    v,
                                    productId,
                                    selectedProductBarcode!!.id,
                                    count + 1,
                                    userId,
                                    storeId,
                                    cartId,
                                    "quantity"
                                )
                            } else {
                                message = getString(R.string.stock_empty)
                                GlobalData.errorDialogWithButton(
                                    activity, getString(R.string.error),
                                    message
                                )
                            }
                        } else {
                            if (count + 1 <= stock && count + 1 <= limit) {
                                updateCart(
                                    v,
                                    productId,
                                    selectedProductBarcode?.id?:0,
                                    count + 1,
                                    userId,
                                    storeId,
                                    cartId,
                                    "quantity"
                                )
                            } else {
                                message = if (count + 1 > stock) {
                                    getString(R.string.stock_empty)
                                } else if (stock == 0) {
                                    getString(R.string.stock_empty)
                                } else {
                                    getString(R.string.limit) + "" + limit
                                }
                                GlobalData.errorDialogWithButton(
                                    activity, getString(R.string.error),
                                    message
                                )
                            }
                        }
                    } else {
                        // add product to cart for first time
                        checkProductToAdd()
                    }
                }
            }
        }
        binding.minusCartBtn.setOnClickListener { v ->
            if (!UtilityApp.isLogin()) {
                val checkLoginDialog = CheckLoginDialog(
                    activity,
                    R.string.LoginFirst,
                    R.string.to_add_cart,
                    R.string.ok,
                    R.string.cancel_label,
                    null,
                    null
                )
                checkLoginDialog.show()
            } else {
                val count = binding.productCartQTY.text.toString().toInt()
                val userId = UtilityApp.getUserData()?.id ?:0
                val storeId = localModel?.cityId?.toInt()?:Constants.default_storeId.toInt()
                val productId = productModel?.id?:0
                val cartId = selectedProductBarcode?.cartId?:0
                updateCart(
                    v,
                    productId,
                    selectedProductBarcode!!.id,
                    count - 1,
                    userId,
                    storeId,
                    cartId,
                    "quantity"
                )
            }
        }
        binding.deleteCartBtn.setOnClickListener { v ->
            val userId = UtilityApp.getUserData().id
            val storeId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
            val productId = productModel?.id?:0
            val cartId = selectedProductBarcode?.cartId?:0
            deleteCart(v, productId, selectedProductBarcode?.id?:0, cartId, userId, storeId)
        }
    }

    private val intentExtra: Unit
        private get() {
            val bundle = intent.extras
            if (bundle != null) {
                isNotify = bundle.getBoolean(Constants.isNotify, false)
                fromBrousher = bundle.getBoolean(Constants.FROM_BROSHER)
                if (fromBrousher) {
                    productId1 = bundle.getString(Constants.product_id)!!.toInt()
                } else {
                    val productModel = bundle.getSerializable(Constants.DB_productModel) as ProductModel?
                    if (productModel != null) {
                        productId1 = productModel?.id?:0
                        productName = if (UtilityApp.getLanguage() == Constants.Arabic) {
                            productModel.gethName()
                        } else {
                            productModel.name
                        }
                        binding.productNameTv.text = productName
                    }
                }
            }
            getSingleProduct(countryId, cityId, productId1, userId1.toString())
        }

    private fun checkProductToAdd() {
        var message = ""
        val count = selectedProductBarcode?.cartQuantity?:0
        val userId = UtilityApp.getUserData().id
        val storeId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        val productId = productModel?.id ?:0
        val stock = selectedProductBarcode?.stockQty?:0
        val limit = selectedProductBarcode?.limitQty?:0
        Log.i("limit", "Log limit  $limit")
        Log.i("stock", "Log stock  $stock")
        if (limit == 0) {
            if (count + 1 <= stock) {
                addToCart(productId, selectedProductBarcode!!.id, count + 1, userId, storeId)
            } else {
                message = getString(R.string.stock_empty)
                GlobalData.errorDialogWithButton(
                    activity, getString(R.string.error),
                    message
                )
            }
        } else {
            if (count + 1 <= stock && count + 1 <= limit) {
                addToCart(productId, selectedProductBarcode!!.id, count + 1, userId, storeId)
            } else {
                message = if (count + 1 > stock) {
                    getString(R.string.stock_empty)
                } else {
                    getString(R.string.limit) + "" + limit
                }
                GlobalData.errorDialogWithButton(
                    activity, getString(R.string.error),
                    message
                )
            }
        }
    }

    fun initReviewAdapter() {
        reviewAdapter = ReviewAdapter(activity, reviewList)
        binding.reviewRecycler.adapter = reviewAdapter
        reviewAdapter?.notifyDataSetChanged()
    }


    private fun getSingleProduct(country_id: Int, city_id: Int, product_id: Int, user_id: String?) {
        AnalyticsHandler.ViewItem(product_id, currency, 0.0)
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.CartLy.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                val result = obj as ProductDetailsModel?
                var message: String? = getString(R.string.fail_to_get_data)
                binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                if (func == Constants.ERROR) {
                    if (result != null) {
                        message = result.message
                    }
                    binding.dataLY.visibility = View.GONE
                    binding.cartBut.visibility = View.VISIBLE
                    binding.noDataLY.noDataLY.visibility = View.GONE
                    binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                    binding.CartLy.visibility = View.GONE
                    binding.failGetDataLY.failTxt.text = message
                } else if (func == Constants.FAIL) {
                    binding.dataLY.visibility = View.GONE
                    binding.noDataLY.noDataLY.visibility = View.GONE
                    binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                    binding.failGetDataLY.failTxt.text = message
                    binding.CartLy.visibility = View.GONE
                    binding.cartBut.visibility = View.GONE
                } else if (func == Constants.NO_CONNECTION) {
                    binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                    binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection)
                    binding.failGetDataLY.noInternetIv.visibility = View.VISIBLE
                    binding.CartLy.visibility = View.GONE
                    binding.dataLY.visibility = View.GONE
                } else {
                    if (IsSuccess) {
                        if (result!!.data != null && result.data.size > 0) {
                            binding.dataLY.visibility = View.VISIBLE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                            productModel = result.data[0]
                            binding.CartLy.visibility = View.VISIBLE
                            categoryId = productModel?.categoryId ?: 0
                            Log.i(
                                javaClass.simpleName,
                                "Log getSingleProduct categoryId $categoryId"
                            )
                            productName = if (UtilityApp.getLanguage() == Constants.Arabic) {
                                productModel?.gethName()
                            } else {
                                productModel?.getName()
                            }
                            binding.productNameTv.text = productName
                            if (productModel?.getDescription() != null && productModel?.gethDescription() != null) {
                                if (UtilityApp.getLanguage() == Constants.Arabic) {
                                    binding.productDesc1Tv.text =
                                        Html.fromHtml(productModel?.gethDescription())
                                } else {
                                    binding.productDesc1Tv.text =
                                        Html.fromHtml(productModel?.getDescription())
                                }
                            }


//                        sliderList = productModel.getImages();
                            val list = productName?.split(" ")?.toTypedArray()
                            filter = list?.get(0)
                            Log.i(javaClass.simpleName, "Log  productName $productName")
                            Log.i(javaClass.simpleName, "Log search filter $filter")
                            binding.ratingBar.rating = productModel?.rate?.toFloat() ?: 0f

                            if (productModel?.images?.size ?: 0 > 0) {
                                setupViewPager(binding.viewPager)
                                binding.pager.setViewPager(binding.viewPager)
                            }

                            if (productModel?.productBarcodes != null && productModel?.productBarcodes?.size ?: 0 > 0) {
                                selectedProductBarcode = productModel?.firstProductBarcodes
                                for (i in productModel?.productBarcodes!!.indices) {
                                    val productBarcode1 = productModel!!.productBarcodes?.get(i)

                                    if (selectedProductPos == 0 && productBarcode1?.cartId != 0) {
                                        selectedProductBarcode = productBarcode1;
                                        selectedProductPos = i;
                                    }
                                }
                                initProductData()
                            }
                            if (productModel?.productBarcodes != null && productModel?.productBarcodes?.size ?: 0 > 0) {
                                initOptionAdapter()
                                binding.productOptionTv.visibility = View.VISIBLE
                                binding.productOptionRv.visibility = View.VISIBLE
                            } else {
                                binding.productOptionTv.visibility = View.GONE
                                binding.productOptionRv.visibility = View.GONE
                            }
                            isFavorite = productModel?.isFavourite ?: false
                            if (productModel != null && isFavorite) {
                                binding.favBut.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        activity,
                                        R.drawable.favorite_icon
                                    )
                                )
                            } else {
                                binding.favBut.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        activity,
                                        R.drawable.empty_fav
                                    )
                                )
                            }
                            suggestedProduct
                            getReviews(product_id, storeId)
                        } else {
                            binding.dataLY.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.VISIBLE
                        }
                    } else {
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                        binding.cartBut.visibility = View.GONE
                    }
                }
            }

        }).GetSingleProduct(country_id, city_id, product_id, user_id)
    }

    private fun addToFavorite(v: View, productId: Int, userId: Int, storeId: Int) {
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (func == Constants.ERROR) {
                        GlobalData.errorDialogWithButton(
                            activity, getString(R.string.error),
                            getString(R.string.fail_to_add_favorite)
                        )
                    } else if (func == Constants.FAIL) {
                        GlobalData.errorDialogWithButton(
                            activity, getString(R.string.error),
                            getString(R.string.fail_to_add_favorite)
                        )
                    } else {
                        if (IsSuccess) {
                            AnalyticsHandler.AddToWishList(productId, currency, productId.toDouble())
                            binding.favBut.setImageDrawable(
                                ContextCompat.getDrawable(
                                    activity,
                                    R.drawable.favorite_icon
                                )
                            )
                            Toasty.success(activity, R.string.success_add, Toast.LENGTH_SHORT, true).show()
                            isFavorite = true
                        } else {
                            GlobalData.errorDialogWithButton(
                                activity, getString(R.string.error),
                                getString(R.string.fail_to_add_favorite)
                            )
                        }
                    }
                }

            }).addToFavoriteHandle(userId, storeId, productId)
    }

    private fun removeFromFavorite(view: View, productId: Int, userId: Int, storeId: Int) {
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (func == Constants.ERROR) {
                        GlobalData.errorDialogWithButton(
                            activity, getString(R.string.error),
                            getString(R.string.fail_to_remove_favorite)
                        )
                    } else if (func == Constants.FAIL) {
                        GlobalData.errorDialogWithButton(
                            activity, getString(R.string.error),
                            getString(R.string.fail_to_remove_favorite)
                        )
                    } else {
                        if (IsSuccess) {
                            binding.favBut.setImageDrawable(
                                ContextCompat.getDrawable(
                                    activity,
                                    R.drawable.empty_fav
                                )
                            )
                            isFavorite = false
                            Toasty.success(activity, R.string.success_delete, Toast.LENGTH_SHORT, true).show()
                        } else {
                            GlobalData.errorDialogWithButton(
                                activity, getString(R.string.error),
                                getString(R.string.fail_to_remove_favorite)
                            )
                        }
                    }
                }

            }).deleteFromFavoriteHandle(userId, storeId, productId)
    }

    val suggestedProduct: Unit
        get() {
            productList!!.clear()
            binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
            binding.dataLY.visibility = View.GONE
            binding.noDataLY.noDataLY.visibility = View.GONE
            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
            DataFeacher(false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    binding.dataLY.visibility = View.VISIBLE
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
                    } else {
                        if (IsSuccess) {
                            if (result!!.data != null && result.data.size > 0) {
                                binding.dataLY.visibility = View.VISIBLE
                                binding.noDataLY.noDataLY.visibility = View.GONE
                                binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                productList = result.data
                                initProductsAdapter()
                            } else {
                                binding.noOffers.text = getString(R.string.no_products)
                                binding.noOffers.visibility = View.VISIBLE
                            }
                        }
                    }
                }

            }).getFavorite(kind_id,sortType,categoryId, countryId, cityId, userId1.toString(), filter, 0, 0, 12)
        }

    private fun initProductsAdapter() {
        productOfferAdapter = SuggestedProductAdapter(activity, productList, this, 0)
        binding.offerRecycler.adapter = productOfferAdapter
    }

    private fun initOptionAdapter() {
        val productOptionAdapter = ProductOptionAdapter(
            activity, productModel!!.productBarcodes
        ) { obj: Any?, func: String?, IsSuccess: Boolean ->
            selectedProductBarcode = obj as ProductBarcode?
            initProductData()
        }
        binding.productOptionRv.adapter = productOptionAdapter
    }

    /*
       private void addToCart(int productId, int product_barcode_id, int quantity, int userId, int storeId) {
        if(quantity>0){

        }
          else {
            Toast(getString(R.string.quanity_wrong));
        }
     */
    private fun addToCart(productId: Int, product_barcode_id: Int, quantity: Int, userId: Int, storeId: Int) {
        if (quantity > 0) {
            DataFeacher(false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    val result = obj as CartProcessModel
                    if (IsSuccess) {
                        val cartId = result.id
                        selectedProductBarcode!!.cartId = cartId
                        selectedProductBarcode!!.cartQuantity = quantity
                        productModel!!.productBarcodes[selectedProductPos] = selectedProductBarcode
                        AnalyticsHandler.AddToCart(cartId, currency, quantity.toDouble())
                        binding.productCartQTY.text = quantity.toString()
                        if (quantity == 1) {
                            binding.deleteCartBtn.visibility = View.VISIBLE
                            binding.minusCartBtn.visibility = View.GONE
                        } else {
                            binding.minusCartBtn.visibility = View.VISIBLE
                            binding.deleteCartBtn.visibility = View.GONE
                        }
                        UtilityApp.updateCart(1, productList!!.size)
                    } else {
                        GlobalData.errorDialogWithButton(
                            activity, getString(R.string.error),
                            getString(R.string.fail_to_add_cart)
                        )
                    }
                }

            }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId)
        } else {
            Toast(getString(R.string.quanity_wrong))
        }
    }

    private fun initSnackBar(message: String, viewBar: View) {
        Toasty.success(activity, message, Toast.LENGTH_SHORT, true).show()
    }

    private fun deleteCart(
        v: View,
        productId: Int,
        product_barcode_id: Int,
        cart_id: Int,
        userId: Int,
        storeId: Int
    ) {
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    initSnackBar(getString(R.string.success_delete_from_cart), v)
                    UtilityApp.updateCart(1, productList!!.size)
                    selectedProductBarcode!!.cartQuantity = 0
                    selectedProductBarcode!!.cartId = 0
                    productModel!!.productBarcodes[selectedProductPos] = selectedProductBarcode
                    //                int quantity = selectedProductBarcode.getCartQuantity();
                    AnalyticsHandler.RemoveFromCart(cart_id, currency, 0.0)
                    binding.productCartQTY.text = 1.toString()
                    binding.deleteCartBtn.visibility = View.GONE
                    binding.minusCartBtn.visibility = View.VISIBLE
                    binding.plusCartBtn.visibility = View.VISIBLE
                } else {
                    GlobalData.errorDialogWithButton(
                        activity, getString(R.string.error),
                        getString(R.string.fail_to_delete_cart)
                    )
                }
            }

        }).deleteCartHandle(productId, product_barcode_id, cart_id, userId, storeId)
    }

    private fun updateCart(
        v: View,
        productId: Int,
        product_barcode_id: Int,
        quantity: Int,
        userId: Int,
        storeId: Int,
        cart_id: Int,
        update_quantity: String
    ) {
        if (quantity > 0) {
            DataFeacher(false,
                object : DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        if (IsSuccess) {
                            binding.productCartQTY.text = quantity.toString()

                            // initSnackBar(getString(R.string.success_to_update_cart), v);
                            selectedProductBarcode!!.cartQuantity = quantity
                            productModel!!.productBarcodes[selectedProductPos] = selectedProductBarcode
                            if (quantity > 0) {
                                binding.productCartQTY.text = quantity.toString()
                                if (quantity == 1) {
                                    binding.deleteCartBtn.visibility = View.VISIBLE
                                    binding.minusCartBtn.visibility = View.GONE
                                } else {
                                    binding.minusCartBtn.visibility = View.VISIBLE
                                    binding.deleteCartBtn.visibility = View.GONE
                                }
                            }
                        } else {
                            GlobalData.errorDialogWithButton(
                                activity, getString(R.string.error),
                                getString(R.string.fail_to_update_cart)
                            )
                        }
                    }

                }).updateCartHandle(
                productId,
                product_barcode_id,
                quantity,
                userId,
                storeId,
                cart_id,
                update_quantity
            )
        } else {
            Toast(getString(R.string.quanity_wrong))
        }
    }

    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    public void onMessageEvent(@NotNull MessageEvent event) {
    //
    //
    //        if (event.type.equals(MessageEvent.TYPE_main)) {
    //            binding.backBtn.setOnClickListener(view -> {
    //                Intent intent = new Intent(getActiviy(), Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
    //                startActivity(intent);
    //            });
    //
    //        }
    //
    //
    //    }
    fun getReviews(product_id: Int, storeId: Int) {
        reviewList!!.clear()
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    val result = obj as ResultAPIModel<ArrayList<ReviewModel>?>
                    if (result.data != null && result.data?.size?:0 > 0) {
                        reviewList = result.data
                        binding.productReviewTv.visibility = View.VISIBLE
                        binding.reviewRecycler.visibility = View.VISIBLE
                        initReviewAdapter()
                    } else {
                        binding.productReviewTv.visibility = View.GONE
                        binding.reviewRecycler.visibility = View.GONE
                    }
                }
            }

        }).getRate(product_id, storeId)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val bundle = intent.extras
        if (bundle != null) {
            val productModel = bundle.getSerializable(Constants.DB_productModel) as ProductModel?
            fromBrousher = bundle.getBoolean(Constants.FROM_BROSHER)
            isNotify = bundle.getBoolean(Constants.isNotify, false)
            if (fromBrousher) {
                productId1 = bundle.getString(Constants.product_id)!!.toInt()
            } else {
                if (productModel != null) {
                    productId1 = productModel.id
                    productName = if (UtilityApp.getLanguage() == Constants.Arabic) {
                        productModel.gethName()
                    } else {
                        productModel.name
                    }
                }
            }
            getSingleProduct(countryId, cityId, productId1, userId1.toString())
            binding.productNameTv.text = productName
        }
    }

    private fun addComment(v: View, reviewModel: ReviewModel) {
        GlobalData.progressDialog(activity, R.string.add_comm, R.string.please_wait_sending)
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                var message = getString(R.string.fail_add_comment)
                val result = obj as ResultAPIModel<ReviewModel>?
                if (result != null) {
                    message = result.message
                }
                if (func == Constants.ERROR) {
                    GlobalData.errorDialog(activity, R.string.rate_app, message)
                } else if (func == Constants.FAIL) {
                    GlobalData.errorDialog(activity, R.string.rate_app, message)
                } else if (func == Constants.NO_CONNECTION) {
                    GlobalData.errorDialog(
                        activity,
                        R.string.rate_app,
                        getString(R.string.no_internet_connection)
                    )
                } else {
                    if (IsSuccess) {
                        addCommentDialog!!.dismiss()
                        GlobalData.hideProgressDialog()
                        getReviews(productId1, storeId)
                        GlobalData.successDialog(
                            activity,
                            getString(R.string.rate_product),
                            getString(R.string.success_rate_product)
                        )
                    } else {
                        addCommentDialog!!.dismiss()
                        GlobalData.hideProgressDialog()
                        GlobalData.errorDialog(
                            activity,
                            R.string.rate_product,
                            getString(R.string.fail_add_comment)
                        )
                    }
                }
            }

        }).setRate(reviewModel)
    }

    @SuppressLint("SetTextI18n")
    private fun initProductData() {
        val quantity = selectedProductBarcode!!.cartQuantity
        if (quantity > 0) {
            binding.productCartQTY.text = quantity.toString()
            if (quantity == 1) {
                binding.deleteCartBtn.visibility = View.VISIBLE
                binding.minusCartBtn.visibility = View.GONE
            } else {
                binding.minusCartBtn.visibility = View.VISIBLE
                binding.deleteCartBtn.visibility = View.GONE
            }
        } else {
            binding.productCartQTY.text = 1.toString()
        }
        if (selectedProductBarcode?.isSpecial == true) {
            binding.productPriceBeforeTv.background = ContextCompat.getDrawable(
                activity, R.drawable.itlatic_red_line
            )

//            if (selectedProductBarcode.getSpecialPrice() != null) {
            binding.productPriceBeforeTv.text =
                NumberHandler.formatDouble(
                    selectedProductBarcode!!.price.toString().toDouble(),
                    fraction
                ) + " " + currency
            binding.productPriceTv.text =
                NumberHandler.formatDouble(
                    selectedProductBarcode!!.specialPrice.toString().toDouble(),
                    fraction
                ) + " " + currency
            val discount = (selectedProductBarcode!!.price.toString()
                .toDouble().minus(selectedProductBarcode!!.specialPrice.toString().toDouble()))
                .div(selectedProductBarcode!!.price.toString().toDouble()) * 100


            val df = DecimalFormat("#")
            val discountStr = df.format(discount)
//            binding.discountTv.text = NumberHandler.arabicToDecimal(discountStr) + " % " + "OFF"


            val originalPrice = selectedProductBarcode?.price
            val specialPrice = selectedProductBarcode?.specialPrice?:0.0
            val discountValue = originalPrice?.minus(specialPrice)

            val discountPercent = (discountValue?.div(originalPrice))?.times(100)

            binding.discountTv.text = NumberHandler.arabicToDecimal(discountPercent?.toInt().toString() .plus(" % " + "OFF"));

            // this is different by seconds
            var diff =
                DateHandler.GetDateOnlyLong(selectedProductBarcode!!.endOffer) - DateHandler.GetDateOnlyLong(
                    DateHandler.GetDateNowString()
                )
            println("Log diff $diff")
            val day = (diff / (24 * 60 * 60)).toInt()
            diff %= (24 * 60 * 60)
            println("Log day $day")
            println("Log diff $diff")
            val hour = (diff / (60 * 60)).toInt()
            diff %= (60 * 60)
            println("Log hour $hour")
            println("Log diff $diff")
            val minutes = (diff / 60).toInt()
            println("Log minutes $minutes")
            var formattedOfferTime = ""
            if (day > 0) formattedOfferTime += day.toString() + " " + getString(R.string.day) + ","
            if (hour > 0) formattedOfferTime += hour.toString() + " " + getString(R.string.hour) + ","
            if (minutes > 0) formattedOfferTime += minutes.toString() + " " + getString(R.string.minute)
            if (formattedOfferTime.endsWith(",")) formattedOfferTime =
                formattedOfferTime.substring(0, formattedOfferTime.length - 1)
            binding.endOfferTv.text = formattedOfferTime

        } else {

            binding.productPriceTv.text =
                NumberHandler.formatDouble(
                    selectedProductBarcode!!.price.toString().toDouble(),
                    fraction
                ) + " " + currency + ""
            binding.productPriceBeforeTv.visibility = View.GONE
            binding.offerLy.visibility = View.GONE
        }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = ViewPagerAdapter(supportFragmentManager)

        for (image in productModel!!.images) {
            val imageBundle = Bundle()
            imageBundle.putString(Constants.KEY_IMAGE_URL, image)
            val imageFragment: Fragment = ImageFragment()
            imageFragment.arguments = imageBundle
            adapter.addFragment(imageFragment, "")
        }
        val list = productName!!.split(" ").toTypedArray()
        val filter = list[0]

        // add suggestions fragment
        val suggestedProductsFragment: Fragment = SuggestedProductsFragment()
        val suggestedBundle = Bundle()
        suggestedBundle.putString(Constants.KEY_FILTER, filter)
        suggestedProductsFragment.arguments = suggestedBundle
        adapter.addFragment(suggestedProductsFragment, "")
        viewPager.adapter = adapter
        viewPager.isSaveEnabled=false

    }

    class ViewPagerAdapter(manager: FragmentManager?) :
            FragmentStatePagerAdapter(manager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    override fun onItemClicked(position: Int, productModel: ProductModel?) {
    }
}