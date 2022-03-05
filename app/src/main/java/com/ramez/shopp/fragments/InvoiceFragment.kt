package com.ramez.shopp.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Dialogs.ErrorMessagesDialog
import com.ramez.shopp.Dialogs.PaymentDialog
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.activities.*
import com.ramez.shopp.adapter.*
import com.ramez.shopp.adapter.AddressCheckAdapter.OnEditClick
import com.ramez.shopp.adapter.AddressCheckAdapter.OnRadioAddressSelect
import com.ramez.shopp.classes.*
import com.ramez.shopp.databinding.FragmentInvoiceBinding
import mobi.foo.benefitinapp.data.Transaction
import mobi.foo.benefitinapp.listener.BenefitInAppButtonListener
import mobi.foo.benefitinapp.listener.CheckoutListener
import mobi.foo.benefitinapp.utils.BenefitInAppCheckout
import mobi.foo.benefitinapp.utils.BenefitInAppHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class InvoiceFragment : FragmentBase(), OnRadioAddressSelect, AddressCheckAdapter.OnContainerSelect,
        OnEditClick {

    var addressList: ArrayList<AddressModel>? = null
    var deliveryTypeList: MutableList<DeliveryTypeModel>? = null
    var deliveryTimesList: List<DeliveryTime>? = null
    var productList: ArrayList<CartModel>? = null
    var productCheckerList: MutableList<ProductChecker>? = null
    var dayList: MutableList<DeliveryTime>? = null
    val datesMap = LinkedHashMap<String, List<DeliveryTime>>()

    var deliveryDayAdapter: DeliveryDayAdapter? = null
    var deliveryTimeAdapter: DeliveryTimeAdapter? = null
    var productCheckerAdapter: ProductCheckAdapter? = null

    private var selectAddressLauncher: ActivityResultLauncher<Intent>? = null
    private var paymentLauncher: ActivityResultLauncher<Intent>? = null
    private var ccmLauncher: ActivityResultLauncher<Intent>? = null

    var selectedPaymentMethod: PaymentModel? = null
    var selectedDeliveryType: DeliveryTypeModel? = null
    var cartResultModel: CartResultModel? = null
    var paymentDialog: PaymentDialog? = null

    var userId: Int? = 0
    var couponCodeId = "0"
    var deliveryDate: String? = null
    var deliveryTime: String? = null
    var expressDelivery = false
    var localModel: LocalModel? = null
    var storeId = 0
    var productsSize = 0
    var total: String? = ""
    var currency = "BHD"
    var fraction = 2

    private var addressTitle: String? = null
    private var addressFullAddress: String? = null
    private var deliveryFees = 0.0
    private var isGetDeliveryInfo = false
    private var expressDeliveryCharge = 0.0
    private var hasExpress: Boolean = false
    private var minimumOrderAmount = 0
    private var addressId = 0
    var deliveryDateId = 0
    var itemNotFoundId = 0
    var countryCode = ""
    var countryId: Int = 0
    var payToken: String? = ""
    var deliveryType: Int = 0
    var ordersDM: OrderModel? = null
    lateinit var binding: FragmentInvoiceBinding
    var deliveryTimeDesc: String = ""

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


        ccmLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val bundle = result.data?.extras
                payToken = bundle?.getString(Constants.PAY_TOKEN)

            } else {
                showFailPage()
            }


        }
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activityy
            )

        countryCode = if (localModel?.shortname != null) {
            localModel?.shortname ?: Constants.default_short_name
        } else {
            GlobalData.COUNTRY
        }
        addressList = ArrayList()
        deliveryTypeList = ArrayList()
        deliveryTimesList = ArrayList()
        dayList = ArrayList()
        productCheckerList = ArrayList()

        fraction = localModel?.fractional ?: Constants.three
        minimumOrderAmount = localModel?.minimum_order_amount ?: 0
        storeId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        countryId = localModel?.countryId?.toInt() ?: Constants.default_country_id
        currency = localModel?.currencyCode ?: Constants.BHD
        userId = UtilityApp.getUserData().id ?: 0

        val deliverDayLlm = LinearLayoutManager(activityy)
        val deliverTimeLlm = LinearLayoutManager(activityy)
        val checkProductLlm = LinearLayoutManager(activityy)
        val deliveryLinearLayoutManager = LinearLayoutManager(activity)

        deliverDayLlm.orientation = LinearLayoutManager.HORIZONTAL
        checkProductLlm.orientation = LinearLayoutManager.VERTICAL
        deliverTimeLlm.orientation = LinearLayoutManager.VERTICAL


        binding.DeliverTimeRecycler.layoutManager = deliverTimeLlm
        binding.DeliverDayRecycler.layoutManager = deliverDayLlm
        binding.ProductCheckRecycler.layoutManager = checkProductLlm
        binding.deliveryTypeRv.layoutManager = deliveryLinearLayoutManager
        binding.deliveryTypeRv.layoutManager = deliveryLinearLayoutManager

        binding.DeliverDayRecycler.setHasFixedSize(true)
        binding.DeliverTimeRecycler.setHasFixedSize(true)
        binding.ProductCheckRecycler.setHasFixedSize(true)
        binding.deliveryTypeRv.setHasFixedSize(true)
        binding.deliveryTypeRv.animation = null


        extraIntent

        initData()
        initListener()

        paymentLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result != null && result.resultCode == RESULT_OK) {
                showCompletePay()
            } else {
                showFailPage()
            }

        }
    }

    fun initDeliveryFees(feesValue: Double?): Double {
        val result: Double? = if (total?.toDouble() ?: 0.0 >= minimumOrderAmount) {
            0.0
        } else {
            feesValue
        }
        return result ?: 0.0
    }

    private fun checkDeliveryFees() {

        Log.i(javaClass.name, "Log checkDeliveryFees deliveryType $deliveryType")

        //check total with minimumOrderAmount
        if (total?.toDouble() ?: 0.0 < minimumOrderAmount) {
//            deliveryFees = 0.0
            val totalPrice = minimumOrderAmount - (total?.toDouble() ?: 0.0)
            binding.freeBut.text = getString(R.string.add).plus(" ")
                .plus(NumberHandler.roundDouble(totalPrice)).plus(" ")
                .plus(currency).plus(" ")
                .plus(getString(R.string.get_Free))
        }

        //check deliveryTyp

        var deliveryFeesStr = ""
        var totalStr = ""
        when (deliveryType) {
            0 -> {
                if (deliveryFees == 0.0) {
                    deliveryFeesStr = getString(R.string.free)
                    binding.freeDelivery.text = getString(R.string.over1)
                } else {
                    deliveryFeesStr = NumberHandler.formatDouble(
                        deliveryFees,
                        fraction
                    ).plus(" $currency")
                    binding.freeDelivery.text =
                        getString(R.string.over).plus(" $minimumOrderAmount $currency.")
                }
                totalStr =
                    NumberHandler.formatDouble((total?.toDouble() ?: 0.0).plus(deliveryFees), fraction)
                initTimeAdapter(deliveryFees)

            }
            1 -> {
                binding.chooseDelivery.visibility = View.GONE
                deliveryFeesStr = getString(R.string.free)
                totalStr =
                    NumberHandler.formatDouble((total?.toDouble() ?: 0.0), fraction)
                initTimeAdapter(0.0)
            }
            2 -> {
                deliveryFeesStr = if (expressDeliveryCharge == 0.0) {
                    getString(R.string.free)
                    //                    binding.freeDelivery.text = getString(R.string.over1)
                } else {
                    NumberHandler.formatDouble(
                        expressDeliveryCharge,
                        fraction
                    ).plus(" $currency")
                    //                    binding.freeDelivery.text = getString(R.string.over).plus(" $minimumOrderAmount $currency.")
                }

                totalStr =
                    NumberHandler.formatDouble(
                        (total?.toDouble() ?: 0.0).plus(expressDeliveryCharge),
                        fraction
                    )

                initTimeAdapter(expressDeliveryCharge)

            }

        }
        binding.deliveryFees.text = deliveryFeesStr
        binding.totalTv.text = totalStr.plus(" $currency")

        println("Log deliveryFees checkDeliveryFees $deliveryFees")
    }

    private val defaultAddress: Unit
        get() {
            if (UtilityApp.getUserData().lastSelectedAddress > 0) {
                addressId = UtilityApp.getUserData().lastSelectedAddress
                Log.i(javaClass.simpleName, "Log addressId$addressId")
                getDeliveryInfo(storeId, addressId, false)
                binding.changeAddressBtu.setText(R.string.change_address)
                showHideDeliveryLY(true)
                showHideDateLY(true)

            } else {
                Log.i(javaClass.simpleName, "Log addressId$addressId")
                binding.changeAddressBtu.setText(R.string.choose_address)
            }
        }

    private fun initListener() {
        binding.saveBut.setOnClickListener {
//            EventBus.getDefault()
//                .post(MessageEvent(MessageEvent.TYPE_POSITION, 0))
        }


        binding.sendOrder.setOnClickListener {

            if (deliveryType == 1 && addressId == 0) {
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


            showPaymentDialog()
        }


        binding.changeAddressBtu.setOnClickListener {

            val intent = Intent(activityy, AddressActivity::class.java)
            selectAddressLauncher!!.launch(intent)
        }


        binding.freeBut.setOnClickListener {
            if (deliveryFees > 0) {
                if (total?.toDouble() ?: 0.0 >= minimumOrderAmount) {
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

        binding.chooseDeliverytType.setOnClickListener {
            showHideDeliveryTypeLY(binding.deliveryTypeRv.visibility == View.GONE)
        }

        binding.chooseDeliveryBtn.setOnClickListener {
            showHideDeliveryLY(
                binding.DeliverLY.visibility == View.GONE
            )
        }

        binding.chooseDeliveryTime.setOnClickListener { showHideDateLY(binding.DeliverDayRecycler.visibility == View.GONE) }

        binding.checkProductLy.setOnClickListener { showHideNoProductLY(binding.ProductCheckRecycler.visibility == View.GONE) }

        binding.btnBenefitBay.setListener(payUsingBenefit())
    }

    private fun initTimesList() {
        initTimeAdapter(deliveryFees)
    }

    private fun showPaymentDialog() {
        paymentDialog =
            PaymentDialog(
                activity, object : DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        selectedPaymentMethod = obj as PaymentModel?
                        if (selectedPaymentMethod != null) {

                            when (selectedPaymentMethod?.shortname) {

                                "CCM" -> {
                                    val intent = Intent(activityy, PayUsingCardActivity::class.java)
                                    ccmLauncher!!.launch(intent)
                                }
                            }
                            checkData()
                            sendOrder()
                        } else {
                            Toast(R.string.please_select_payment_method)
                        }

                    }
                })
        paymentDialog?.show()
    }

    private fun payUsingBenefit(): BenefitInAppButtonListener {
        return object : BenefitInAppButtonListener {
            override fun onButtonClicked() {
                val appId = Constants.appId
                val referenceId = ordersDM?.orderCode
                val merchantId = Constants.merchantId
                val secret = Constants.secret
                val amount = binding.totalTv.toString().replace(currency, " ")
                Log.i(javaClass.name, "Log amount $amount")

                val country = Constants.default_short_name
                val currency = Constants.currency
                val merchantCategoryCode = userId.toString()
                val merchantName = Constants.merchantName
                val merchantCity = Constants.merchantCity
                BenefitInAppCheckout.newInstance(
                    requireActivity(),
                    appId, referenceId, merchantId, secret, amount, country, currency,
                    merchantCategoryCode, merchantName, merchantCity, object : CheckoutListener {
                        override fun onTransactionSuccess(transaction: Transaction?) {
                            Log.i(javaClass.name, "Log transactionMessage ${transaction?.transactionMessage}")
                            Toast(transaction?.transactionMessage)
                            saveSuccessTransaction(transaction, ordersDM?.orderId)
                        }

                        override fun onTransactionFail(transaction: Transaction?) {
                            Log.i(
                                javaClass.name,
                                "Log Fail transactionMessage ${transaction?.transactionMessage}"
                            )

                            Toast(transaction?.transactionMessage)
                            ordersDM?.message = transaction?.transactionMessage
                            saveFailTransaction(transaction, ordersDM?.orderId)

                        }
                    }).checkParams()

            }

            override fun onFail(p0: Int) {
                Toast(getString(R.string.error))
                Log.i(javaClass.name, "Log referenceId ${ordersDM?.orderCode}")
                Log.i(javaClass.name, "Log referenceId Constants  ${Constants.referenceId}")
                Log.i(javaClass.name, "Log onFail  $p0")
            }
        }


    }

    fun sendOrder() {
        val orderCall = OrderCall()
        orderCall.store_ID = storeId
        orderCall.address_id = addressId
        orderCall.payment_method = selectedPaymentMethod?.shortname
        orderCall.coupon_code_id = couponCodeId
        orderCall.delivery_date_id = deliveryDateId
        orderCall.itemNotFoundAction = itemNotFoundId
        orderCall.expressDelivery = expressDelivery
        orderCall.pay_token = payToken
        orderCall.delivery_type = deliveryType
        sendOrder(orderCall)
    }

    private val extraIntent: Unit
        get() {
            val bundle = arguments
            if (bundle != null) {
                total = bundle.getString(Constants.CART_SUM)
                productsSize = bundle.getInt(Constants.CART_PRODUCT_COUNT, 0)
                cartResultModel = bundle.getSerializable(Constants.CART_MODEL) as CartResultModel?
                productList = cartResultModel?.data?.cartData
                binding.productsSizeTv.text = total.plus("  $currency")

                binding.totalTv.text = NumberHandler.formatDouble(
                    total?.toDouble() ?: 0.0.plus(deliveryFees),
                    fraction
                ).plus("  $currency")

                minimumOrderAmount = cartResultModel?.data?.minimumOrderAmount ?: 0
                Log.i(
                    javaClass.simpleName,
                    "Log intent minimum_order_amount $minimumOrderAmount"
                )
                println("Log deliveryFees intent $deliveryFees")
                Log.i(javaClass.simpleName, "Log intent total $total")

                checkDeliveryFees()
            }
        }

    private fun sendOrder(orderCall: OrderCall) {

        AnalyticsHandler.checkOut(couponCodeId, currency, total, total)
        GlobalData.progressDialog(activityy, R.string.make_order, R.string.please_wait_sending)
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                GlobalData.hideProgressDialog()
                var message: String? = getString(R.string.fail_to_send_order)
                if (func == Constants.ERROR) {
                    val result = obj as OrdersResultModel?
                    if (result != null && result.message != null) {
                        message = result.message
                    }
                    GlobalData.errorDialog(activityy, R.string.make_order, message)
                } else {
                    if (IsSuccess) {
                        val result = obj as OrdersResultModel?

                        Log.i(tag, "Log status " + result?.status)

                        when (result?.status) {
                            200 -> {
                                val result = obj as OrdersResultModel?

                                Log.i(tag, "Log status " + result?.status)

                                AnalyticsHandler.PurchaseEvent(
                                    couponCodeId,
                                    currency,
                                    selectedPaymentMethod?.id ?: 0,
                                    deliveryFees,
                                    result?.orderId.toString(),
                                    total
                                )
                                ordersDM = OrderModel()
                                ordersDM?.orderId = result?.orderId ?: 0
                                ordersDM?.orderCode = result?.orderCode
                                ordersDM?.deliveryDate = deliveryDate
                                ordersDM?.deliveryTime = deliveryTime
                                Log.i(javaClass.name, "Log referenceId after  ${result?.orderCode}")
                                Log.i(
                                    javaClass.name,
                                    "Log selectedPaymentMethod?.shortname  ${selectedPaymentMethod?.shortname}"
                                )


                                when (selectedPaymentMethod?.shortname) {
                                    "BNP" -> {
                                        val intent = Intent(activityy, PayWebViewActivity::class.java)
                                        intent.putExtra(
                                            PayWebViewActivity.KEY_WEB_URL,
                                            result?.paymentResp?.result
                                        )
                                        val successUrl = GlobalData.successPayUrl
                                        val failUrl = GlobalData.failPayURl

                                        intent.putExtra(
                                            PayWebViewActivity.KEY_RETURN_SUCCESS_URL, successUrl
                                        )
                                        intent.putExtra(
                                            PayWebViewActivity.KEY_RETURN_FAIL_URL, failUrl
                                        )
                                        paymentLauncher?.launch(intent)


                                    }
                                    "BNM" -> {
                                        binding.btnBenefitBay.performClick()


                                    }
                                    else -> {

                                        showCompletePay()
                                    }
                                }
                            }
                            400 -> {
                                if (result.message != null) {
                                    message = result.message
                                }
                                GlobalData.errorDialog(activityy, R.string.make_order, message)

                            }
                            else -> {
                                message = getString(R.string.fail_create_order)
                                if (result?.message?.isNotEmpty() == true) {
                                    message = result.message
                                }
                                GlobalData.errorDialog(activityy, R.string.make_order, message)
                            }
                        }

                    } else {
                        val result = obj as OrdersResultModel?

                        if (result?.message != null) {
                            message = result.message
                        }
                        GlobalData.errorDialog(activityy, R.string.make_order, message)
                    }
                }
            }


        }).makeOrder(orderCall)
    }

    private fun showCompletePay() {
        UtilityApp.setCartCount(0)
        val intent = Intent(requireActivity(), OrderCompleteActivity::class.java)
        intent.putExtra(Constants.ORDER_MODEL, ordersDM)
        intent.putExtra(Constants.KEY_SHOW, true)
        startActivity(intent)
    }

    private fun showFailPage() {
//        UtilityApp.setCartCount(0)
        val intent = Intent(requireActivity(), PayFailActivity::class.java)
        intent.putExtra(Constants.ORDER_MODEL, ordersDM)
        intent.putExtra(Constants.KEY_SHOW, true)
        startActivity(intent)
    }

    private fun initData() {
        binding.freeLY.visibility = View.VISIBLE
        getQuickDelivery(storeId, countryId)
        getDeliveryTimeList(storeId, userId!!)
        defaultAddress
        getProductCheckerList()
        checkData()

    }

    private fun showHideDeliveryTypeLY(show: Boolean) {
        if (show) {
            binding.deliveryTypeArrowImg.setImageDrawable(
                ContextCompat.getDrawable(
                    activityy, R.drawable.ic_angle_up
                )
            )
            binding.deliveryTypeRv.visibility = View.VISIBLE
        } else {
            binding.deliveryTypeArrowImg.setImageDrawable(
                ContextCompat.getDrawable(
                    activityy, R.drawable.ic_angle_down
                )
            )
            binding.deliveryTypeRv.visibility = View.GONE

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
        dayList = ArrayList()
        for ((dateId, s) in datesMap.keys.withIndex()) {
            val deliveryTime = DeliveryTime(dateId, s, s)
            dayList?.add(deliveryTime)
        }
        deliveryDayAdapter = DeliveryDayAdapter(
            activityy, dayList
        ) { obj: Any, func: String?, IsSuccess: Boolean ->
            val deliveryTime = obj as DeliveryTime
            deliveryTimesList = datesMap[deliveryTime.time]
            binding.DeliverTimeRecycler.visibility = View.VISIBLE
            initTimesList()

        }
        binding.DeliverDayRecycler.adapter = deliveryDayAdapter
        initTimesList()
    }

    private fun initTimeAdapter(deliveryFee: Double) {

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
            showHideDateLY(false)
            showHideNoProductLY(true)
        }
        binding.DeliverTimeRecycler.adapter = deliveryTimeAdapter
        deliveryTimeAdapter?.notifyDataSetChanged()
    }

    override fun OnEditClicked(addressModel: AddressModel, isChecked: Boolean, position: Int) {
        val intent = Intent(activityy, AddNewAddressActivity::class.java)
        intent.putExtra(Constants.KEY_EDIT, true)
        intent.putExtra(Constants.KEY_ADDRESS_ID, addressModel.id)
        startActivity(intent)
    }

    private fun initAddressData(resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {
            if (data != null) {
                val bundle = data.extras
                addressId = bundle?.getInt(Constants.ADDRESS_ID) ?: 0
                addressTitle = bundle?.getString(Constants.ADDRESS_TITLE)
                addressFullAddress = bundle?.getString(Constants.ADDRESS_FULL)
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

    private fun getQuickDelivery(storeId: Int, countryId: Int) {
        binding.loadingLYDelivery.visibility = View.VISIBLE
        val quickCall1 = QuickCall()
        quickCall1.store_id = storeId
        quickCall1.country_id = countryId
        DataFeacher(
            false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                    if (isVisible) {
                        var message = getString(R.string.fail_to_get_data)
//                        if (func == Constants.ERROR) {
//                            val result = obj as ResultAPIModel<QuickDeliveryRespond?>?
//
//                            if (result?.message != null) {
//                                message = result.message
//                            }
//                            GlobalData.Toast(activityy, message)
//                            binding.loadingLYDelivery.visibility = View.GONE
//
//                        } else if (func == Constants.FAIL) {
//                            GlobalData.Toast(activityy, message)
//                        } else if (func == Constants.NO_CONNECTION) {
//                            GlobalData.Toast(activityy, R.string.no_internet_connection)
//                            binding.loadingLYDelivery.visibility = View.GONE
//
//                        } else {
//                        }

                        if (IsSuccess) {
                            binding.loadingLYDelivery.visibility = View.GONE
                            val result = obj as ResultAPIModel<QuickDeliveryRespond?>?
                            if (result?.data != null) {
                                Log.i(
                                    javaClass.simpleName,
                                    "Log result" + result.data
                                )
                                val quickDeliveryRespond = result.data
                                hasExpress = quickDeliveryRespond?.isHasExpressDelivery ?: false


                                Log.i(javaClass.simpleName, "Log hasExpress  $hasExpress")
                                if (hasExpress) {
                                    expressDeliveryCharge =
                                        quickDeliveryRespond?.expressDeliveryCharge?.toDouble() ?: 0.0
                                    deliveryTimeDesc =
                                        quickDeliveryRespond?.expressDeliverydescription ?: ""

                                    Log.i(
                                        javaClass.simpleName, "Log  deliveryFees$deliveryFees"
                                    )

                                }

                            }
                            if (isGetDeliveryInfo) {
                                getDeliveryTypes()
                                checkDeliveryFees()
                            }
                        } else {
                            showErrorDialog(2)
                        }


                    }
                }
            }).getQuickDelivery(quickCall1)
    }

    private fun getDeliveryInfo(storeId: Int, addressId: Int, fromAddress: Boolean) {
        if (fromAddress) {
            GlobalData.progressDialog(activityy, R.string.getData, R.string.please_wait_upload)
        }
        DataFeacher(
            false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        if (fromAddress) {
                            GlobalData.hideProgressDialog()
                        }
                        if (IsSuccess) {
                            isGetDeliveryInfo = true
                            val quickDeliveryRespond = obj as DeliveryInfo?

                            if (quickDeliveryRespond != null) {
//                                deliveryFees = quickDeliveryRespond.deliveryCharges
                                deliveryFees = initDeliveryFees(quickDeliveryRespond.deliveryCharges)
                                println("Log deliveryFees getDeliveryInfo $deliveryFees")
                                getDeliveryTypes()
                                checkDeliveryFees()
                            }
                        } else {
                            showErrorDialog(1)
                        }
//                        getDeliveryTypes()
//                        checkDeliveryFees()

                    }


                }
            }
        ).GetDeliveryInfo(storeId, addressId)
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
        productCheckerList?.add(
            ProductChecker(
                1,
                getString(R.string.product_not_found_1)
            )
        )
        productCheckerList?.add(
            ProductChecker(
                2,
                getString(R.string.product_not_found_2)
            )
        )
        productCheckerList?.add(
            ProductChecker(
                3,
                getString(R.string.product_not_found_3)
            )
        )
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

        if (selectedDeliveryType == null) {
            showHideDeliveryTypeLY(true)
            return
        }

        if (deliveryType != 1 && addressId == 0) {
            showHideDeliveryLY(true)
            binding.changeAddressBtu.performClick()
            return
        }
        if (binding.DeliverDayRecycler.visibility == View.GONE) {
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
            true, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        val message = getString(R.string.fail_to_get_data)
                        binding.loadingDelivery.visibility = View.GONE
                        if (IsSuccess) {
                            val result = obj as CheckOrderResponse?
                            if (result?.status == 200) {
                                if (result.data != null && result.data
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
                                                "Log  CheckOrderResponse AddressId  " + result.data.userAddress.id
                                            )
                                        }
                                        minimumOrderAmount = checkOrderResponse.minimumOrderAmount
//                                        deliveryFees = checkOrderResponse.deliveryCharges
                                        deliveryFees = initDeliveryFees(checkOrderResponse.deliveryCharges)
                                        println("Log deliveryFees getDeliveryTimeList $deliveryFees")
                                        checkDeliveryFees()
                                        val datesList =
                                            result.data.deliveryTimes
                                        val firstTime = datesList[0]
                                        var currentDate = firstTime.date
                                        var timesList: MutableList<DeliveryTime> = ArrayList()

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
                                        if (deliveryTimesList != null && deliveryTimesList?.size ?: 0 > 0) deliveryDateId =
                                            deliveryTimesList!![0].id
                                        deliveryDate = firstTime.date
                                        deliveryTime = firstTime.time

                                        initDaysAdapter()
                                    } else {
                                        binding.noDeliveryTv.visibility = View.VISIBLE
                                        GlobalData.Toast(activityy, message)
                                    }
                                }
                            } else {
                                binding.noDeliveryTv.visibility = View.VISIBLE
                                binding.noDeliveryTv.text = message
                                GlobalData.Toast(activityy, message)
                            }


                        } else {
                            binding.noDeliveryTv.visibility = View.VISIBLE
                            binding.noDeliveryTv.text = message
                            GlobalData.Toast(activityy, message)
                        }

                    }
                }
            }).getDeliveryTimeList(storeId, user_id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        BenefitInAppHelper.handleResult(data)
        Toast("onActivityResult")
        when (resultCode) {
            RESULT_OK -> {
                Toast(getString(R.string.payment_success))
            }

            Activity.RESULT_CANCELED -> {
                Toast(getString(R.string.payment_cancle))
            }
            else -> {
                Toast(getString(R.string.payment_fail))

            }
        }

    }

    private fun saveSuccessTransaction(transaction: Transaction?, orderId: Int?) {
        GlobalData.progressDialog(activity, R.string.text_save, R.string.please_wait_sending)
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                GlobalData.hideProgressDialog()

                var message = getString(R.string.failto_save)
                val result = obj as ResultAPIModel<String>?
                if (result != null) {
                    message = result.message
                }
                if (func == Constants.ERROR) {
                    GlobalData.errorDialog(activity, R.string.text_save, message)
                } else if (func == Constants.FAIL) {
                    GlobalData.errorDialog(activity, R.string.text_save, message)
                } else if (func == Constants.NO_CONNECTION) {
                    GlobalData.errorDialog(
                        activity,
                        R.string.rate_app,
                        getString(R.string.no_internet_connection)
                    )
                } else {
                    if (IsSuccess) {
                        GlobalData.hideProgressDialog()

                        showCompletePay()

                    } else {
                        GlobalData.hideProgressDialog()
                        GlobalData.errorDialog(
                            activity,
                            R.string.text_save,
                            getString(R.string.failto_save)
                        )
                    }
                }
            }

        }).saveSucessTransactionD(transaction, orderId)
    }

    private fun saveFailTransaction(transaction: Transaction?, orderId: Int?) {
        GlobalData.progressDialog(activity, R.string.text_save, R.string.please_wait_sending)
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                GlobalData.hideProgressDialog()

                var message = getString(R.string.failto_save)
                val result = obj as ResultAPIModel<String>?
                if (result != null) {
                    message = result.message
                }
                if (func == Constants.ERROR) {
                    GlobalData.errorDialog(activity, R.string.text_save, message)
                } else if (func == Constants.FAIL) {
                    GlobalData.errorDialog(activity, R.string.text_save, message)
                } else if (func == Constants.NO_CONNECTION) {
                    GlobalData.errorDialog(
                        activity,
                        R.string.rate_app,
                        getString(R.string.no_internet_connection)
                    )
                } else {
                    if (IsSuccess) {
                        GlobalData.hideProgressDialog()
                        if (result?.status == 200) {
                            showFailPage()

                        }

                    } else {
                        GlobalData.hideProgressDialog()
                        GlobalData.errorDialog(
                            activity,
                            R.string.text_save,
                            getString(R.string.failto_save)
                        )
                    }
                }
            }

        }).saveFailTransaction(transaction, orderId)
    }

    private fun getDeliveryTypes() {
        deliveryTypeList?.clear()
        val normal = UtilityApp.getNormalDelivery()

        deliveryTypeList?.add(
            DeliveryTypeModel(
                0,
                getString(R.string.normal_delivery),
                R.drawable.normal_delivery_black,
                R.drawable.normal_delivery_red,
                normal, deliveryFees

            )
        )
        deliveryTypeList?.add(
            DeliveryTypeModel(
                1,
                getString(R.string.click_collect),
                R.drawable.ic_click_black,
                R.drawable.ic_click_red,
                deliveryTimeDesc,
                0.0
            )
        )
        if (hasExpress) {
            deliveryTypeList?.add(
                DeliveryTypeModel(
                    2,
                    getString(R.string.express_delivery),
                    R.drawable.ic_express_black,
                    R.drawable.ic_express_red,
                    deliveryTimeDesc,
                    expressDeliveryCharge
                )
            )
        }
        initDeliveryAdapter()


    }

    private fun initDeliveryAdapter() {

        val deliveryTypeAdapter = DeliveryTypeAdapter(
            requireContext(), deliveryTypeList
        ) { obj: Any?, func: String?, IsSuccess: Boolean ->
            val deliveryTypeModel: DeliveryTypeModel = obj as DeliveryTypeModel
            deliveryType = deliveryTypeModel.id
            Log.i(javaClass.name, "Log deliveryType $deliveryType")

            when (deliveryType) {
                0 -> {
                    binding.chooseDelivery.visibility = View.VISIBLE
                    expressDelivery = false
                }
                1 -> {
                    binding.chooseDelivery.visibility = View.GONE
                    expressDelivery = false
                }
                2 -> {
                    binding.chooseDelivery.visibility = View.VISIBLE
                    expressDelivery = true
                }
            }

            showHideDeliveryTypeLY(false)
            checkData()
            checkDeliveryFees()

        }
        binding.deliveryTypeRv.adapter = deliveryTypeAdapter
    }

    private fun showErrorDialog(i: Int) {
        val okClick: ErrorMessagesDialog.Click = object : ErrorMessagesDialog.Click() {
            override fun click() {
                if (i == 1) {
                    getDeliveryInfo(storeId, addressId, false)

                } else if (i == 2) {
                    getQuickDelivery(storeId, countryId)

                }


            }
        }

        val errorMessagesDialog = ErrorMessagesDialog(
            activityy,
            R.string.fail_to_get_data,
            R.string.try_again,
            okClick,
        )
        errorMessagesDialog.show()
    }

}

