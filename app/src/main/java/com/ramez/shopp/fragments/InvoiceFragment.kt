package com.ramez.shopp.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramez.shopp.activities.AddNewAddressActivity
import com.ramez.shopp.activities.AddressActivity
import com.ramez.shopp.activities.OrderCompleteActivity
import com.ramez.shopp.adapter.*
import com.ramez.shopp.adapter.AddressCheckAdapter.OnEditClick
import com.ramez.shopp.adapter.AddressCheckAdapter.OnRadioAddressSelect
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.*
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.databinding.FragmentInvoiceBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class InvoiceFragment : FragmentBase(), OnRadioAddressSelect, AddressCheckAdapter.OnContainerSelect,
    OnEditClick {
    var userId: Int? = 0
    var couponCodeId = "0"
    var deliveryDate: String? = null
    var deliveryTime: String? = null
    var expressDelivery = false
    var deliveryDayAdapter: DeliveryDayAdapter? = null
    var deliveryTimeAdapter: DeliveryTimeAdapter? = null
    var productCheckerAdapter: ProductCheckAdapter? = null
    var paymentList: ArrayList<PaymentModel>? = null
    var deliveryTimesList: List<DeliveryTime>? = null
    var productList: ArrayList<CartModel>? = null
    var payLinearLayoutManager: GridLayoutManager? = null
    var productCheckerList: MutableList<ProductChecker>? = null
    private var toggleButton = false
    var localModel: LocalModel? = null
    var storeId = 0
    var productsSize = 0
    var total: String? = ""
    var currency = "BHD"
    var DayList: MutableList<DeliveryTime>? = null
    var fraction = 2
    var addressList: ArrayList<AddressModel>? = null
    private var paymentAdapter: PaymentAdapter? = null
    private var addressTitle: String? = null
    private var addressFullAddress: String? = null
    private var deliveryFees = 0.0
    private var expressDeliveryCharge = 0.0
    private var hasExpress: Boolean = false
    private var cartResultModel: CartResultModel? = null
    private var minimum_order_amount = 0
    private val datesMap = LinkedHashMap<String, List<DeliveryTime>>()
    private var selectAddressLauncher: ActivityResultLauncher<Intent>? = null
    var selectedPaymentMethod: PaymentModel? = null
    private var addressId = 0
    var deliveryDateId = 0
    var itemNotFoundId = 0

    lateinit var binding: FragmentInvoiceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInvoiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectAddressLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
            initAddressData(
                result.resultCode,
                result.data
            )
        }
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activityy
            )
        paymentList = ArrayList()
        addressList = ArrayList()
        deliveryTimesList = ArrayList()
        DayList = ArrayList()
        productCheckerList = ArrayList()
        fraction = localModel?.fractional ?: Constants.three
        minimum_order_amount = localModel?.minimum_order_amount ?: 0
        storeId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        currency = localModel?.currencyCode ?: Constants.BHD
        userId = UtilityApp.getUserData().id
        payLinearLayoutManager = GridLayoutManager(activityy, 2, RecyclerView.VERTICAL, false)
        binding.paymentRv.layoutManager = payLinearLayoutManager
        binding.paymentRv.setHasFixedSize(true)
        binding.paymentRv.animation = null
        binding.DeliverDayRecycler.setHasFixedSize(true)
        val deliverDayLlm = LinearLayoutManager(activityy)
        deliverDayLlm.orientation = LinearLayoutManager.HORIZONTAL
        binding.DeliverDayRecycler.layoutManager = deliverDayLlm
        binding.DeliverTimeRecycler.setHasFixedSize(true)
        binding.ProductCheckRecycler.setHasFixedSize(true)
        val deliverTimeLlm = LinearLayoutManager(activityy)
        deliverTimeLlm.orientation = LinearLayoutManager.VERTICAL
        binding.DeliverTimeRecycler.layoutManager = deliverTimeLlm
        val checkProductLlm = LinearLayoutManager(activityy)
        checkProductLlm.orientation = LinearLayoutManager.VERTICAL
        binding.ProductCheckRecycler.layoutManager = checkProductLlm
        extraIntent
        getPaymentMethod(storeId)
        getDeliveryTimeList(storeId, userId!!)
        initListener()
    }

    private fun checkDeliveryFees() {
        if (total!!.toDouble() >= minimum_order_amount) {
            deliveryFees = 0.0
            binding.deliveryFees.text = getString(R.string.free)
        } else {
            val total_price = minimum_order_amount - total!!.toDouble()

            binding.freeBut.text = getString(R.string.add).plus(" ")
                .plus(NumberHandler.roundDouble(total_price)).plus(" ")
                .plus(currency).plus(" ")
                .plus(
                    getString(
                        R.string.get_Free
                    )
                )

//                getString(R.string.add) + " " + NumberHandler.roundDouble(total_price)
            //                + " " + currency + " " + getString(
//                    R.string.get_Free )
        }
        if (deliveryFees == 0.0 || deliveryFees == 0.00 || deliveryFees == 0.000) {
            binding.deliveryFees.text = getString(R.string.free)
            binding.freeDelivery.text = getString(R.string.over1)
        } else {
            binding.deliveryFees.text =
                NumberHandler.formatDouble(deliveryFees, localModel!!.fractional).plus(" ")
                    .plus(currency)
            binding.freeDelivery.text =
                getString(R.string.over).plus(" ").plus(minimum_order_amount).plus(" ")
                    .plus(currency).plus(".")


        }
    }

    private val defaultAddress: Unit
        get() {
            if (UtilityApp.getUserData().lastSelectedAddress > 0) {
                addressId = UtilityApp.getUserData().lastSelectedAddress
                Log.i(javaClass.simpleName, "Log addressId$addressId")
                getDeliveryInfo(storeId, addressId, false)
            } else {
                Log.i(javaClass.simpleName, "Log addressId$addressId")
                binding.changeAddressBtu.setText(R.string.choose_address)
            }
        }

    @SuppressLint("SetTextI18n")
    private fun initListener() {
        binding.saveBut.setOnClickListener { view1 ->
//            EventBus.getDefault()
//                .post(MessageEvent(MessageEvent.TYPE_POSITION, 0))
        }


        binding.sendOrder.setOnClickListener {

            if (selectedPaymentMethod == null) {
                Toast(R.string.please_select_payment_method)
                return@setOnClickListener
            }
            if (selectedPaymentMethod!!.shortname != "CC" && addressId == 0) {
                Toast(R.string.choose_address)
                binding.tvFullAddress.isFocusable = true
                binding.tvFullAddress.error = getString(R.string.choose_address)
                return@setOnClickListener
            }
            if (deliveryDateId == 0) {
                Toast(R.string.select_delivery_time)
                return@setOnClickListener
            }
            if (itemNotFoundId == 0) {
                Toast(R.string.check_product)
                return@setOnClickListener
            }


            val orderCall = OrderCall()
            orderCall.user_id = userId!!
            orderCall.store_ID = storeId
            orderCall.address_id = addressId
            orderCall.payment_method = selectedPaymentMethod!!.shortname
            orderCall.coupon_code_id = couponCodeId
            orderCall.delivery_date_id = deliveryDateId
            orderCall.itemNotFoundAction = itemNotFoundId
            orderCall.expressDelivery = expressDelivery
            sendOrder(orderCall)
        }


        binding.changeAddressBtu.setOnClickListener {

            val intent = Intent(activityy, AddressActivity::class.java)
            selectAddressLauncher!!.launch(intent)
        }


        binding.freeBut.setOnClickListener {
            if (deliveryFees > 0) {
                if (total!!.toDouble() >= minimum_order_amount) {
                    Toast(getString(R.string.getFreeDelivery))
                } else {
                    val categoryFragment = CategoryFragment()
                    val fragmentManager =
                        parentFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, categoryFragment, "homeFragment").commit()
                }
            } else {
                Toast(getString(R.string.getFreeDelivery))
            }
        }


        binding.quickLy.setOnClickListener {
            toggleButton = !toggleButton
            if (toggleButton) {

                expressDelivery = true
                if (deliveryDayAdapter != null) deliveryDayAdapter!!.lastIndex = -1

                deliveryDayAdapter!!.notifyDataSetChanged()
                binding.DeliverTimeRecycler.visibility = View.GONE
                binding.quickLy.background = ContextCompat.getDrawable(
                    activityy,
                    R.drawable.round_corner_white_fill_green_border
                )
                binding.totalTv.text = NumberHandler.formatDouble(
                    total!!.toDouble() + expressDeliveryCharge,
                    fraction
                ) + " " + currency


                if (expressDeliveryCharge == 0.0 || expressDeliveryCharge == 0.00 || expressDeliveryCharge == 0.00) {
                    binding.deliveryFees.text = getString(R.string.free)
                    binding.freeDelivery.text = getString(R.string.over1)
                    binding.deliveryPrice.text = getString(R.string.free)

                } else {


                    binding.deliveryFees.text = NumberHandler.formatDouble(
                        expressDeliveryCharge,
                        fraction
                    ).plus(" ").plus(localModel?.currencyCode)

//                    binding.deliveryPrice.text = "$expressDeliveryCharge  $ currency"

                }

            } else {
              removeExpress()
            }
        }
        binding.choosePaymentType.setOnClickListener {
            showHidePaymentLY(binding.paymentRv.visibility === View.GONE)
        }

        binding.chooseDeliveryBtn.setOnClickListener {
            showHideDeliveryLY(
                binding.DeliverLY.visibility === View.GONE
            )
        }

        binding.chooseDeliveryTime.setOnClickListener { showHideDateLY(binding.DeliverDayRecycler.visibility === View.GONE) }

        binding.checkProductLy.setOnClickListener { showHideNoProductLY(binding.ProductCheckRecycler.visibility === View.GONE) }
    }

    private fun removeExpress() {
        expressDelivery = false
        binding.quickLy.background = ContextCompat.getDrawable(
            activityy,
            R.drawable.round_corner_gray_border_fill
        )
        binding.totalTv.text = NumberHandler.formatDouble(
            total!!.toDouble() + deliveryFees,
            fraction
        ) + " " + currency
        checkDeliveryFees()    }

    private fun initTimesList() {
        initTimeAdapter(deliveryFees)
    }

    private fun initPaymentAdapter() {
        for (i in paymentList!!.indices) {

            val paymentModel = paymentList!![i]

            when (paymentModel.id) {
                1 -> {
                    paymentModel.image = R.drawable.cash
                }
                2 -> {
                    paymentModel.image = R.drawable.collect
                }
                3 -> {
                    paymentModel.image = R.drawable.benefit
                }
                4 -> {
                    paymentModel.image = R.drawable.card
                }
            }
        }
        paymentAdapter = PaymentAdapter(
            activityy, paymentList
        ) { obj: Any?, func: String?, IsSuccess: Boolean ->
            selectedPaymentMethod = obj as PaymentModel?
            Log.i(
                javaClass.simpleName,
                "Log selectedPaymentMethod " + selectedPaymentMethod!!.shortname
            )
            if (selectedPaymentMethod != null) {
                if (selectedPaymentMethod!!.shortname == "CC") {
                    binding.chooseDelivery.visibility = View.GONE
                    initTimeAdapter(0.0)
                    binding.deliveryFees.text = getString(R.string.free)
                    binding.totalTv.text = NumberHandler.formatDouble(
                        NumberHandler.formatDouble(
                            total!!.toDouble(), fraction
                        ).toDouble() + 0.0, fraction
                    ).plus(" ").plus(currency)
                } else {
                    binding.chooseDelivery.visibility = View.VISIBLE
                    initTimeAdapter(deliveryFees)
                    binding.deliveryFees.text = NumberHandler.formatDouble(
                        deliveryFees,
                        localModel!!.fractional
                    ).plus(" ").plus(currency)

                    binding.totalTv.text = NumberHandler.formatDouble(
                        total!!.toDouble() + deliveryFees,
                        fraction
                    ).plus(" ").plus(currency)
                }
                showHidePaymentLY(false)
                checkData()

                checkDeliveryFees()
            }
        }
        binding.paymentRv.adapter = paymentAdapter
    }

    private fun getPaymentMethod(user_id: Int) {
        paymentList!!.clear()
        binding.loadingLYPay.visibility = View.VISIBLE
        DataFeacher(
            false,object :DataFetcherCallBack{
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        var message = getString(R.string.fail_to_get_data)
                        binding.loadingLYPay.visibility = View.GONE
                        val result = obj as PaymentResultModel?
                        if (func == Constants.ERROR) {
                            if (result != null && result.message != null) {
                                message = result.message
                            }
                            GlobalData.Toast(activityy, message)
                        } else if (func == Constants.FAIL) {
                            GlobalData.Toast(activityy, message)
                        } else if (func == Constants.NO_CONNECTION) {
                            GlobalData.Toast(activityy, R.string.no_internet_connection)
                        } else {
                            if (IsSuccess) {
                                if (result!!.data != null && result.data.size > 0) {
                                    paymentList = result.data
                                    initPaymentAdapter()
                                    initData()
                                }
                            }
                        }
                    }                }
            }
        ) .getPaymentMethod(user_id)
    }

    @get:SuppressLint("SetTextI18n")
    private val extraIntent: Unit
        get() {
            val bundle = arguments
            if (bundle != null) {
                total = bundle.getString(Constants.CART_SUM)
                productsSize = bundle.getInt(Constants.CART_PRODUCT_COUNT, 0)
                cartResultModel = bundle.getSerializable(Constants.CART_MODEL) as CartResultModel?
                productList = cartResultModel!!.data.cartData
                binding.productsSizeTv.text = "$total $currency"
                binding.totalTv.text =
                    NumberHandler.formatDouble(
                        total!!.toDouble() + deliveryFees,
                        fraction
                    ) + " " + currency
                minimum_order_amount = cartResultModel!!.data.minimumOrderAmount
                Log.i(
                    javaClass.simpleName,
                    "Log minimum_order_amount $minimum_order_amount"
                )
                Log.i(javaClass.simpleName, "Log deliveryFees $deliveryFees")
                Log.i(javaClass.simpleName, "Log total $total")
                Log.i(javaClass.simpleName, "Log deliveryFees $deliveryFees")
                if (total!!.toDouble() >= minimum_order_amount) {
                    deliveryFees = 0.0
                    binding.deliveryFees.text = getString(R.string.free)
                } else {
                    val total_price = minimum_order_amount - total!!.toDouble()
                    binding.freeBut.text =
                        getString(R.string.add) + " " + NumberHandler.roundDouble(total_price) + " " + currency + " " + getString(
                            R.string.get_Free
                        )
                }
            }
        }

    private fun sendOrder(orderCall: OrderCall) {
        AnalyticsHandler.checkOut(couponCodeId, currency, total, total)
        GlobalData.progressDialog(activityy, R.string.make_order, R.string.please_wait_sending)
        DataFeacher(false,object :DataFetcherCallBack{
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                GlobalData.hideProgressDialog()
                val result = obj as OrdersResultModel?
                if (func == Constants.ERROR) {
                    var message: String? = getString(R.string.fail_to_send_order)
                    if (result != null && result.message != null) {
                        message = result.message
                    }
                    GlobalData.errorDialog(activityy, R.string.make_order, message)
                } else {
                    if (IsSuccess) {
                        if (result!!.status == 200) {
                            UtilityApp.setCartCount(0)
                            if (result.order_id > 0) {
                                AnalyticsHandler.PurchaseEvent(
                                    couponCodeId,
                                    currency,
                                    selectedPaymentMethod!!.id,
                                    deliveryFees,
                                    result.order_id.toString(),
                                    total
                                )

//                                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_POSITION, 0))

                                println("Log order deliveryDate $deliveryDate")
                                println("Log order deliveryTime $deliveryTime")
                                val ordersDM = OrderModel()
                                ordersDM.orderId = result.order_id
                                ordersDM.deliveryDate = deliveryDate
                                ordersDM.deliveryTime = deliveryTime
                                val intent =
                                    Intent(activityy, OrderCompleteActivity::class.java)
                                intent.putExtra(Constants.ORDER_MODEL, ordersDM)
                                intent.putExtra(Constants.KEY_SHOW, true)
                                startActivity(intent)
                            }
                        } else {
                            var message: String? = getString(R.string.fail_to_send_order)
                            if (result.message != null) {
                                message = result.message
                            }
                            GlobalData.errorDialog(activityy, R.string.make_order, message)
                        }
                    } else {
                        Toast(getString(R.string.fail_to_send_order))
                    }
                }            }
        }) .makeOrder(orderCall)
    }

    private fun initData() {
        binding.freeLY.visibility = View.VISIBLE
        getDeliveryTimeList(storeId, userId!!)
        getQuickDelivery(storeId, localModel!!.countryId)
        defaultAddress
        getProductCheckerList()
        checkData()
    }

    private fun showHidePaymentLY(show: Boolean) {
        if (show) {
            binding.paymentArrowImg.setImageDrawable(
                ContextCompat.getDrawable(
                    activityy, R.drawable.ic_angle_up
                )
            )
            binding.paymentRv.visibility = View.VISIBLE
        } else {
            binding.paymentArrowImg.setImageDrawable(
                ContextCompat.getDrawable(
                    activityy, R.drawable.ic_angle_down
                )
            )
            binding.paymentRv.visibility = View.GONE
        }
    }

    private fun showHideDeliveryLY(show: Boolean) {
        if (show) {
            binding.deliveryArrowImg.setImageDrawable(
                ContextCompat.getDrawable(
                    activityy, R.drawable.ic_angle_up
                )
            )
            binding.DeliverLY.visibility = View.VISIBLE
        } else {
            binding.deliveryArrowImg.setImageDrawable(
                ContextCompat.getDrawable(
                    activityy, R.drawable.ic_angle_down
                )
            )
            binding.DeliverLY.visibility = View.GONE
        }
    }

    private fun showHideDateLY(show: Boolean) {
        if (show) {
            binding.toggleDeliveryBut.setImageDrawable(
                ContextCompat.getDrawable(
                    activityy, R.drawable.ic_angle_up
                )
            )
            binding.DeliverDayRecycler.visibility = View.VISIBLE
            binding.DeliverTimeRecycler.visibility = View.VISIBLE
        } else {
            binding.toggleDeliveryBut.setImageDrawable(
                ContextCompat.getDrawable(
                    activityy, R.drawable.ic_angle_down
                )
            )
            binding.DeliverDayRecycler.visibility = View.GONE
            binding.DeliverTimeRecycler.visibility = View.GONE
        }
    }

    private fun showHideNoProductLY(show: Boolean) {
        if (show) {
            binding.toggleCheckerBut.setImageDrawable(
                ContextCompat.getDrawable(
                    activityy, R.drawable.ic_angle_up
                )
            )
            binding.ProductCheckRecycler.visibility = View.VISIBLE
        } else {
            binding.toggleCheckerBut.setImageDrawable(
                ContextCompat.getDrawable(
                    activityy, R.drawable.ic_angle_down
                )
            )
            binding.ProductCheckRecycler.visibility = View.GONE
        }
    }

    override fun onContainerSelectSelected(addressesDM: AddressModel) {
        addressId = addressesDM.id
    }

    override fun onAddressSelected(addressesDM: AddressModel) {
        addressId = addressesDM.id
    }

    private fun initDaysAdapter() {
        DayList = ArrayList()
        for ((dateId, s) in datesMap.keys.withIndex()) {
            val deliveryTime = DeliveryTime(dateId, s, s)
            DayList?.add(deliveryTime)
        }
        deliveryDayAdapter = DeliveryDayAdapter(
            activityy, DayList
        ) { obj: Any, func: String?, IsSuccess: Boolean ->
            val deliveryTime = obj as DeliveryTime
            deliveryTimesList = datesMap[deliveryTime.time]
            binding.DeliverTimeRecycler.visibility = View.VISIBLE
            binding.quickLy.background = ContextCompat.getDrawable(
                activityy,
                R.drawable.round_corner_gray_border_fill
            )
            initTimesList()
        }
        binding.DeliverDayRecycler.adapter = deliveryDayAdapter
        initTimesList()
    }

    private fun initTimeAdapter(deliveryFee: Double) {
        var deliveryFee: Double? = deliveryFee
        if (selectedPaymentMethod != null && selectedPaymentMethod!!.shortname == "CC") {
            deliveryFee = 0.0
        }
        deliveryTimeAdapter = DeliveryTimeAdapter(
            activityy, deliveryTimesList, deliveryFee, deliveryDateId
        ) { obj: Any, func: String?, IsSuccess: Boolean ->
            val selectedTime = obj as DeliveryTime
            deliveryDateId = selectedTime.id
            deliveryDate = selectedTime.date
            deliveryTime = selectedTime.time
            Log.i(
                javaClass.simpleName,
                "Log deliveryTimesList click $deliveryDateId"
            )
            Log.i(
                javaClass.simpleName,
                "Log deliveryTimesList click " + selectedTime.time
            )

            // to hide layout when select time
            showHideDateLY(false)
            showHideNoProductLY(true)
            toggleButton = !toggleButton
            removeExpress()
        }
        binding.DeliverTimeRecycler.adapter = deliveryTimeAdapter

        deliveryTimeAdapter!!.notifyDataSetChanged()
    }

    override fun OnEditClicked(addressModel: AddressModel, isChecked: Boolean, position: Int) {
        val intent = Intent(activityy, AddNewAddressActivity::class.java)
        intent.putExtra(Constants.KEY_EDIT, true)
        intent.putExtra(Constants.KEY_ADDRESS_ID, addressModel.id)
        startActivity(intent)
    }

    private fun initAddressData(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
//            EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_invoice))
            if (data != null) {
                val bundle = data.extras
                addressId = bundle!!.getInt(Constants.ADDRESS_ID)
                addressTitle = bundle.getString(Constants.ADDRESS_TITLE)
                addressFullAddress = bundle.getString(Constants.ADDRESS_FULL)
                binding.delivery.text = addressTitle
                binding.tvFullAddress.text = addressFullAddress
                binding.changeAddressBtu.setText(R.string.change_address)
                AnalyticsHandler.addShippingInfo(couponCodeId, currency, total, total)
                getDeliveryInfo(storeId, addressId, true)
                showHideDeliveryLY(false)
                showHideDateLY(true)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun getQuickDelivery(storeId: Int, countryId: Int) {
        val quickCall1 = QuickCall()
        quickCall1.store_id = storeId
        quickCall1.country_id = countryId
        DataFeacher(
            false
        ,object :DataFetcherCallBack{
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        var message = getString(R.string.fail_to_get_data)
                        binding.loadingLYPay.visibility = View.GONE
                        val result =
                            obj as ResultAPIModel<QuickDeliveryRespond?>?
                        if (func == Constants.ERROR) {
                            if (result?.message != null) {
                                message = result.message
                            }
                            GlobalData.Toast(activityy, message)
                        } else if (func == Constants.FAIL) {
                            GlobalData.Toast(activityy, message)
                        } else if (func == Constants.NO_CONNECTION) {
                            GlobalData.Toast(activityy, R.string.no_internet_connection)
                        } else {
                            if (IsSuccess) {
                                if (result!!.data != null) {
                                    Log.i(
                                        javaClass.simpleName,
                                        "Log result" + result.data
                                    )
                                    val quickDeliveryRespond = result.data
                                    hasExpress = quickDeliveryRespond!!.isHasExpressDelivery

                                    if (hasExpress) {
                                        binding.quickLy.visibility = View.VISIBLE

                                        expressDeliveryCharge =
                                            quickDeliveryRespond.expressDeliveryCharge.toDouble()
                                        binding.deliveryTime.text =
                                            quickDeliveryRespond.expressDeliverydescription


                                        Log.i(
                                            javaClass.simpleName,
                                            "Log 1 deliveryFees$deliveryFees"
                                        )
                                        if (expressDeliveryCharge == 0.0 || expressDeliveryCharge == 0.00 || expressDeliveryCharge == 0.000) {
                                            binding.deliveryPrice.text = getString(R.string.free)
                                        } else {

                                            binding.deliveryPrice.text= NumberHandler.formatDouble(
                                                expressDeliveryCharge,
                                                fraction
                                            ) + " " + currency


                                        }
                                    } else {
                                        binding.quickLy.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }                }
            }) .getQuickDelivery(quickCall1)
    }

    private fun getDeliveryInfo(storeId: Int, addressId: Int, fromAddress: Boolean) {
        if (fromAddress) {
            GlobalData.progressDialog(activityy, R.string.getData, R.string.please_wait_upload)
        }
        DataFeacher(
            false,object :DataFetcherCallBack{
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        val quickDeliveryRespond = obj as DeliveryInfo?
                        if (IsSuccess) {
                            if (fromAddress) {
                                GlobalData.hideProgressDialog()
                            }
                            if (quickDeliveryRespond != null) {
                                Log.i(ContentValues.TAG, "Log GetDeliveryInfo")
                                if (total!!.toDouble() >= minimum_order_amount) {
                                    deliveryFees = 0.0
                                    binding.deliveryFees.text = getString(R.string.free)
                                } else {
                                    val total_price =
                                        minimum_order_amount - total!!.toDouble()
                                    binding.freeBut.text =
                                        getString(R.string.add).plus(" ").plus(
                                            NumberHandler.roundDouble(
                                                total_price
                                            )
                                        ).plus(" ").plus(currency).plus("  ")
                                            .plus(getString(R.string.get_Free))

                                    deliveryFees = quickDeliveryRespond.deliveryCharges
                                    if (deliveryFees == 0.0 || deliveryFees == 0.00 || deliveryFees == 0.000) {
                                        binding.deliveryFees.text = getString(R.string.free)
                                        binding.freeDelivery.text = getString(R.string.over1)
                                        binding.deliveryPrice.text = getString(R.string.free)
                                    } else {
                                        binding.deliveryFees.text = NumberHandler.formatDouble(
                                            deliveryFees,
                                            localModel!!.fractional
                                        ).plus(" ").plus(currency)
                                        binding.freeDelivery.text =
                                            getString(R.string.over).plus(" ").plus(minimum_order_amount)
                                                .plus(" ").plus(currency).plus(".")
                                        binding.deliveryPrice.text =
                                            quickDeliveryRespond.expressDeliveryCharges.toString().plus(" ")
                                                .plus(localModel!!.currencyCode)
                                    }
                                }
                                binding.totalTv.text = NumberHandler.formatDouble(
                                    total!!.toDouble() + deliveryFees,
                                    fraction
                                ).plus(" ").plus(currency)
                            } else {
                                binding.quickLy.visibility = View.GONE
                            }
                        } else {
                            GlobalData.hideProgressDialog()
                        }
                    }                }
            }
        ) .GetDeliveryInfo(storeId, addressId)
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
    fun onMessageEvent(event: MessageEvent) {
    }

    private fun getProductCheckerList() {
        productCheckerList!!.add(ProductChecker(1, getString(R.string.product_not_found_1)))
        productCheckerList!!.add(ProductChecker(2, getString(R.string.product_not_found_2)))
        productCheckerList!!.add(ProductChecker(3, getString(R.string.product_not_found_3)))
        productCheckerAdapter = ProductCheckAdapter(
            activityy, productCheckerList, itemNotFoundId
        ) { obj: Any, func: String?, IsSuccess: Boolean ->
            val productChecker = obj as ProductChecker
            itemNotFoundId = productChecker.id
            showHideNoProductLY(false)
        }
        binding.ProductCheckRecycler.adapter = productCheckerAdapter
    }

    private fun checkData() {
        Log.i(javaClass.simpleName, "Log deliveryDateId $deliveryDateId")
        if (selectedPaymentMethod == null) {
            showHidePaymentLY(true)
            return
        }
        if (selectedPaymentMethod!!.shortname != "CC" && addressId == 0) {
            showHideDeliveryLY(true)
            binding.changeAddressBtu.performClick()
            return
        }

//        if (deliveryDateId == 0) {
        if (binding.DeliverDayRecycler.visibility === View.GONE) {
            showHideDateLY(true)
            return
        }
        if (itemNotFoundId == 0) {
            showHideNoProductLY(true)
        }
    }

    private fun getDeliveryTimeList(storeId: Int, user_id: Int) {
        datesMap.clear()
        binding.loadingDelivery.visibility = View.VISIBLE
        DataFeacher(
            true
        , object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        val result = obj as CheckOrderResponse?
                        val message = getString(R.string.fail_to_get_data)
                        binding.loadingDelivery.visibility = View.GONE
                        if (IsSuccess) {
                            if (result?.data != null && result.data
                                    .deliveryTimes != null && result.data.deliveryTimes.size > 0
                            ) {
                                if (result.data != null && result.data
                                        .deliveryTimes.size > 0
                                ) {
                                    val checkOrderResponse = result.data
                                    val userDefaultAddress =
                                        checkOrderResponse.userAddress
                                    if (userDefaultAddress != null && userDefaultAddress.id > 0) {
                                        addressId = userDefaultAddress.id
                                        addressTitle = userDefaultAddress.name
                                        binding.tvFullAddress.text = userDefaultAddress.fullAddress
                                        binding.delivery.text = addressTitle
                                        Log.i(
                                            javaClass.simpleName,
                                            "Log  CheckOrderResponse AddressId  " + result.data
                                                .userAddress.id
                                        )
                                    }
                                    minimum_order_amount = checkOrderResponse.minimumOrderAmount
                                    deliveryFees = checkOrderResponse.deliveryCharges
                                    Log.i(
                                        javaClass.simpleName,
                                        "Log  CheckOrderResponse deliveryFees  $deliveryFees"
                                    )
                                    checkDeliveryFees()
                                    val datesList =
                                        result.data.deliveryTimes
                                    val firstTime = datesList[0]
                                    var currentDate = firstTime.date
                                    var timesList: MutableList<DeliveryTime> =
                                        ArrayList()
                                    while (datesList.size > 0) {
                                        val deliveryTime = datesList[0]
                                        if (deliveryTime.date == currentDate) {
                                            timesList.add(deliveryTime)
                                            datesList.removeAt(0)
                                            if (datesList.isEmpty()) {
                                                datesMap[deliveryTime.date] = timesList
                                            }
                                        } else {
                                            datesMap[currentDate] = timesList
                                            currentDate = deliveryTime.date
                                            timesList = ArrayList()
                                        }
                                    }
                                    deliveryTimesList = datesMap[firstTime.date]
                                    if (deliveryTimesList != null && deliveryTimesList!!.size > 0) deliveryDateId =
                                        deliveryTimesList!![0].id
                                    deliveryDate = firstTime.date
                                    deliveryTime = firstTime.time
                                    Log.i(
                                        javaClass.simpleName,
                                        "Log deliveryTimesList click $deliveryDateId"
                                    )
                                    Log.i(
                                        javaClass.simpleName,
                                        "Log deliveryTimesList click $deliveryTime"
                                    )
                                    initDaysAdapter()
                                } else {
                                    binding.noDeliveryTv.visibility = View.VISIBLE
                                    GlobalData.Toast(activityy, message)
                                }
                            }
                        }
                    }
                }
        }).getDeliveryTimeList(storeId, user_id)
    }

    companion object {
        private const val ADDRESS_CODE = 100
    }


}

