package com.amour.shop.fragments

import android.annotation.SuppressLint
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
import com.github.dhaval2404.form_validation.rule.EqualRule
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import com.onesignal.OneSignal
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData
import com.amour.shop.activities.ConditionActivity
import com.amour.shop.activities.ConfirmActivity
import com.amour.shop.activities.TermsActivity
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.Models.OtpModel
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.LocalModel
import com.amour.shop.Models.MemberModel
import com.amour.shop.Models.RegisterResultModel
import com.amour.shop.R
import com.amour.shop.Utils.NumberHandler
import com.amour.shop.Utils.SharedPManger
import com.amour.shop.classes.Constants.MAIN_ACTIVITY_CLASS
import com.amour.shop.classes.GlobalData.errorDialog
import com.amour.shop.classes.GlobalData.getIntro
import com.amour.shop.classes.GlobalData.hideProgressDialog
import com.amour.shop.classes.GlobalData.progressDialog
import com.amour.shop.databinding.FragmentRegisterBinding
import com.amour.shop.BuildConfig


class RegisterFragment : FragmentBase() {
    var FCMToken: String? = null
    var countryCode = "+966"
    var countryName = "BH"
    var cityId = "7263"
    var sharedPManger: SharedPManger? = null
    private lateinit var binding: FragmentRegisterBinding
    private var viewPager: ViewPager? = null
    var localModel: LocalModel? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view: View = binding.root
        viewPager = container!!.findViewById(R.id.viewPager)
        deviceToken
        sharedPManger = SharedPManger(activityy)
        localModel = UtilityApp.getLocalData()
        countryName = localModel?.shortname.toString()
        countryCode = localModel?.phonecode.toString()
        cityId = localModel?.cityId.toString()
        val intro = getIntro(countryCode)
        Log.i(javaClass.simpleName, "Log get  Intro $intro")
        binding.edtPhoneNumber.hint = intro
        initListeners()
        return view
    }

    private fun initListeners() {
        binding.loginBut.setOnClickListener { startLogin() }
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
        binding.showConfirmPassBut.setOnClickListener { view1 ->
            if (binding.edtConfirmPassword.transformationMethod
                        .equals(PasswordTransformationMethod.getInstance())
            ) {
                (view1 as ImageView).setImageResource(R.drawable.ic_visibility_off)
                binding.edtConfirmPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                (view1 as ImageView).setImageResource(R.drawable.ic_visibility)
                binding.edtConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        binding.registerBut.setOnClickListener {
            if (isValidForm) {
                registerUser()
            }
        }
        binding.privacyBut.setOnClickListener { startConditionActivity() }
        binding.termsBut.setOnClickListener { startTermsActivity() }
        binding.skipButton.setOnClickListener { startMain() }
    }

    private fun registerUser() {
        val mobileStr = NumberHandler.arabicToDecimal(binding.edtPhoneNumber.text.toString())
        val passwordStr = NumberHandler.arabicToDecimal(binding.edtPassword.text.toString())
        val nameStr = NumberHandler.arabicToDecimal(binding.edtFirstName.text.toString())
        val memberModel = MemberModel()
        memberModel.mobileNumber = mobileStr
        memberModel.password = passwordStr
        memberModel.name = nameStr
        memberModel.email = ""
        memberModel.city = cityId.toInt()
        memberModel.country = countryName
        memberModel.deviceToken = FCMToken
        memberModel.deviceId = UtilityApp.unique
        memberModel.deviceType = Constants.deviceType
        memberModel.prefix = countryCode
        memberModel.userType = Constants.user_type
        progressDialog(activityy, R.string.register, R.string.please_wait_register)
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    hideProgressDialog()
                    val result = obj as RegisterResultModel?
                    if (func == Constants.ERROR) {
                        var message: String? = getString(R.string.fail_register)
                        if (result != null && result.message != null) {
                            message = result.message
                        }
                        errorDialog(activityy, R.string.register, message)
                    } else if (func == Constants.NO_CONNECTION) {
                        GlobalData.Toast(activityy, R.string.no_internet_connection)
                    } else {
                        if (IsSuccess) {
                            Log.i("TAG", "Log getStatus " + result!!.status)
                            if (result.status == 200) {
                                val user = result.data
                                if (user != null) {
                                    user.userType = Constants.user_type
                                }
                                UtilityApp.setUserData(user)

                                startLogin()

                            } else {
                                var message: String? = getString(R.string.fail_register)
                                if (result.message != null) {
                                    message = result.message
                                }
                                errorDialog(activityy, R.string.register, message)
                            }
                        } else {
                            Toast(getString(R.string.fail_register))
                        }
                    }
                }
            }
        }).registerHandle(memberModel)
    }

    private fun startLogin() {
        viewPager!!.currentItem = 1
    }

    private val isValidForm: Boolean
        get() {
            val formValidator = FormValidator.getInstance()
            return formValidator.addField(binding.edtFirstName, NonEmptyRule(getString(R.string.enter_name)))
                .addField(binding.edtPhoneNumber, NonEmptyRule(getString(R.string.enter_phone_number)))
                .addField(binding.edtPassword, NonEmptyRule(getString(R.string.enter_password))).addField(
                    binding.edtConfirmPassword,
                    NonEmptyRule(getString(R.string.enter_confirm_password)),
                    EqualRule(
                        java.lang.String.valueOf(binding.edtPassword.text),
                        R.string.password_confirm_not_match
                    )
                ).validate()
        }

    //            OneSignal.idsAvailable((userId, registrationId) -> {
    //                if (registrationId != null)
//                    FCMToken=OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
//                UtilityApp.setFCMToken(FCMToken);
//
//                Log.d("debug", "Log token one signal first :" + OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
//                Log.d("debug", "Log token firebase:" + UtilityApp.getFCMToken());
//
//            });
    private val deviceToken: Unit
        get() {
            FCMToken = UtilityApp.getFCMToken()
            if (FCMToken == null) {
                val FCMToken = OneSignal.getDeviceState()!!.userId
                UtilityApp.setFCMToken(FCMToken)

                //            OneSignal.idsAvailable((userId, registrationId) -> {
                Log.d("debug", "Log User:$FCMToken")
                //                if (registrationId != null)
                //                    FCMToken=OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
                //                UtilityApp.setFCMToken(FCMToken);
                //
                //                Log.d("debug", "Log token one signal first :" + OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                //                Log.d("debug", "Log token firebase:" + UtilityApp.getFCMToken());
                //
                //            });
            }
        }


    fun SendOtp(mobile: String?, password: String?) {
        val mobileStr = NumberHandler.arabicToDecimal(binding.edtPhoneNumber.getText().toString())
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    if (func == Constants.ERROR) {
                        Toast(R.string.error_in_data)
                    } else if (func == Constants.FAIL) {
                        Toast(R.string.fail_to_sen_otp)
                    } else {
                        if (IsSuccess) {
                            val otpModel = obj as OtpModel
                            if (otpModel.status == 200) {
                                Log.i("TAG", "Log otp " + otpModel.data)
                                val intent = Intent(activityy, ConfirmActivity::class.java)
                                intent.putExtra(Constants.KEY_MOBILE, mobileStr)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        } else {
                            Toast(R.string.fail_to_sen_otp)
                        }
                    }
                }
            }

        }).sendOpt(mobile ?: "")
    }

    private fun startTermsActivity() {
        val intent = Intent(activityy, TermsActivity::class.java)
        startActivity(intent)
    }

    private fun startConditionActivity() {
        val intent = Intent(activityy, ConditionActivity::class.java)
        startActivity(intent)
    }

    private fun startMain() {
        val intent = Intent(activityy, MAIN_ACTIVITY_CLASS)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}