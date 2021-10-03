package com.ramez.shopp.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.*
import com.ramez.shopp.Dialogs.CheckLoginDialog
import com.ramez.shopp.Dialogs.ConfirmDialog
import com.ramez.shopp.Dialogs.EmptyCartDialog
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.activities.ProductDetailsActivity
import com.ramez.shopp.adapter.CartAdapter
import com.ramez.shopp.adapter.CartAdapter.OnCartItemClicked
import com.ramez.shopp.databinding.FragmentCartBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.annotations.NotNull
import java.util.*


class CartFragment : FragmentBase(), OnCartItemClicked {
    var cartList: ArrayList<CartModel>? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var currency = "BHD"
    var fraction = 2
    var storeId = 0
    var userId = 0
    var user: MemberModel? = null
    var localModel: LocalModel? = null
    var isLogin = false
    var productsSize = 0
    var total = ""
    var totalSavePrice: String? = null
    var productSize = 0
    lateinit var binding: FragmentCartBinding
    private var cartAdapter: CartAdapter? = null
    private var emptyCartDialog: EmptyCartDialog? = null
    private var minimum_order_amount = 0
    private val delivery_charges = 0.0
    private var cartResultModel: CartResultModel? = null
    private var activity: Activity? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    var cartNumber = 0

    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        val view: View = binding.root
        cartList = ArrayList()
        isLogin = UtilityApp.isLogin()
        activity = getActivity()
        localModel = if (UtilityApp.getLocalData() != null)
            UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
            activityy
        )
        currency = localModel?.currencyCode ?: Constants.BHD
        fraction = localModel?.fractional ?: Constants.three
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activityy)
        user = UtilityApp.getUserData()


        if (!UtilityApp.isLogin()) {
            binding.dataLY.visibility = View.GONE
            binding.noDataLY.noDataLY.visibility = View.GONE
            binding.contBut.visibility = View.GONE
            showLoginDialog()
        } else {
            storeId =
                localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
            userId = user?.id ?: 0

            linearLayoutManager = LinearLayoutManager(activityy)
            binding.cartRecycler.layoutManager = linearLayoutManager
            binding.cartRecycler.setHasFixedSize(true)
            binding.cartRecycler.animation = null
            binding.cartRecycler.itemAnimator = null

            getCarts(storeId, userId)
        }
        initListeners()
        return view
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun initAdapter() {
        cartAdapter = CartAdapter(activityy, cartList, this,
            { obj: Any, func: String?, IsSuccess: Boolean ->
                val cartProcessModel = obj as CartProcessModel
                productSize = cartProcessModel.cartCount
                if (cartProcessModel.cartCount == 0) {
                    getCarts(storeId, userId)
                } else {
                    total = NumberHandler.formatDouble(
                        cartProcessModel.total,
                        fraction
                    )
                    binding.totalTv.text = "$total $currency"
                    if (cartProcessModel.totalSavePrice == 0.0) {
                        binding.savePriceLy.visibility = View.GONE
                    } else {
                        binding.savePriceLy.visibility = View.VISIBLE
                    }
                    totalSavePrice =
                        NumberHandler.formatDouble(
                            cartProcessModel.totalSavePrice,
                            fraction
                        )
                    binding.saveText.text = "$totalSavePrice $currency"
                    if (cartProcessModel.total >= minimum_order_amount) {
                        binding.tvFreeDelivery.text =
                            getString(R.string.getFreeDelivery)
                    } else {
                        val total_price =
                            minimum_order_amount - cartProcessModel.total
                        val Add_more = activityy.getString(R.string.Add_more)
                        val freeStr = activityy.getString(R.string.get_Free)
                        binding.tvFreeDelivery.text = (Add_more + " " +
                                NumberHandler.formatDouble(
                                    total_price,
                                    fraction
                                )
                                + " " + currency + " " + freeStr)
                    }
                }
            })
        binding.cartRecycler.adapter = cartAdapter
        productsSize = cartList?.size ?: 0
        total = NumberHandler.formatDouble(
            cartAdapter!!.calculateSubTotalPrice(),
            fraction
        )
        totalSavePrice = NumberHandler.formatDouble(
            cartAdapter!!.calculateSavePrice(),
            fraction
        )
        binding.totalTv.text = "$total $currency"
        if (cartAdapter!!.calculateSavePrice() == 0.0) {
            binding.savePriceLy.visibility = View.GONE
        } else {
            binding.savePriceLy.visibility = View.VISIBLE
        }
        binding.saveText.text = "$totalSavePrice $currency"
        binding.cartRecycler.post { cartAdapter!!.notifyDataSetChanged() }
    }

    override fun onCartItemClicked(cartDM: CartModel) {
        val intent = Intent(activityy, ProductDetailsActivity::class.java)
        val productModel = ProductModel()
        productModel.id = cartDM.productId
        productModel.name = cartDM.name
        productModel.sethName(cartDM.hProductName)
        intent.putExtra(Constants.DB_productModel, productModel)
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")


    fun getCarts(storeId: Int, userId: Int) {

        Log.i(
            javaClass.name,
            "Log GetCarts countryName ${localModel?.shortname} "
        )

        cartList!!.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        binding.contBut.visibility = View.GONE

        DataFeacher(
            true, object : DataFetcherCallBack {
                override fun Result(
                    obj: Any?,
                    func: String?,
                    IsSuccess: Boolean
                ) {
                    if (isVisible) {
                        cartResultModel = obj as CartResultModel?

                        var message: String? =
                            getString(R.string.fail_to_get_data)
                        binding.loadingProgressLY.loadingProgressLY.visibility =
                            View.GONE

                        if (func == Constants.ERROR) {
                            if (cartResultModel != null) {
                                message = cartResultModel!!.message
                            }
                            binding.dataLY.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility =
                                View.VISIBLE
                            binding.failGetDataLY.failTxt.text = message
                        } else if (func == Constants.FAIL) {
                            binding.dataLY.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility =
                                View.VISIBLE
                            binding.failGetDataLY.failTxt.text = message
                        } else if (func == Constants.NO_CONNECTION) {
                            binding.failGetDataLY.failGetDataLY.visibility =
                                View.VISIBLE
                            binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection)
                            binding.failGetDataLY.noInternetIv.visibility =
                                View.VISIBLE
                            binding.dataLY.visibility = View.GONE
                        } else {

                            if (IsSuccess) {

                                if (cartResultModel?.data != null && cartResultModel?.data!!.cartData != null
                                        && cartResultModel!!.data.cartData.size > 0 &&
                                        cartResultModel!!.data.cartCount > 0
                                ) {

                                    binding.dataLY.visibility = View.VISIBLE
                                    binding.noDataLY.noDataLY.visibility =
                                        View.GONE
                                    binding.failGetDataLY.failGetDataLY.visibility =
                                        View.GONE
                                    cartList = cartResultModel!!.data.cartData
                                    binding.contBut.visibility = View.VISIBLE
                                    val data: Data = cartResultModel!!.data
                                    minimum_order_amount =
                                        data.minimumOrderAmount
                                    cartNumber =
                                        cartResultModel!!.data.cartCount
                                    localModel!!.minimum_order_amount =
                                        minimum_order_amount

                                    UtilityApp.setLocalData(localModel)
                                    UtilityApp.setCartCount(cartNumber)
                                    initAdapter()
                                    binding.cartRecycler.post { cartAdapter!!.notifyDataSetChanged() }
                                    Log.i(
                                        javaClass.simpleName,
                                        "Log  minimum_order_amount $minimum_order_amount"
                                    )
                                    Log.i(
                                        javaClass.simpleName,
                                        "Log deliveryFees $delivery_charges"
                                    )
                                    Log.i(
                                        javaClass.simpleName,
                                        "Log total $total"
                                    )
                                    if (delivery_charges >= 0) {
                                        if (cartAdapter!!.calculateSubTotalPrice() >= minimum_order_amount) {
                                            binding.tvFreeDelivery.setText(R.string.getFreeDelivery)
                                        } else {
                                            val total_price =
                                                minimum_order_amount - cartAdapter!!.calculateSubTotalPrice()
                                            binding.tvFreeDelivery.text =
                                                getString(R.string.Add_more) + " " + NumberHandler.formatDouble(
                                                    total_price,
                                                    fraction
                                                ) + " " + currency + " " + getString(
                                                    R.string.get_Free
                                                )
                                        }
                                    } else {
                                        binding.tvFreeDelivery.setText(R.string.getFreeDelivery)
                                    }
                                    AnalyticsHandler.ViewCart(
                                        userId,
                                        currency,
                                        total.toDouble()
                                    )
                                } else {
                                    binding.contBut.visibility = View.GONE
                                    binding.dataLY.visibility = View.GONE
                                    showEmptyCartDialog()
                                }
                            } else {
                                binding.dataLY.visibility = View.GONE
                                binding.noDataLY.noDataLY.visibility = View.GONE
                                binding.failGetDataLY.failGetDataLY.visibility =
                                    View.VISIBLE
                                binding.failGetDataLY.failTxt.text = message
                            }
                        }
                    }
                }
            }
        ).GetCarts(storeId, userId)
    }

    fun showHomeFragment() {

        val bundle = Bundle()
        bundle.putInt(
            Constants.KEY_FRAGMENT_ID,
            R.id.homeButton
        )
        EventBus.getDefault()
            .post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
    }

    private fun showEmptyCartDialog() {
        val okClick: EmptyCartDialog.Click = object : EmptyCartDialog.Click() {
            override fun click() {
                // show home fragment
                showHomeFragment()
//                EventBus.getDefault()
//                    .post(MessageEvent(MessageEvent.TYPE_POSITION, 0))
            }
        }
        val cancelClick: EmptyCartDialog.Click =
            object : EmptyCartDialog.Click() {
                override fun click() {
                    showHomeFragment()
                }
            }
        emptyCartDialog = EmptyCartDialog(
            activityy,
            R.string.please_login,
            R.string.text_login_login,
            R.string.register,
            okClick,
            cancelClick
        )
        emptyCartDialog!!.show()
    }

    private fun showLoginDialog() {
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

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(@NotNull event: MessageEvent?) {
    }

    override fun onResume() {
        super.onResume()
        if (GlobalData.REFRESH_CART) {
            getCarts(storeId, userId)
            GlobalData.REFRESH_CART = false
        }
    }

    private fun goToCompleteOrder() {
        AnalyticsHandler.checkOut("0", currency, total, total)

//        val fragmentManager: FragmentManager = parentFragmentManager
//        val invoiceFragment = InvoiceFragment()
        val bundle = Bundle()
        bundle.putInt(Constants.CART_PRODUCT_COUNT, productsSize)
        bundle.putString(Constants.CART_SUM, total)
        bundle.putDouble(Constants.delivery_charges, delivery_charges)
        bundle.putSerializable(Constants.CART_MODEL, cartResultModel)
//        invoiceFragment.arguments = bundle
//        fragmentManager.beginTransaction()
//            .replace(R.id.mainContainer, invoiceFragment, "InvoiceFragment").commit()
        bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.invoiceFragment)
        EventBus.getDefault()
            .post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
    }

    private fun initListeners() {

        binding.contBut.setOnClickListener {

            AnalyticsHandler.checkOut("0", currency, total, total)
            var message = ""
            var allMessage: String
            val s = StringBuilder()
            var productName = ""
            var productPrice = ""
            var productQuantity = ""
            var canOrder = true
            var lastPosition = 0
            for ((i, cartModel) in cartList?.withIndex() ?: mutableListOf()) {
                GlobalData.progressDialog(
                    activityy,
                    R.string.please_wait_sending,
                    R.string.save_update
                )
//                val cartModel = cartList!![i]
                if (cartModel.quantity > cartModel.productQuantity && !cartModel.isExtra) {
                    message = getString(R.string.outofstock)
                    productName = cartModel.name
                    productPrice = cartModel.productPrice.toString()
                    productQuantity = cartModel.productQuantity.toString()
                    allMessage =
                        message + " " + getString(R.string.product_Name) + " " + productName + ", " + getString(
                            R.string.product_price
                        ) + " " + productPrice + " " + currency + " , " + getString(
                            R.string.product_quan
                        ) + " " + productQuantity
                    s.append(allMessage).append("\n")
                    val product_barcode_id = cartModel.productBarcodeId
                    val userId = UtilityApp.getUserData().id
                    val storeId = localModel!!.cityId.toInt()
                    val productId = cartModel.productId
                    val cart_id = cartModel.id
                    val count = cartModel.productQuantity
                    // updateCart(i, productId, product_barcode_id, count, userId, storeId, cart_id, "quantity");
                    // cartAdapter.updateCart(i, productId, product_barcode_id, count,count,false, userId, storeId, cart_id, "quantity");
                    canOrder = false
                } else if (cartModel.productQuantity == 0 && !cartModel.isExtra) {
                    message = getString(R.string.product_not_Available)
                    productName = cartModel.name
                    allMessage =
                        message + " " + getString(R.string.product_Name) + " " + productName
                    s.append(
                        """
                            $allMessage
                            
                            """.trimIndent()
                    )
                    canOrder = false
                }
                GlobalData.hideProgressDialog()
                if (i == cartList?.lastIndex) {
                    lastPosition = i
                    GlobalData.hideProgressDialog()
                }
            }
            if (canOrder) {
                GlobalData.hideProgressDialog()
                goToCompleteOrder()
            } else {
                if (lastPosition == cartList?.lastIndex) {
                    GlobalData.hideProgressDialog()
                    val click: ConfirmDialog.Click =
                        object : ConfirmDialog.Click() {
                            override fun click() {}
                        }
                    ConfirmDialog(
                        activityy,
                        s.toString() + "" + getString(R.string.please_update_cart),
                        R.string.ok,
                        R.string.cancel_label,
                        click,
                        null,
                        true
                    )
                }
            }
        }

        binding.failGetDataLY.refreshBtn.setOnClickListener {
            getCarts(
                storeId,
                userId
            )
        }
    }
}