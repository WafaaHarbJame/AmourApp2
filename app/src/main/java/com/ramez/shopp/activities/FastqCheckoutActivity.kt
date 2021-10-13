package com.ramez.shopp.activities

import android.Manifest
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.jaeger.library.StatusBarUtil
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack
import com.kcode.permissionslib.main.PermissionCompat
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.GlobalData
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.CartFastQModel
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.Models.ResultAPIModel
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.databinding.ActivityFastqCheckoutActivityBinding
import java.lang.Exception


class FastqCheckoutActivity : ActivityBase() {

    private lateinit var binding: ActivityFastqCheckoutActivityBinding
    var localModel: LocalModel? = null
    private var countryId = 0
    private var cityId = 0
    private var cashierBarcode = ""
    private lateinit var codeScanner: CodeScanner
    var userId = 0
    private var list: MutableList<CartFastQModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFastqCheckoutActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeToolBarColor()

        if (UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) {
            userId = UtilityApp.getUserData()?.id ?: 0
        }

        StatusBarUtil.setColor(this, ContextCompat.getColor(activiy, R.color.fastq_color), 0)

        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activiy
            )


        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        title = getString(R.string.Pay_at_Checkout)

        getFastQCarts(cityId, userId)


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

                    if(UtilityApp.getScanSound()){
                        val sound = Uri.parse(
                            "android.resource://" + activiy.packageName
                                .toString() + "/" + R.raw.beep_04
                        )
                        val r = RingtoneManager.getRingtone(activiy, sound)
                        r.play()
                    }

                    cashierBarcode = it.text
                    generateOrder(cityId, userId, cashierBarcode)


                } catch (e: Exception) {
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

    }

    fun initListeners() {

        binding.inStoreLabel.setOnClickListener {

            val intent = Intent(activiy, FastqSummaryActivity::class.java)
            startActivity(intent)

        }

    }


    private fun changeToolBarColor() {
        binding.toolBar.toolbarBack.setBackgroundColor(ContextCompat.getColor(activiy, R.color.fastq_color))
        binding.toolBar.logoImg.visibility = gone
        binding.toolBar.mainTitleTv.visibility = visible

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

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()

    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun generateOrder(cityId: Int, userId: Int, cashierBarcode: String) {

        GlobalData.progressDialog(activiy, R.string.make_order, R.string.please_wait_sending)

        DataFeacher(false, object : DataFetcherCallBack {

            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                GlobalData.hideProgressDialog()
                if (IsSuccess) {

                    val result: ResultAPIModel<String> = obj as ResultAPIModel<String>

                    if (result.status == 200) {
                        UtilityApp.setFastQCartTotal(0.0F)
                        UtilityApp.setFastQCartCount(0)

                        val productsList = list?.toTypedArray()
                        val intent = Intent(activiy, FastqSummaryActivity::class.java)
                        intent.putExtra(Constants.CART_MODEL, productsList)
                        startActivity(intent)


                    } else {
                        Toast(getString(R.string.fail_to_send_order))
                    }


                }
            }

        }).generateOrders(cityId, userId.toString(), cashierBarcode)

    }


    private fun getFastQCarts(storeId: Int, userId: Int) {

        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {

                    val cartFastQModels = obj as ResultAPIModel<MutableList<CartFastQModel>?>?

                    if (cartFastQModels?.status == 200 && cartFastQModels.data?.isNotEmpty() == true) {

                        list = cartFastQModels.data


                    } else {
                        Log.i(javaClass.name, "Log fast Cart count 0")

                        UtilityApp.setFastQCartCount(0)
                    }


                }
            }

        }).getFastQCarts(storeId, userId.toString())
    }




}