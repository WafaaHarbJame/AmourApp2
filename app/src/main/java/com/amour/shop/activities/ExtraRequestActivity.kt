package com.amour.shop.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.single.PermissionListener
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack
import com.kcode.permissionslib.main.PermissionCompat
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.BuildConfig
import com.amour.shop.Dialogs.PickImageDialog
import com.amour.shop.Models.AddExtraCall
import com.amour.shop.Models.LocalModel
import com.amour.shop.Models.MemberModel
import com.amour.shop.R
import com.amour.shop.Utils.FileUtil
import com.amour.shop.classes.Constants
import com.amour.shop.classes.Constants.MAIN_ACTIVITY_CLASS
import com.amour.shop.classes.GlobalData
import com.amour.shop.classes.GlobalData.REFRESH_CART
import com.amour.shop.classes.GlobalData.errorDialog
import com.amour.shop.classes.GlobalData.hideProgressDialog
import com.amour.shop.classes.GlobalData.progressDialog
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.ActivityExtraRequestBinding
import id.zelory.compressor.Compressor
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class ExtraRequestActivity : ActivityBase() {
   private lateinit var binding: ActivityExtraRequestBinding
    var pickImageDialog: PickImageDialog? = null
    var REQUEST_PICK_IMAGE = 11
    var selectedPhotoFil: File? = null
    private var count = 1
    private var userId = 0
    private var storeId = 0
    private var user: MemberModel? = null
    private var localModel: LocalModel? = null
    private var choosePhotoHelper: ChoosePhotoHelper? = null
    private var selectedPhotoUri: Uri? = null
    private val SEARCH_CODE = 2000
    private var country: String? = null
    private var CODE: String? = ""
    private var accessToken: String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExtraRequestBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        title = ""
        user = UtilityApp.getUserData()
        localModel = UtilityApp.getLocalData()
        if (UtilityApp.getUserData() != null) {
            storeId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
            userId = UtilityApp.getUserData()?.id?:0
        }
         accessToken = UtilityApp.getUserToken()

        initListener()
    }

    private fun initListener() {
        binding.plusCartBtn.setOnClickListener { v ->
            count = binding.productCartQTY.text.toString().toInt()
            if (count == 1) {
                binding.minusCartBtn.visibility = View.VISIBLE
                binding.deleteCartBtn.visibility = View.GONE
            }
            count++
            binding.productCartQTY.text = count.toString()
        }
        binding.minusCartBtn.setOnClickListener { v ->
            count = binding.productCartQTY.text.toString().toInt()
            binding.productCartQTY.text = count.toString()
            if (count == 1) {
                binding.minusCartBtn.visibility = View.GONE
                binding.deleteCartBtn.visibility = View.VISIBLE
            } else {
                binding.minusCartBtn.visibility = (View.VISIBLE)
                binding.deleteCartBtn.visibility = (View.GONE)
                count--
                if (count == 1) {
                    binding.minusCartBtn.visibility = (View.GONE)
                    binding.deleteCartBtn.visibility = (View.VISIBLE)
                }
            }
            binding.plusCartBtn.visibility = (View.VISIBLE)
            binding.productCartQTY.visibility = (View.VISIBLE)
            binding.productCartQTY.text = count.toString()
        }
        binding.addToCartBtn.setOnClickListener { v ->
            if (userId > 0) {
                count = binding.productCartQTY.text.toString().toInt()
                val addExtraCall: AddExtraCall = AddExtraCall()
                if (Objects.requireNonNull(binding.tvProductDesc.text).toString()
                            .isEmpty()
                ) {
                    Toast(getString(R.string.please_add_desc))
                } else {
                    addExtraCall.description = binding.tvProductDesc.text.toString()
                    addExtraCall.userId = userId
                    addExtraCall.barcode = CODE
                    addExtraCall.qty = binding.productCartQTY.text.toString().toInt()
                    addExtraCall.storeId = storeId
                    if (selectedPhotoFil != null) {
                        AddRequestWithPhoto(addExtraCall, selectedPhotoFil!!)
                    } else {
                        addRequestWithOutPhoto(addExtraCall)
                    }
                }
            } else {
                UtilityApp.logOut()
                val intent: Intent = Intent(activity, RegisterLoginActivity::class.java)
                intent.putExtra(Constants.LOGIN, true)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        binding.deleteCartBtn.setOnClickListener { v -> }
        binding.addImage.setOnClickListener { view -> openPicker() }
        binding.scanBarcode.setOnClickListener { v ->
            hideSoftKeyboard(activity)
            checkCameraPermission()
        }
    }

    private fun openPicker() {
        try {
            val builder = PermissionCompat.Builder(activity)
            builder.addPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            builder.addPermissionRationale(getString(R.string.should_allow_permission))
            builder.addRequestPermissionsCallBack(object : OnRequestPermissionsCallBack {
                override fun onGrant() {
                    pickImage()
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

    private fun pickImage() {
        pickImageDialog = PickImageDialog(activity, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if ((func == Constants.CAPTURE)) {
                    choosePhotoHelper = ChoosePhotoHelper.with(activity).asUri()
                        .build(ChoosePhotoCallback { uri: Uri? ->
                            selectedPhotoUri = uri
                            try {
                                selectedPhotoFil = FileUtil.from(activity, uri)
                                Glide.with(activity).asBitmap().load(selectedPhotoUri)
                                    .placeholder(R.drawable.avatar)
                                    .into(
                                        binding.addImage
                                    )
                                selectedPhotoFil =
                                    Compressor(activity).setQuality(65).compressToFile(selectedPhotoFil)
                                //                        Log.i(getClass().getSimpleName(), "Log Image Size "+selectedPhotoFil.getPath().length());
//
//                        long length = selectedPhotoFil.length();
//                        length = length/1024;
//
//                        Log.i(getClass().getSimpleName(), "Log File Path "+selectedPhotoFil.getPath() + ", File size : " + length +" KB");
//                        Log.i(getClass().getSimpleName(), "Log Image Size "+selectedPhotoFil.getPath().length());
                            } catch (e: IOException) {
                                e.printStackTrace()
                                errorDialog(
                                    activity,
                                    R.string.upload_photo,
                                    getString(R.string.textTryAgain)
                                )
                            }
                        })
                    choosePhotoHelper!!.takePhoto()
                } else if ((func == Constants.PICK)) {
                    val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.selectImage)),
                        REQUEST_PICK_IMAGE
                    )
                }

                try {
                    activity
                    if (!activity.isFinishing) {
                        pickImageDialog!!.show()
                    }
                } catch (e: Exception) {
                    pickImageDialog!!.dismiss()
                }
            }

        })
    }

    private fun navigateToCartScreen() {
        REFRESH_CART = true
        val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
        intent.putExtra(Constants.CART, true)
        startActivity(intent)
        finish()
    }

    @SuppressLint("SetTextI18n")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (choosePhotoHelper != null) choosePhotoHelper!!.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_PICK_IMAGE) {
            try {
                if (data!!.data != null) {
                    selectedPhotoUri = data.data
                    selectedPhotoFil = FileUtil.from(activity, selectedPhotoUri)
                    selectedPhotoFil = Compressor(activity).setQuality(65).compressToFile(selectedPhotoFil)
                    var length = selectedPhotoFil?.length()
                    length = length?.div(1024)
                    Log.i(
                        javaClass.simpleName,
                        "Log File Path " + selectedPhotoFil?.path + ", File size : " + length + " KB"
                    )
                    Log.i(javaClass.simpleName, "Log Image Size " + (selectedPhotoFil?.path?.length ?: 0))
                    Glide.with(activity).asBitmap().load(selectedPhotoUri).placeholder(R.drawable.avatar)
                        .into(
                            binding.addImage
                        )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorDialog(activity, R.string.upload_photo, getString(R.string.textTryAgain))
            }
        } else if (requestCode == SEARCH_CODE) {
            if (data != null) {
                CODE = data.getStringExtra(Constants.CODE)
                binding.barcodeTv.text = getString(R.string.Barcode).toString() + " " + CODE
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        choosePhotoHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun AddRequestWithPhoto(addExtraCall: AddExtraCall, photo: File) {
        Log.i("tag", "Log  userId " + addExtraCall.userId)
        progressDialog(activity, R.string.add_specail_order, R.string.please_wait_to_add_request)
        country = if (localModel?.shortname != null) {
            localModel?.shortname
        } else {
            GlobalData.COUNTRY
        }
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
        val token = UtilityApp.getToken()

        country=Constants.default_amour_short_name


        AndroidNetworking.upload(
            UtilityApp.getUrl() + country + GlobalData.amour +
                    GlobalData.Api + "v9/Carts/AddExtrat"
        ).addMultipartFile("file", photo)
            .addHeaders("ApiKey", Constants.api_key)
            .addHeaders("Authorization", Constants.TOKEN_PREFIX + accessToken)
            .addHeaders("device_type", Constants.deviceType)
            .addHeaders("app_version", UtilityApp.getAppVersionStr())
            .addHeaders("token", token)
            .addQueryParameter("qty", addExtraCall.qty.toString())
            .addQueryParameter("barcode", addExtraCall.barcode.toString())
            .addQueryParameter("description", addExtraCall.description.toString())
            .addQueryParameter("user_id", addExtraCall.userId.toString())
            .addQueryParameter("store_id", addExtraCall.storeId.toString())
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().setUploadProgressListener { bytesUploaded: Long, totalBytes: Long -> }
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.i("tag", "Log  addExtraCall $addExtraCall")
                    hideProgressDialog()
                    Log.i("tag", "Log data response $response")
                    var message: String = getString(R.string.fail_to_add_extra_order)
                    try {
                        val jsonObject = response
                        val status = jsonObject.getInt("status")
                        if (status == 200) {
                            UtilityApp.updateCart(1, 1)
                            val successDialog = AwesomeSuccessDialog(activity)
                            successDialog.setTitle(R.string.add_specail_order)
                                .setMessage(R.string.success_update)
                                .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                                .setDialogIconAndColor(R.drawable.ic_check, R.color.white).show()
                                .setOnDismissListener { dialogInterface: DialogInterface? -> navigateToCartScreen() }
                            successDialog.show()
                        } else {
                            message = jsonObject.getString("message")
                            errorDialog(activity, R.string.add_specail_order, message)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: ANError) {
                    if (error.message != null) {
                        errorDialog(activity, R.string.add_specail_order, error.message)
                    }
                }
            })
    }

    private fun addRequestWithOutPhoto(addExtraCall: AddExtraCall) {
        Log.i("tag", "Log  userId " + addExtraCall.userId)
        progressDialog(activity, R.string.add_specail_order, R.string.please_wait_to_add_request)
        country = if (localModel?.shortname != null) {
            localModel?.shortname
        } else {
            GlobalData.COUNTRY
        }
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
        val token = if (UtilityApp.getToken() != null) UtilityApp.getToken() else "token"

        country=Constants.default_amour_short_name

        AndroidNetworking.post(
            (UtilityApp.getUrl() + country + GlobalData.amour +
                    GlobalData.Api + "v9/Carts/AddExtrat")
        )
            .addHeaders("ApiKey", Constants.api_key)
            .addHeaders("device_type", Constants.deviceType)
            .addHeaders("app_version", UtilityApp.getAppVersionStr())
            .addHeaders("token", token)
            .addHeaders("Authorization", Constants.TOKEN_PREFIX + accessToken)
            .addQueryParameter("qty", addExtraCall.qty.toString())
            .addQueryParameter("barcode", addExtraCall.barcode.toString())
            .addQueryParameter("description", addExtraCall.description.toString())
            .addQueryParameter("user_id", addExtraCall.userId.toString())
            .addQueryParameter("store_id", addExtraCall.storeId.toString())

            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
            .build().setUploadProgressListener { bytesUploaded: Long, totalBytes: Long -> }
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    hideProgressDialog()
                    Log.i("tag", "Log data response $response")
                    var message: String = getString(R.string.fail_to_add_extra_order)
                    try {
                        val jsonObject = response
                        val status = jsonObject.getInt("status")
                        if (status == 200) {
                            UtilityApp.updateCart(1, 1)
                            val successDialog = AwesomeSuccessDialog(activity)
                            successDialog.setTitle(R.string.add_specail_order)
                                .setMessage(R.string.success_update)
                                .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                                .setDialogIconAndColor(R.drawable.ic_check, R.color.white).show()
                                .setOnDismissListener { dialogInterface: DialogInterface? -> navigateToCartScreen() }
                            successDialog.show()
                        } else {
                            message = jsonObject.getString("message")
                            errorDialog(activity, R.string.add_specail_order, message)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                override fun onError(error: ANError) {
                    if (error.errorCode== 404 || error.errorCode == 443) {
                        var message =""
                        if (error.message != null) {
                            message=error.message.toString()
                        }
                        errorDialog(activity, R.string.add_specail_order, message)



                    }


                }
            })
    }

    private fun changeUrl() {
        val url = UtilityApp.getUrl()
        if (url == GlobalData.BetaBaseRamezURL1) {
            UtilityApp.setUrl(GlobalData.BetaBaseRamezURL2)
        } else {
            UtilityApp.setUrl(GlobalData.BetaBaseRamezURL1)
        }
    }

    private fun checkCameraPermission() {
        Dexter.withContext(activity).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                @RequiresApi(api = Build.VERSION_CODES.M)
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    startScan()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        activity,
                        "" + activity.getString(R.string.permission_camera_rationale),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener { error: DexterError? ->
                Toast.makeText(
                    activity,
                    "" + activity.getString(R.string.error_in_data),
                    Toast.LENGTH_SHORT
                ).show()
            }.onSameThread().check()
    }

    private fun startScan() {
        if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                ZBAR_CAMERA_PERMISSION
            )
        } else {
            val intent = Intent(activity, FullScannerActivity::class.java)
            startActivityForResult(intent, SEARCH_CODE)
        }
    }

    companion object {
        private val ZBAR_CAMERA_PERMISSION = 1
    }
}