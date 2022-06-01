package com.amour.shop.fragments

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator.Companion.getInstance
import com.onesignal.OneSignal
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.Models.*
import com.amour.shop.R
import com.amour.shop.Utils.NumberHandler
import com.amour.shop.activities.ConfirmActivity
import com.amour.shop.activities.ConfirmPhoneActivity
import com.amour.shop.classes.Constants
import com.amour.shop.classes.Constants.MAIN_ACTIVITY_CLASS
import com.amour.shop.classes.GlobalData
import com.amour.shop.classes.GlobalData.errorDialog
import com.amour.shop.classes.GlobalData.getIntro
import com.amour.shop.classes.GlobalData.hideProgressDialog
import com.amour.shop.classes.GlobalData.progressDialog
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.FragmentLoginBinding


class LoginFragment : FragmentBase() {
    val TAG = "Log"
    var FCMToken: String? = null
    var CountryCode = "+966"
    var select_country = false
    var country_name = "BH"
    var city_id = "7263"
    var localModel: LocalModel? = null
    var cartNumber = 0
    var storeId = 0
    var userId = 0
    var user: MemberModel? = null
    var countryId = 0
    private lateinit var binding: FragmentLoginBinding
    private var viewPager: ViewPager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewPager = container!!.findViewById(R.id.viewPager)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        localModel = UtilityApp.getLocalData()
        countryId = localModel?.countryId ?: UtilityApp.getLocalData().countryId
        deviceToken
        country_name = localModel?.shortname ?: UtilityApp.getLocalData().shortname
        CountryCode = localModel?.phonecode.toString()
        val intro = getIntro(CountryCode)
        Log.i(javaClass.simpleName, "Log get  Intro $intro")
        binding.edtPhoneNumber.hint = intro
        initListeners()
    }

    private fun initListeners() {
        binding.edtPassword.transformationMethod = PasswordTransformationMethod()
        binding.textForgotPassword.setOnClickListener { startRestPassword() }
        binding.skipButton.setOnClickListener { startMain() }
        binding.loginBut.setOnClickListener {
            if (isValidForm) {
                loginUser()
            }
        }
        binding.loginGoogleBut.setOnClickListener { view1 -> }
        binding.loginFacebookBut.setOnClickListener { view1 -> }
        binding.registerBut.setOnClickListener { view1 -> startLogin() }
        binding.loginTwitterBut.setOnClickListener { view1 -> }
        binding.showPassBut.setOnClickListener { view1 ->
            if (binding.edtPassword.transformationMethod
                        .equals(PasswordTransformationMethod.getInstance())
            ) {
                (view1 as ImageView).setImageResource(R.drawable.ic_visibility_off)
                binding.edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                (view1 as ImageView).setImageResource(R.drawable.ic_visibility)
                binding.edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    private fun loginUser() {
        val mobileStr = NumberHandler.arabicToDecimal(binding.edtPhoneNumber.text.toString())
        val passwordStr = NumberHandler.arabicToDecimal(binding.edtPassword.text.toString())
        val memberModel = MemberModel()
        memberModel.mobileNumber = mobileStr
        memberModel.password = passwordStr
        memberModel.deviceType = Constants.deviceType
        memberModel.deviceToken = FCMToken
        memberModel.deviceId = UtilityApp.unique
        memberModel.userType = Constants.user_type
        memberModel.storeId = localModel!!.cityId.toInt()
        memberModel.city = localModel!!.cityId.toInt()
        progressDialog(activityy, R.string.text_login_login, R.string.please_wait_login)
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    val result = obj as LoginResultModel?
                    when (func) {
                        Constants.ERROR, Constants.FAIL -> {
                            var message: String? = getString(R.string.fail_signin)
                            if (result != null && result.message != null) {
                                message = result.message
                            }
                            hideProgressDialog()
                            errorDialog(activityy, R.string.text_login_login, message)
                        }
                        Constants.NO_CONNECTION -> {
                            hideProgressDialog()
                            GlobalData.Toast(activityy, R.string.no_internet_connection)
                        }
                        else -> if (IsSuccess) {
                            if (result != null) {
                                if (result.status == 106) {
                                    hideProgressDialog()
                                    val intent = Intent(activityy, ConfirmActivity::class.java)
                                    intent.putExtra(Constants.KEY_MOBILE, mobileStr)
                                    intent.putExtra(Constants.verify_account, true)
                                    intent.putExtra(
                                        Constants.KEY_PASSWORD,
                                        passwordStr
                                    )
                                    startActivity(intent)
                                } else if (result.status == 0) {
                                    hideProgressDialog()
                                    var message: String? = getString(R.string.fail_signin)
                                    if (result.message != null) {
                                        message = result.message
                                    }
                                    errorDialog(activityy, R.string.text_login_login, message)
                                } else if (result.status == 200) {
                                    val user = result.data
                                    if (user != null) {
                                        user.userType = Constants.user_type
                                        user.password = passwordStr
                                    }
                                    UtilityApp.setUserData(user)
                                    UtilityApp.setUserToken(result.token)
                                    UtilityApp.setRefreshToken(result.refreshToken)
                                    Log.i(
                                        TAG,
                                        "Log token  user token " + UtilityApp.getUserToken()
                                    )
                                    Log.i(
                                        TAG,
                                        "Log token refersh token " + UtilityApp.getRefreshToken()
                                    )
                                    UtilityApp.setIsFirstLogin(true)
                                    if (UtilityApp.getUserData() != null) {
                                        updateToken()
                                    }
                                } else {
                                    hideProgressDialog()
                                    var message: String? = getString(R.string.fail_signin)
                                    if (result.message != null) {
                                        message = result.message
                                    }
                                    errorDialog(activityy, R.string.text_login_login, message)
                                }
                            }
                        } else {
                            hideProgressDialog()
                            Toast(getString(R.string.fail_signin))
                        }
                    }
                }
            }
        }).loginHandle(memberModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        binding = null
    }

    private fun startRestPassword() {
        val intent = Intent(activityy, ConfirmPhoneActivity::class.java)
        intent.putExtra(Constants.reset_account, true)
        startActivity(intent)
    }

    private fun startLogin() {
        viewPager!!.currentItem = 0
    }

    private val isValidForm: Boolean
        get() {
            val formValidator = getInstance()
            return formValidator.addField(
                binding.edtPhoneNumber,
                NonEmptyRule(getString(R.string.enter_phone_number))
            ).addField(
                binding.edtPassword, NonEmptyRule(R.string.enter_password)
            ).validate()
        }

    //            OneSignal.idsAvailable((userId, registrationId) -> {
//                Log.d("debug", "Log User:" + userId);
//                if (registrationId != null)
//                    FCMToken = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
//                UtilityApp.setFCMToken(FCMToken);
//
//                Log.d("debug", "Log token one signal first :" + OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
//                Log.d("debug", "Log token firebase:" + UtilityApp.getFCMToken());
//
//            });
    val deviceToken: Unit
        get() {
            FCMToken = UtilityApp.getFCMToken()
            if (FCMToken == null) {
                val FCMToken = OneSignal.getDeviceState()!!.userId
                UtilityApp.setFCMToken(FCMToken)

                //            OneSignal.idsAvailable((userId, registrationId) -> {
                //                Log.d("debug", "Log User:" + userId);
                //                if (registrationId != null)
                //                    FCMToken = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
                //                UtilityApp.setFCMToken(FCMToken);
                //
                //                Log.d("debug", "Log token one signal first :" + OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                //                Log.d("debug", "Log token firebase:" + UtilityApp.getFCMToken());
                //
                //            });
            }
        }

    private fun startMain() {
        val intent = Intent(activityy, MAIN_ACTIVITY_CLASS)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activityy.finish()
    }

    private fun updateToken() {

//        GlobalData.INSTANCE.progressDialog(getActivityy(), R.string.text_login_login, R.string.please_wait_login);
        val memberModel = UtilityApp.getUserData()
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                hideProgressDialog()

                if (isVisible) {
                    if (func == Constants.ERROR) {
                        var message: String? = getString(R.string.fail_signin)
                        val result: ResultAPIModel<*>? = obj as ResultAPIModel<*>?

                        if (result?.message != null
                        ) {
                            message = result.message
                        }
                        hideProgressDialog()
                        Toast(message)
                    } else {
                        if (IsSuccess) {
                            localModel = UtilityApp.getLocalData()
                            storeId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
                            user = UtilityApp.getUserData()
                            userId = user?.id ?: 0
                            getCarts(storeId, userId)
                        } else {
                            Toast(getString(R.string.fail_signin))
                            hideProgressDialog()
                        }
                    }
                }
            }
        }).updateTokenHandle(memberModel)
    }


    fun getCarts(storeId: Int, userId: Int) {
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    hideProgressDialog()
                    val cartResultModel = obj as CartResultModel
                    val message = getString(R.string.fail_to_get_data)
                    if (IsSuccess) {
                        if (cartResultModel.data != null && cartResultModel.data
                                    .cartData != null && cartResultModel.data.cartData.size > 0
                        ) {
                            cartNumber = cartResultModel.data.cartCount
                            Log.i(javaClass.simpleName, "Log cartNumber$cartNumber")
                            UtilityApp.setCartCount(cartNumber)
                            val intent =
                                Intent(activityy, MAIN_ACTIVITY_CLASS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {
                            UtilityApp.setCartCount(0)
                            val intent =
                                Intent(activityy, MAIN_ACTIVITY_CLASS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                        activity!!.finish()
                    } else {
                        hideProgressDialog()
                    }
                }
            }
        }).GetCarts(storeId, userId)
    }
}