package com.ramez.shopp.activities


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.adapter.OrderProductsAdapter
import com.ramez.shopp.databinding.ActivityInvoiceInfoBinding
import es.dmoral.toasty.Toasty


class InvoiceInfoActivity : ActivityBase() {
    var currency = "BHD"
    lateinit var binding: ActivityInvoiceInfoBinding

    //    List<OrderItemDetail> list;
    var orderModel: ItemDetailsModel? = null
    private var orderProductsAdapter: OrderProductsAdapter? = null
    private var orderId = 0
    var storeId1 = 0
    var userId = 0
    var fraction = 2
    var localModel: LocalModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceInfoBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

//        list = new ArrayList<>();
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
        currency = localModel?.currencyCode ?:Constants.CURRENCY
        fraction = localModel?.fractional ?:Constants.three
        if (UtilityApp.isLogin()) {
            userId = UtilityApp.getUserData().id
        }
        storeId1 = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        intentData
        setTitle(R.string.invoice_details)
        binding.reOrderBut.setOnClickListener { view1 ->
            orderModel?.orderItemDetails?.indices?.forEach { i ->
                val orderProductsDM = orderModel?.orderItemDetails!![i]
                val count = orderProductsDM.quantity
                val userId = UtilityApp.getUserData().id
                val storeId = localModel?.cityId?.toInt()?:Constants.default_storeId.toInt()
                val productId = orderProductsDM.productId
                val productBarcodeId = orderProductsDM.productBarcodeId
                addToCart(view1, i, productId, productBarcodeId, count, userId, storeId)
            }
        }
    }

    //  list = orderModel.getOrderProductsDMS();
    private val intentData: Unit
        get() {
            val bundle = intent.extras
            if (bundle != null) {
                val orderNewModel = bundle.getSerializable(Constants.ORDER_MODEL) as OrderNewModel?
                orderId = orderNewModel?.id?:0
                //  list = orderModel.getOrderProductsDMS();
                getProductList(orderId, userId, storeId1, Constants.user_type)
            }
        }

    @SuppressLint("SetTextI18n")
    fun initAdapter() {
        val linearLayoutManager = LinearLayoutManager(activity)
        binding.productsRecycler.layoutManager = linearLayoutManager
        orderProductsAdapter = OrderProductsAdapter(activity, orderModel?.orderItemDetails)
        binding.productsRecycler.adapter = orderProductsAdapter
        if (orderModel?.totalAmount?:0.0 > 0) {
            binding.tvTotalPrice.text =
                NumberHandler.formatDouble(orderModel?.totalAmount, fraction) + " " + currency
        }
    }

    private fun addToCart(
        v: View,
        i: Int,
        productId: Int,
        product_barcode_id: Int,
        quantity: Int,
        userId: Int,
        storeId: Int
    ) {
        if (quantity > 0) {
            DataFeacher(false,object :
                DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        UtilityApp.updateCart(1, orderModel?.orderItemDetails?.size?:0)
                        if (i == (orderModel?.orderItemDetails?.size ?: 0) - 1) {
                            initSnackBar(" " + resources.getString(R.string.success_to_update_cart), v)
                        }
                    } else {
                        initSnackBar(getString(R.string.fail_to_update_cart), v)
                    }                }


            }).addCartHandle(productId, product_barcode_id, quantity, userId, storeId)
        } else {
            Toast(getString(R.string.quanity_wrong))
        }
    }

    private fun initSnackBar(message: String, viewBar: View) {
        Toasty.success(activity, message, Toast.LENGTH_SHORT, true).show()
    }

    fun getProductList(order_id: Int, user_id: Int, store_id: Int, type: String?) {

//        orderModel.getOrderItemDetails().clear();
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<ItemDetailsModel?>?
                var message: String? = getString(R.string.fail_to_get_data)
                binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                if (func == Constants.ERROR) {
                    if (result?.message != null) {
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
                        if (result?.data != null && result.data?.orderItemDetails != null && result.data?.orderItemDetails?.size?:0 > 0) {
                            binding.dataLY.visibility = View.VISIBLE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                            orderModel = result.data
                            //                        list = result.data.getOrderItemDetails();
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

        }).getOrderDetails(order_id, user_id, store_id, type)
    }
}