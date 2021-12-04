package com.ramez.shopp.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator.Companion.getInstance
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.GlobalData
import com.ramez.shopp.Classes.GlobalData.getIntro
import com.ramez.shopp.Classes.GlobalData.hideProgressDialog
import com.ramez.shopp.Classes.GlobalData.progressDialog
import com.ramez.shopp.Classes.OtpModel
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.Models.MemberModel
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.databinding.ActivityConformPhoneBinding


class ConfirmPhoneActivity : ActivityBase() {
    private lateinit var binding: ActivityConformPhoneBinding
    var mobileStr: String? = null
    var resetAccount = false
    var localModel: LocalModel? = null
    var countryCode = "+966"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConformPhoneBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
        countryCode = localModel?.phonecode.toString()
        val intro = getIntro(countryCode)
        Log.i(javaClass.simpleName, "Log get  Intro $intro")
        binding.edtPhoneNumber.hint = intro
        binding.toolBar.backBtn.setOnClickListener { view1 -> onBackPressed() }
        val bundle = intent.extras
        if (bundle != null) {
            mobileStr = intent.getStringExtra(Constants.KEY_MOBILE)
            resetAccount = intent.getBooleanExtra(Constants.reset_account, false)
        }
        binding.confirmBut.setOnClickListener { view1 ->
            if (isValidForm) {
                val mobileStr =
                    NumberHandler.arabicToDecimal(binding.edtPhoneNumber.text.toString())
                val memberModel = MemberModel()
                memberModel.userType = Constants.user_type
                memberModel.mobileNumber = mobileStr
                forgetPassword(memberModel)
            }
        }
        setTitle(R.string.forget_pass)
    }

    private fun goToConfirm() {
        val mobileStr = NumberHandler.arabicToDecimal(binding.edtPhoneNumber.text.toString())
        val intent = Intent(activity, ConfirmActivity::class.java)
        intent.putExtra(Constants.verify_account, false)
        intent.putExtra(Constants.reset_account, resetAccount)
        intent.putExtra(Constants.KEY_MOBILE, mobileStr)
        startActivity(intent)
        //        finish();
    }

    private val isValidForm: Boolean
        get() {
            val formValidator = getInstance()
            return formValidator.addField(
                binding.edtPhoneNumber,
                NonEmptyRule(getString(R.string.enter_phone_number))
            ).validate()
        }

    fun SendOtp(mobile: String?) {
        DataFeacher(false,object :
            DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (func == Constants.ERROR) {
                    Toast(R.string.error_in_data)
                } else if (func == Constants.FAIL) {
                    Toast(R.string.fail_to_sen_otp)
                } else {
                    if (IsSuccess) {
                        val otpModel = obj as OtpModel
                        if (otpModel.status == 200) {
                            Log.i("TAG", "Log otp " + otpModel.data)
                            goToConfirm()
                        } else {
                            val message =
                                if (otpModel.message != null) otpModel.message else getString(R.string.fail_to_sen_otp)
                            Toast(message)
                        }
                    } else {
                        Toast(R.string.fail_to_sen_otp)
                    }
                }            }

        }).sendOpt(mobile)
    }

    private fun forgetPassword(memberModel: MemberModel) {
        progressDialog(
            activity,
            R.string.forget_pass,
            R.string.please_wait_sending
        )
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                hideProgressDialog()
                if (func == Constants.ERROR) {
                    Toast(R.string.error_in_data)
                } else if (func == Constants.FAIL) {
                    Toast(R.string.fail_to_rest_password)
                } else if (func == Constants.NO_CONNECTION) {
                    GlobalData.Toast(activity, R.string.no_internet_connection)
                } else {
                    if (IsSuccess) {
                        val otpModel = obj as OtpModel
                        Log.i(
                            javaClass.simpleName,
                            "Log OtpModel status " + otpModel.status
                        )
                        if (otpModel.status == 200) {
                            SendOtp(memberModel.mobileNumber)
                        } else {
                            val message =
                                if (otpModel.message != null) otpModel.message else getString(R.string.fail_to_rest_password)
                            Toast(message)
                        }
                    } else {
                        Toast(R.string.fail_to_rest_password)
                    }
                }            }

        }).ForgetPasswordHandle(memberModel)
    }
}