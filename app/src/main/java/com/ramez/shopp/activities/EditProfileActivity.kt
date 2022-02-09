package com.ramez.shopp.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.aminography.choosephotohelper.ChoosePhotoHelper
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator.Companion.getInstance
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack
import com.kcode.permissionslib.main.PermissionCompat
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.classes.Constants
import com.ramez.shopp.classes.GlobalData
import com.ramez.shopp.classes.GlobalData.errorDialog
import com.ramez.shopp.classes.GlobalData.hideProgressDialog
import com.ramez.shopp.classes.GlobalData.progressDialog
import com.ramez.shopp.classes.GlobalData.successDialog
import com.ramez.shopp.classes.UtilityApp
import com.ramez.shopp.Dialogs.PickImageDialog
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.FileUtil
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.databinding.ActivityEditProfileBinding
import id.zelory.compressor.Compressor
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException


class EditProfileActivity : ActivityBase() {
    var memberModel: MemberModel? = null
    var pickImageDialog: PickImageDialog? = null
    var requestPickImage = 11
    var userId = 0
    var selectedPhotoFil: File? = null
    private var country: String? = null
    private var choosePhotoHelper: ChoosePhotoHelper? = null
    private var selectedPhotoUri: Uri? = null
    var localModel: LocalModel? = null
    lateinit var   binding: ActivityEditProfileBinding
    var storeId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
        storeId = localModel?.cityId?.toInt()?:Constants.default_storeId.toInt()
        setTitle(R.string.text_title_edit_profile)
        memberModel = UtilityApp.getUserData()
        if (memberModel != null && memberModel?.id != null) {
            initData()
        } else {
            UtilityApp.logOut()
            val intent = Intent(activity, RegisterLoginActivity::class.java)
            intent.putExtra(Constants.LOGIN, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        binding.saveBut.setOnClickListener {
            if (isValidForm) {
                updateProfile()
            }
        }
        binding.editPhotoBut.setOnClickListener { openPicker() }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (choosePhotoHelper != null) choosePhotoHelper?.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == requestPickImage) {
            try {
                if (data?.data != null) {
                    selectedPhotoUri = data.data
                    selectedPhotoFil = FileUtil.from(activity, selectedPhotoUri)
                    selectedPhotoFil = Compressor(activity).setQuality(65).compressToFile(selectedPhotoFil)
                    Glide.with(activity).asBitmap().load(selectedPhotoUri).placeholder(R.drawable.avatar).into(
                        binding.userImg
                    )
                    uploadPhoto(userId, selectedPhotoFil)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorDialog(activity, R.string.upload_photo, getString(R.string.textTryAgain))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        choosePhotoHelper?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun pickImage() {
        pickImageDialog = PickImageDialog(
            activity,
            object :DataFetcherCallBack {
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
                                            binding.userImg
                                        )
                                    selectedPhotoFil =
                                        Compressor(activity).setQuality(65).compressToFile(selectedPhotoFil)

//                        Log.i("tag","Log selectedPhotoFil  " + selectedPhotoFil);
//                        Log.i("tag","Log uri "+uri);
                                    uploadPhoto(userId, selectedPhotoFil)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    errorDialog(
                                        activity,
                                        R.string.upload_photo,
                                        getString(R.string.textTryAgain)
                                    )
                                }
                            })
                        choosePhotoHelper?.takePhoto()
                    } else if ((func == Constants.PICK)) {
                        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                        startActivityForResult(
                            Intent.createChooser(intent, getString(R.string.selectImage)),
                            requestPickImage
                        )
                    }                }

            })
        try {
            pickImageDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openPicker() {
        try {
            val builder = PermissionCompat.Builder(activity)
            builder.addPermissions(
                arrayOf(
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
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

    private fun updateProfile() {
        val name = NumberHandler.arabicToDecimal(binding.edtUserName.text.toString())
        val email = NumberHandler.arabicToDecimal(binding.etEmail.text.toString())
        if (memberModel != null) {
            memberModel?.name = name
            memberModel?.email = email
        }
        progressDialog(activity, R.string.update_profile, R.string.please_wait_sending)
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                hideProgressDialog()
                if ((func == Constants.ERROR)) {
                    val result: ResultAPIModel<*>? = obj as ResultAPIModel<*>?
                    var message: String? = getString(R.string.failtoupdate_profile)
                    if ((result != null) && (result.message != null) && !result.message.isEmpty()) {
                        message = result.message
                    }
                    errorDialog(activity, R.string.failtoupdate_profile, message)
                } else if ((func == Constants.NO_CONNECTION)) {
                    errorDialog(
                        activity,
                        R.string.failtoupdate_profile,
                        getString(R.string.no_internet_connection)
                    )
                } else {
                    if (IsSuccess) {
                        val result: LoginResultModel = obj as LoginResultModel
                        val user: MemberModel = result.data
                        UtilityApp.setUserData(user)
                        successDialog(
                            activity,
                            getString(R.string.update_profile),
                            getString(R.string.success_update)
                        )
                    } else {
                        Toast(getString(R.string.failtoupdate_profile))
                    }
                }            }


        }).updateProfile(memberModel)
    }

    private fun uploadPhoto(userId: Int, photo: File?) {
        Log.i("tag", "Log  userId $userId")
        progressDialog(activity, R.string.upload_photo, R.string.please_wait_to_upload_photo)
        country = if (localModel != null) {
            localModel?.shortname
        } else {
            GlobalData.COUNTRY
        }
        val token = if (UtilityApp.getToken() != null) UtilityApp.getToken() else "token"
        AndroidNetworking.upload(
            UtilityApp.getUrl() + country + GlobalData.grocery +
                    GlobalData.Api + " v9/Account/UploadPhoto" + "?user_id=" + userId
        ).addMultipartFile("file", photo)
            .addHeaders("ApiKey", Constants.api_key)
            .addHeaders("device_type", Constants.deviceType)
            .addHeaders("app_version", UtilityApp.getAppVersionStr())
            .addHeaders("token", token)
            .setPriority(Priority.HIGH).build().setUploadProgressListener { bytesUploaded: Long, totalBytes: Long -> }
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    hideProgressDialog()
                    Log.i("tag", "Log data response $response")
                    var message: String = getString(R.string.failtoupdate_profile)
                    if ((response.toString() == Constants.ERROR)) {
                        errorDialog(activity, R.string.failtoupdate_profile, message)
                    } else {
                        var data: String? = null
                        try {
                            val status = response.getInt("status")
                            if (status == 200) {
                                data = response.getString("data")
                                Log.i("tag", "Log data result $data")
                                if (data != null) {
                                    memberModel?.profilePicture = data
                                    UtilityApp.setUserData(memberModel)
                                    successDialog(
                                        activity,
                                        getString(R.string.upload_photo),
                                        getString(R.string.success_update)
                                    )
                                }
                            } else {
                                message = response.getString("message")
                                errorDialog(activity, R.string.failtoupdate_profile, message)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onError(error: ANError) {
                    // handle error
                }
            })
    }


    fun getUserData(user_id: Int, store_id: Int) {
        DataFeacher(false,object:DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result: ResultAPIModel<ProfileData?>? =
                    obj as ResultAPIModel<ProfileData?>?
                val message: String = getString(R.string.fail_to_get_data)
                if ((func == Constants.ERROR)) {
                    UtilityApp.logOut()
                    val intent: Intent = Intent(activity, RegisterLoginActivity::class.java)
                    intent.putExtra(Constants.LOGIN, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else if ((func == Constants.FAIL)) {
                    UtilityApp.logOut()
                    val intent: Intent = Intent(activity, RegisterLoginActivity::class.java)
                    intent.putExtra(Constants.LOGIN, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else if ((func == Constants.NO_CONNECTION)) {
                    //  Toasty.error(getActiviy(),R.string.no_internet_connection, Toast.LENGTH_SHORT, true).show();
                } else if (IsSuccess) {
                    val memberModel: MemberModel = UtilityApp.getUserData()
                    if (result?.data != null) {
                        memberModel.name = result.data?.name
                        memberModel.email = result.data?.email
                        memberModel.loyalBarcode = result.data?.loyalBarcode
                        memberModel.profilePicture = result.data?.profilePicture
                        UtilityApp.setUserData(memberModel)
                        initData()
                    }
                }            }

        }).getUserDetails(user_id, store_id)
    }

    private fun initData() {
        userId = memberModel?.id?:0
        binding.userNameTv.text = memberModel?.name
        binding.edtUserName.setText(memberModel?.name?:"")
        binding.etEmail.setText(memberModel?.email)
        binding.edtPhoneNumber.text = memberModel?.mobileNumber
        Glide.with(activity).asBitmap().load(memberModel?.profilePicture).placeholder(R.drawable.avatar).into(
            binding.userImg
        )
    }

    private val isValidForm: Boolean
        get() {
            val formValidator = getInstance()
            return formValidator.addField(binding.edtUserName, NonEmptyRule(R.string.enter_name))
                .addField(binding.etEmail, NonEmptyRule(R.string.enter_email))
                .validate()
        }
}