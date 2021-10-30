package com.ramez.shopp.activities

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.jaeger.library.StatusBarUtil
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack
import com.kcode.permissionslib.main.PermissionCompat
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.CityModelResult
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.GlobalData
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.databinding.ActivityFastqScanActivityBinding
import es.dmoral.toasty.Toasty
import java.util.*


class FastqScanActivity : ActivityBase() {

    private lateinit var binding: ActivityFastqScanActivityBinding
    var localModel: LocalModel? = null
    var branchName: String = ""
    private var countryId = 0
    private var cityId = 0
    var cityModelArrayList: ArrayList<CityModel>? = null
    private var selectedCityModel: CityModel? = null
    private lateinit var codeScanner: CodeScanner
    private var user: MemberModel? = null
    private var userId = "0"
    var currency = "BHD"
    var fraction = 2
    var cartCount = 0
    var total: Double = 0.0
    var cartId: Int = 0
    var currBarcode: String? = null
    var isMakeScan: Boolean = false

    var cartProductsBarcodes: MutableSet<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFastqScanActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeToolBarColor()

        StatusBarUtil.setColor(this, ContextCompat.getColor(activiy, R.color.fastq_color), 0)

        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activiy
            )

        cartCount = UtilityApp.getFastQCartCount()
        total = UtilityApp.getFastQCartTotal().toDouble()

        cartProductsBarcodes = mutableSetOf()

        Log.i(javaClass.simpleName, "Log fast Cart count $cartCount")

        binding.scanHintLabel.visibility = visible
        binding.productDetailsLy.visibility = gone

        currency = localModel?.currencyCode ?: Constants.BHD
        fraction = localModel?.fractional ?: Constants.three

        if (UtilityApp.isLogin() && UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) {
            user = UtilityApp.getUserData()
            userId = user?.id.toString()
        }

        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()

        if (UtilityApp.getBranchName() != null) {
            setBranchData()

        } else {
            getCityList(countryId)

        }


        codeScanner = CodeScanner(this, binding.scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {

            runOnUiThread {

                try {

                    if (UtilityApp.getScanSound()) {
                        val sound = Uri.parse(
                            "android.resource://" + activiy.packageName
                                .toString() + "/" + R.raw.beep_04
                        )
                        val r = RingtoneManager.getRingtone(activiy, sound)
                        r.play()
                    }

                    checkBarcode(it.text)
//                    checkBarcode("4600680000640")
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        checkBarcode("7010000679680")
//                    },3000)
//                    Handler(Looper.getMainLooper()).postDelayed({
//                        checkBarcode("6084001011375")
//                    },6000)
////                    checkBarcode("7010000679680")
////                    checkBarcode("6084001011375")
////                    checkBarcode("5711953137518")

                } catch (e: java.lang.Exception) {
                }

            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast("Camera initialization error: ${it.message}")

            }
        }

        startScan()
        initListeners()
        getCart()


    }


    @SuppressLint("SetTextI18n")
    private fun checkCart() {

        cartCount = UtilityApp.getFastQCartCount()

        if (cartCount > 0) {
            binding.cartFastCountTv.visibility = visible
        } else {
            binding.cartFastCountTv.visibility = gone
        }

        Log.i(javaClass.simpleName, "Log total  $total  , cartCount $cartCount")
        binding.cartFastCountTv.text = cartCount.toString()

        total = UtilityApp.getFastQCartTotal().toDouble()
        binding.totalTv.text = NumberHandler.formatDouble(
            total, fraction
        ) + " " + currency


    }

    fun initListeners() {

        binding.cartBtn.setOnClickListener {

            changeToScanning()

            val intent = Intent(activiy, FastqCartActivity::class.java)
            startActivity(intent)

        }


        binding.toolBar.cartFastBtn.setOnClickListener {

            val intent = Intent(activiy, FastqCartActivity::class.java)
            startActivity(intent)

        }

        binding.scanBut.setOnClickListener {

            if (isMakeScan) {
                changeToScanning()
                startScan()
            }

        }

        binding.scannerView.setOnClickListener {

            if (isMakeScan) {
                changeToScanning()
                startScan()
            }

        }

        binding.plusBtn.setOnClickListener {

            binding.deleteButton.visibility = gone
            val quantity: Int = binding.quantityTv.text?.toString()?.toInt() ?: 1

            updateCart(cartId, quantity + 1, cityId, userId, isAdd = true, isDelete = false)


        }

        binding.minusBtn.setOnClickListener {
            val quantity: Int = binding.quantityTv.text?.toString()?.toInt() ?: 1

            if (quantity == 1) {
                updateCart(cartId, 0, cityId, userId, false, true)

            } else {
                updateCart(cartId, quantity - 1, cityId, userId, false, false)

            }


        }

    }

    private fun getCityList(country_id: Int) {
        Log.i(javaClass.name, "Log country_id$country_id")
        callCityListApi()
    }

    private fun callCityListApi() {
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        val result = obj as CityModelResult
                        if (result.data != null && result.data.size > 0) {
                            cityModelArrayList = ArrayList(result.data)
                            searchSelectedCity()
                            setBranchData()
                        }
                    }
                }

            }).CityHandle(countryId, activiy)
    }

    private fun searchSelectedCity() {
        for (cityModel in cityModelArrayList!!) {
            if (cityId == cityModel.id) {
                selectedCityModel = cityModel
                break
            }
        }
    }

    private fun setBranchData() {
        if (selectedCityModel != null) {
            branchName = selectedCityModel!!.cityName
            Log.i(javaClass.name, "Log branchName $branchName")
            title = branchName
        } else {
            branchName = UtilityApp.getBranchName()
            Log.i(javaClass.name, "Log branchName $branchName")
            title = branchName
        }
    }

    private fun changeToolBarColor() {

        binding.toolBar.toolbarBack.setBackgroundColor(ContextCompat.getColor(activiy, R.color.fastq_color))
        binding.toolBar.logoImg.visibility = gone
        binding.toolBar.mainTitleTv.visibility = visible

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()

        if (GlobalData.refresh_cart) {
            GlobalData.refresh_cart = false
            getCart()
        } else {
            checkCart()
        }

    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun startScan() {
        try {
            val builder = PermissionCompat.Builder(activiy)
            builder.addPermissions(arrayOf(Manifest.permission.CAMERA))
            builder.addPermissionRationale(getString(R.string.should_allow_permission))
            builder.addRequestPermissionsCallBack(object : OnRequestPermissionsCallBack {
                override fun onGrant() {
                    codeScanner.startPreview()
                }

                override fun onDenied(permission: String) {
                    Toast(R.string.some_permission_denied)
                }
            })
            builder.build().request()
        } catch (var2: Exception) {
            var2.printStackTrace()
        }
    }

    private fun addToCartUsingBarcode(
        city_id: Int,
        user_id: String?,
        barcode: String?
    ) {
        GlobalData.progressDialog(activiy, R.string.getproduct, R.string.please_wait_upload)

        DataFeacher(false,
            object : DataFetcherCallBack {

                @SuppressLint("SetTextI18n")
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                    GlobalData.hideProgressDialog()

                    val result = obj as ScanResult?
                    var message: String? = activiy.getString(R.string.fail_to_get_data)
                    if (func == Constants.ERROR) {
                        message = result?.message ?: getString(R.string.fail_to_get_data)

                        Toast(message)
                    } else {
                        if (IsSuccess) {
                            if (result?.data != null) {

                                currBarcode = barcode

                                val productModel = result.data
                                cartId = productModel.id
                                val name: String? = productModel.name()
                                val price: Double = productModel.price
                                val quantity: Int = binding.quantityTv.text?.toString()?.toInt() ?: 1
                                val currentTotal = price.times(quantity)

                                binding.productNameTv.text = name


                                binding.priceTv.text = NumberHandler.formatDouble(
                                    price, fraction
                                ) + " " + currency

                                cartCount += 1
                                total += currentTotal

                                binding.totalTv.text = NumberHandler.formatDouble(
                                    total, fraction
                                ) + " " + currency

                                binding.cartFastCountTv.text = cartCount.toString()

                                binding.scanHintLabel.visibility = gone
                                binding.cartFastCountTv.visibility = visible
                                binding.productDetailsLy.visibility = visible

                                UtilityApp.setFastQCartCount(cartCount)
                                UtilityApp.setFastQCartTotal(total.toFloat())

                                cartProductsBarcodes?.add(productModel.barcode ?: "")

                                showSuccess()

                                if (UtilityApp.getContinuousScan()) {
                                    startScan()
                                } else {
                                    changeToScanAgain()
                                }


                            } else {
                                message = result?.message ?: getString(R.string.fail_to_get_data)
                                Toast(message)
                            }
                        } else {
                            message = result?.message ?: getString(R.string.fail_to_get_data)

                            Toast(message)

                        }
                    }
                }


            }).addToFastQCart(city_id, user_id, barcode)
    }

    fun showSuccess() {
        binding.successAddProductLy.visibility = visible
        if (UtilityApp.getContinuousScan()) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.successAddProductLy.animate().alpha(0f)
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            binding.successAddProductLy.visibility = gone

                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationRepeat(animation: Animator?) {

                        }
                    })
            }, 2000)

        }
    }

    private fun changeToScanAgain() {
        isMakeScan = true
        binding.scanBut.background = ContextCompat.getDrawable(activiy, R.drawable.round_corner_red_fill_big)
        binding.processTv.text = getString(R.string.scan_again)
    }

    private fun changeToScanning() {
        isMakeScan = false
        binding.scanHintLabel.visibility = visible
        binding.productDetailsLy.visibility = gone
        binding.successAddProductLy.visibility = gone
        binding.scanBut.background =
            ContextCompat.getDrawable(activiy, R.drawable.round_corner_green_fill_big)
        binding.processTv.text = getString(R.string.scanning)
    }


    private fun updateCart(
        cartId: Int,
        quantity: Int,
        cityId: Int,
        userId: String?,
        isAdd: Boolean,
        isDelete: Boolean
    ) {

        DataFeacher(false,
            object : DataFetcherCallBack {

                @SuppressLint("SetTextI18n")
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    val result = obj as ResultAPIModel<ScanModel>?

                    var message: String? = activiy.getString(R.string.fail_to_update_cart)
                    if (func == Constants.ERROR) {
                        if (result?.message != null) {
                            message = result.message
                        }
                        Toast(message)
                    } else {
                        if (IsSuccess) {
                            if (result?.data != null) {
                                val productModel = result.data
                                val price: Double = productModel.price
                                binding.quantityTv.text = quantity.toString()

                                when {
                                    isAdd -> {
                                        total = total.plus(price)

                                    }
                                    isDelete -> {

                                        total = total.minus(price)
                                        cartCount = cartCount.minus(1)
                                        UtilityApp.setFastQCartCount(cartCount)
                                        changeToScanning()
                                        cartProductsBarcodes?.remove(currBarcode ?: "")
                                    }
                                    else -> {
                                        total = total.minus(price)
                                    }
                                }

                                UtilityApp.setFastQCartTotal(total.toFloat())

                                checkCart()

                            } else {
                                Toast(message)
                            }
                        } else {
                            Toast(message)

                        }
                    }
                }


            }).updateCartFastQ(cartId, quantity, cityId, userId)
    }

    fun getCart() {

        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                GlobalData.hideProgressDialog()
                if (IsSuccess) {
                    cartProductsBarcodes?.clear()

                    val cartFastQModel = obj as ResultAPIModel<ArrayList<CartFastQModel?>?>?

                    if (cartFastQModel?.status == 200 && cartFastQModel.data?.isNotEmpty() == true) {

                        val productsList = cartFastQModel.data ?: mutableListOf()

                        val data = cartFastQModel.data
                        val fastCartNumber = data?.size ?: 0
                        UtilityApp.setFastQCartCount(fastCartNumber)
                        calculateSubTotalPrice(data)

                        for (product in productsList) {
                            Log.i(javaClass.simpleName, "Log barcode $product ")

                            cartProductsBarcodes?.add(product?.barcode ?: "")

                        }

                    } else {
                        Log.i(javaClass.name, "Log fast Cart count 0")

                        UtilityApp.setFastQCartCount(0)
                        UtilityApp.setFastQCartTotal(0f)
                    }

                    checkCart()


                }
            }

        }).getFastQCarts(cityId, userId)

    }

    private fun checkBarcode(barcode: String) {

        if (cartProductsBarcodes?.contains(barcode) == true) {
            Toasty.error(activiy, getString(R.string.product_added_to_cart), Toast.LENGTH_SHORT, true).show()
            startScan()
        } else {
            addToCartUsingBarcode(cityId, userId, barcode)
        }

    }


    fun calculateSubTotalPrice(cartList: ArrayList<CartFastQModel?>?): Double {
        var subTotal = 0.0
        for (product in cartList ?: mutableListOf()) {

            subTotal = subTotal.plus(product?.price?.times(product.qty) ?: 0.0)

        }
        UtilityApp.setFastQCartTotal(subTotal.toFloat())
        println("Log subTotal result $subTotal")
        return subTotal
    }


}