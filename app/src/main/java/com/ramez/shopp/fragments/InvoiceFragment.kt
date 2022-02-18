package com.ramez.shopp.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
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
import mobi.foo.benefitinapp.Application
import mobi.foo.benefitinapp.data.Transaction
import mobi.foo.benefitinapp.listener.BenefitInAppButtonListener
import mobi.foo.benefitinapp.listener.CheckoutListener
import mobi.foo.benefitinapp.utils.BenefitInAppCheckout
import mobi.foo.benefitinapp.utils.BenefitInAppHelper
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
    var deliveryTimesList: List<DeliveryTime>? = null
    var productList: ArrayList<CartModel>? = null
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
    var deliveryTypeList: MutableList<DeliveryTypeModel>? = null
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
    var selectedDeliveryType: DeliveryTypeModel? = null
    private var addressId = 0
    var deliveryDateId = 0
    var itemNotFoundId = 0
    var countryCode = ""
    var payToken: String? = ""
    var deliveryType: Int = 0
    private var paymentLauncher: ActivityResultLauncher<Intent>? = null
    private var ccmLauncher: ActivityResultLauncher<Intent>? = null
    var ordersDM: OrderModel? = null
    lateinit var binding: FragmentInvoiceBinding
    var paymentDialog:PaymentDialog?=null

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
        DayList = ArrayList()
        productCheckerList = ArrayList()
        fraction = localModel?.fractional ?: Constants.three
        minimum_order_amount = localModel?.minimum_order_amount ?: 0
        storeId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        currency = localModel?.currencyCode ?: Constants.BHD
        userId = UtilityApp.getUserData().id
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

        val deliveryLinearLayoutManager = LinearLayoutManager(activity)
        binding.deliveryTypeRv.layoutManager = deliveryLinearLayoutManager
        binding.deliveryTypeRv.setHasFixedSize(true)
        binding.deliveryTypeRv.animation = null
        binding.deliveryTypeRv.layoutManager = deliveryLinearLayoutManager

        getDeliveryTypes()

        extraIntent

        getDeliveryTimeList(storeId, userId!!)
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

    @SuppressLint("NotifyDataSetChanged")
    private fun initListener() {
        binding.saveBut.setOnClickListener { view1 ->
//            EventBus.getDefault()
//                .post(MessageEvent(MessageEvent.TYPE_POSITION, 0))
        }


        binding.sendOrder.setOnClickListener {

//            if (selectedPaymentMethod == null) {
//                Toast(R.string.please_select_payment_method)
//                return@setOnClickListener
//            }
//            if (selectedPaymentMethod!!.shortname != "CC" && addressId == 0) {
//                Toast(R.string.choose_address)
//                binding.tvFullAddress.isFocusable = true
//                binding.tvFullAddress.error = getString(R.string.choose_address)
//                return@setOnClickListener
//            }
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
                if (total?.toDouble() ?: 0.0 >= minimum_order_amount) {
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
                deliveryType=2
                if (deliveryDayAdapter != null) deliveryDayAdapter!!.lastIndex = -1
                deliveryDayAdapter?.notifyDataSetChanged()
                binding.DeliverTimeRecycler.visibility = View.GONE
                binding.quickLy.background = ContextCompat.getDrawable(
                    activityy,
                    R.drawable.round_corner_white_fill_green_border
                )


                binding.totalTv.text =
                    NumberHandler.formatDouble(total?.toDouble() ?: 0.plus(expressDeliveryCharge), fraction)
                        .plus(" " + currency)
//                binding.totalTv.text = NumberHandler.formatDouble(
//                    total!!.toDouble() + expressDeliveryCharge,
//                    fraction
//                ) + " " + currency


                if (expressDeliveryCharge == 0.0 || expressDeliveryCharge == 0.00 || expressDeliveryCharge == 0.00) {
                    binding.deliveryFees.text = getString(R.string.free)
                    binding.freeDelivery.text = getString(R.string.over1)
                    binding.deliveryPrice.text = getString(R.string.free)

                } else {


                    binding.deliveryFees.text = NumberHandler.formatDouble(
                        expressDeliveryCharge,
                        fraction
                    ).plus(" " + localModel?.currencyCode)

//                    binding.deliveryPrice.text = "$expressDeliveryCharge  $ currency"

                }

            } else {
                removeExpress()
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

    private fun removeExpress() {
        expressDelivery = false
        binding.quickLy.background = ContextCompat.getDrawable(
            activityy,
            R.drawable.round_corner_gray_border_fill
        )
        binding.totalTv.text = NumberHandler.formatDouble(
            (total?.toDouble()?.plus(deliveryFees) ?: 0), fraction
        ).plus(currency)

        checkDeliveryFees()
    }

    private fun initTimesList() {
        initTimeAdapter(deliveryFees)
    }

    private fun showPaymentDialog() {
         paymentDialog =
            PaymentDialog(
                activity,object:DataFetcherCallBack{
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                         selectedPaymentMethod= obj as PaymentModel?
                        if (selectedPaymentMethod != null) {

                            when (selectedPaymentMethod?.shortname) {
                                "CC" -> {
                                    binding.chooseDelivery.visibility = View.GONE
                                    initTimeAdapter(0.0)
                                    binding.deliveryFees.text = getString(R.string.free)
                                    binding.totalTv.text = NumberHandler.formatDouble(
                                        NumberHandler.formatDouble(
                                            total!!.toDouble(), fraction
                                        ).toDouble() + 0.0, fraction
                                    ).plus(" ").plus(currency)

                                }


                                "CCM" -> {
                                    val intent = Intent(activityy, PayUsingCardActivity::class.java)
                                    ccmLauncher!!.launch(intent)


                                }
                                else -> {
                                    binding.chooseDelivery.visibility = View.VISIBLE
                                    initTimeAdapter(deliveryFees)
                                    binding.deliveryFees.text = NumberHandler.formatDouble(
                                        deliveryFees,
                                        fraction
                                    ).plus(" ").plus(currency)

                                    binding.totalTv.text = NumberHandler.formatDouble(
                                        total!!.toDouble() + deliveryFees,
                                        fraction
                                    ).plus(" ").plus(currency)
                                }
                            }
                            checkData()
                            checkDeliveryFees()
                            sendOrder()
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
                val amount = total
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

//                   if (benefitInAppCheckout.checkParams() == -1)
//                       benefitInAppCheckout.


            }

            override fun onFail(p0: Int) {
                Toast(getString(R.string.error))
                Log.i(javaClass.name, "Log referenceId ${ordersDM?.orderCode}")
                Log.i(javaClass.name, "Log referenceId Constants  ${Constants.referenceId}")

                Log.i(javaClass.name, "Log onFail  ${p0}")
            }
        }


    }
    fun sendOrder () {
        val orderCall = OrderCall()
//            orderCall.user_id = userId!!
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
                productList = cartResultModel!!.data.cartData
                binding.productsSizeTv.text = "$total $currency"
                binding.totalTv.text = NumberHandler.formatDouble(
                    total!!.toDouble().plus(deliveryFees),
                    fraction
                ).plus("  $currency")

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
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                GlobalData.hideProgressDialog()
                var message: String? = getString(R.string.fail_to_send_order)
                val result = obj as OrdersResultModel?
                if (func == Constants.ERROR) {
                    if (result != null && result.message != null) {
                        message = result.message
                    }
                    GlobalData.errorDialog(activityy, R.string.make_order, message)
                } else {
                    if (IsSuccess) {
                        Log.i(tag, "Log status " + result?.status)

                        when (result?.status) {
                            200 -> {

                    //                            UtilityApp.setCartCount(0)

                                Log.i(tag, "Log status " + result.status)

                                AnalyticsHandler.PurchaseEvent(
                                    couponCodeId,
                                    currency,
                                    selectedPaymentMethod!!.id,
                                    deliveryFees,
                                    result.orderId.toString(),
                                    total
                                )
                                ordersDM = OrderModel()
                                ordersDM?.orderId = result.orderId
                                ordersDM?.orderCode = result.orderCode
                                ordersDM?.deliveryDate = deliveryDate
                                ordersDM?.deliveryTime = deliveryTime
                                Log.i(javaClass.name, "Log referenceId after  ${result.orderCode}")
                                Log.i(
                                    javaClass.name,
                                    "Log selectedPaymentMethod?.shortname  ${selectedPaymentMethod?.shortname}"
                                )


                                when (selectedPaymentMethod?.shortname) {
                                    "BNP" -> {
                                        val intent = Intent(activityy, PayWebViewActivity::class.java)
                                        intent.putExtra(PayWebViewActivity.KEY_WEB_URL, result.paymentResp.result)
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
        getDeliveryTimeList(storeId, userId!!)
        getQuickDelivery(storeId, localModel!!.countryId)
        defaultAddress
        getProductCheckerList()
        checkData()
    }

    private fun showHideDeliveryTypeLY(show: Boolean) {
        if (show) {
            binding.paymentArrowImg.setImageDrawable(
                ContextCompat.getDrawable(
                    activityy, R.drawable.ic_angle_up
                )
            )
            binding.deliveryTypeRv.visibility = View.VISIBLE
        } else {
            binding.paymentArrowImg.setImageDrawable(
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

    private fun getQuickDelivery(storeId: Int, countryId: Int) {
        val quickCall1 = QuickCall()
        quickCall1.store_id = storeId
        quickCall1.country_id = countryId
        DataFeacher(
            false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        var message = getString(R.string.fail_to_get_data)
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

                                            binding.deliveryPrice.text =  NumberHandler.formatDouble(expressDeliveryCharge,fraction).plus(""+currency)

//                                                NumberHandler.formatDouble(
//                                                expressDeliveryCharge,
//                                                fraction
//                                            ) + " " + currency


                                        }
                                    } else {
                                        binding.quickLy.visibility = View.GONE
                                    }
                                }
                            }
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
                                    val totalPrice =
                                        minimum_order_amount - total!!.toDouble()
                                    binding.freeBut.text =
                                        getString(R.string.add).plus(" ").plus(
                                            NumberHandler.roundDouble(
                                                totalPrice
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
        productCheckerList!!.add(
            ProductChecker(
                1,
                getString(R.string.product_not_found_1)
            )
        )
        productCheckerList!!.add(
            ProductChecker(
                2,
                getString(R.string.product_not_found_2)
            )
        )
        productCheckerList!!.add(
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
        if (selectedPaymentMethod!!.shortname != "CC" && addressId == 0) {
            showHideDeliveryLY(true)
            binding.changeAddressBtu.performClick()
            return
        }

//        if (deliveryDateId == 0) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        BenefitInAppHelper.handleResult(data)
        Toast("onActivityResult")
        when (resultCode) {
            RESULT_OK -> {

                val messageFromResponse: String? = data?.getStringExtra(
                    Application.ujco("倴鱝栝澵擿뽺鷶") ?: getString(R.string.payment_success)
                )
                Toast(messageFromResponse)


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
        deliveryTypeList?.add(DeliveryTypeModel(0,getString(R.string.normal_delivery),getString(R.string.normal_delivery),R.drawable.normal_delivery))
        deliveryTypeList?.add(DeliveryTypeModel(1,getString(R.string.click_collect),getString(R.string.click_collect),R.drawable.click_collect))
        initDeliveryAdapter()
        initData()

    }

    private fun  initDeliveryAdapter() {

        val deliveryTypeAdapter = DeliveryTypeAdapter(
            requireContext(), deliveryTypeList
        ) { obj: Any?, func: String?, IsSuccess: Boolean ->
            val deliveryTypeModel:DeliveryTypeModel = obj as DeliveryTypeModel
            deliveryType = deliveryTypeModel.id
            showHideDeliveryTypeLY(false)
            checkData()
            checkDeliveryFees()

        }
        binding.deliveryTypeRv.adapter = deliveryTypeAdapter
    }

}

