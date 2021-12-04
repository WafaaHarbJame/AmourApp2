package com.ramez.shopp.activities


import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.onesignal.OneSignal
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.Constants.MAIN_ACTIVITY_CLASS
import com.ramez.shopp.Classes.GlobalData
import com.ramez.shopp.Classes.GlobalData.errorDialog
import com.ramez.shopp.Classes.GlobalData.hideProgressDialog
import com.ramez.shopp.Classes.GlobalData.progressDialog
import com.ramez.shopp.Classes.OtpModel
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.databinding.ActivityConfirmBinding


class ConfirmActivity : ActivityBase() {
    lateinit var binding: ActivityConfirmBinding
    var mobileStr: String? = ""
    var passwordStr: String? = null
    var verify_account = false
    var isStart = false
    var FCMToken: String? = null
    var downTimer: CountDownTimer? = null
    var reset_account = false
    var localModel: LocalModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfirmBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        binding.toolBar.backBtn.setOnClickListener { view1 -> onBackPressed() }
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
        deviceToken
        val bundle = intent.extras
        if (bundle != null) {
            mobileStr = intent.getStringExtra(Constants.KEY_MOBILE)
            verify_account = intent.getBooleanExtra(Constants.verify_account, false)
            reset_account = intent.getBooleanExtra(Constants.reset_account, false)
            passwordStr = bundle.getString(Constants.KEY_PASSWORD)
        }
        binding.resendCodeTxt.setOnClickListener { view1 -> sendOtp(mobileStr, true) }
        if (verify_account) {
            sendOtp(mobileStr, false)
        }
        downTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(l: Long) {
                if (!isStart) binding.resendCodeTxt.isEnabled = false
                val time = (l / 1000).toInt()
                val str = getString(R.string.resend_again) + " (" + time + ")"
                binding.resendCodeTxt.text = str
            }

            override fun onFinish() {
                binding.resendCodeTxt.text = getString(R.string.resend_again)
                binding.resendCodeTxt.isEnabled = true
                isStart = false
            }
        }
        binding.confirmBut.setOnClickListener { view1 ->
            val codeStr = NumberHandler.arabicToDecimal(binding.codeTxt.text.toString())
            VerifyOtp(mobileStr, codeStr)
        }
    }

    fun VerifyOtp(mobile: String?, otp: String?) {
        progressDialog(activity, R.string.confirm_code, R.string.please_wait_sending)
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                hideProgressDialog()
                var message = getString(R.string.fail_to_get_data)
                val result = obj as GeneralModel
                if (func == Constants.ERROR) {
                    if (result?.message != null) {
                        message = result.message
                    }
                    errorDialog(activity, R.string.confirm_code, message)
                } else if (func == Constants.FAIL) {
                    if (result != null && result.message != null) {
                        message = result.message
                    }
                    errorDialog(activity, R.string.confirm_code, message)
                } else if (func == Constants.NO_CONNECTION) {
                    GlobalData.Toast(activity, R.string.no_internet_connection)
                } else {
                    if (IsSuccess) {
                        if (verify_account) {
                            loginUser()
                        } else if (reset_account) {
                            val intent = Intent(activity, ChangePassActivity::class.java)
                            intent.putExtra(Constants.KEY_MOBILE, mobileStr)
                            intent.putExtra(Constants.reset_account, true)
                            startActivity(intent)
                            startActivity(intent)
                        } else {
                            val otpModel = obj
                            Log.i("TAG", "Log otp verify " + otpModel.message)
                            if (otpModel.status == 200) {
                                if (UtilityApp.getUserData() != null) {
                                    updateToken()
                                }
                            } else {
                                message = otpModel.message
                                errorDialog(activity, R.string.confirm_code, message)
                            }
                        }
                    } else {
                        errorDialog(
                            activity,
                            R.string.confirm_code,
                            getString(R.string.fail_to_sen_otp)
                        )
                    }
                }            }

        }).VerifyOtpHandle(mobile, otp)
    }

    fun sendOtp(mobile: String?, isLoad: Boolean) {
        if (isLoad) {
            progressDialog(activity, R.string.confirm_code, R.string.please_wait_sending)
        }
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isLoad) {
                    hideProgressDialog()
                }
                if (func == Constants.ERROR) {
                    Toast(R.string.error_in_data)
                } else if (func == Constants.FAIL) {
                    Toast(R.string.fail_to_sen_otp)
                } else {
                    if (IsSuccess) {
                        val otpModel = obj as OtpModel
                        if (otpModel.status == 200) {
                            Log.i("TAG", "Log otp " + otpModel.data)
                            binding.codeTxt.setText("")
                            downTimer!!.start()
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

    private val deviceToken: Unit
        get() {
            FCMToken = UtilityApp.getFCMToken()
            if (FCMToken == null) {
                FCMToken= OneSignal.getDeviceState()?.userId
                UtilityApp.setFCMToken(FCMToken)
//                OneSignal.idsAvailable { userId: String, registrationId: String? ->
//                    Log.d("debug", "Log User:$userId")
//                    if (registrationId != null) FCMToken =
//                        OneSignal.getPermissionSubscriptionState().subscriptionStatus.userId
//                    UtilityApp.setFCMToken(FCMToken)
//                    Log.d(
//                        "debug",
//                        "Log token one signal first :" + OneSignal.getPermissionSubscriptionState()
//                            .subscriptionStatus.userId
//                    )
//                    Log.d("debug", "Log token firebase:" + UtilityApp.getFCMToken())
//                }
            }
        }

    private fun updateToken() {
        progressDialog(activity, R.string.confirm_code, R.string.please_wait_sending)
        val memberModel = UtilityApp.getUserData()
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                hideProgressDialog()
                hideProgressDialog()
                val result: ResultAPIModel<*>? = obj as ResultAPIModel<*>?
                if (func == Constants.ERROR) {
                    var message: String? = getString(R.string.fail_signin)
                    if (result?.message != null) {
                        message = result.message
                    }
                    Toast(message)
                } else {
                    if (IsSuccess) {
                        startMain()
                    }
                }            }

        }).UpdateTokenHandle(memberModel)
    }

    fun startMain() {
        val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity.finish()
    }

    private fun loginUser() {
        val memberModel = MemberModel()
        memberModel.mobileNumber = mobileStr
        memberModel.password = passwordStr
        memberModel.deviceType = Constants.deviceType
        memberModel.deviceToken = FCMToken
        memberModel.deviceId = UtilityApp.getUnique()
        memberModel.userType = Constants.user_type
        memberModel.city = localModel!!.cityId.toInt()
        progressDialog(activity, R.string.confirm_code, R.string.please_wait_sending)
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                hideProgressDialog()
                val result = obj as LoginResultModel?
                if (func == Constants.ERROR) {
                    var message: String? = getString(R.string.fail_signin)
                    if (result != null && result.message != null) {
                        message = result.message
                    }
                    errorDialog(activity, R.string.fail_signin, message)
                } else if (func == Constants.FAIL) {
                    var message: String? = getString(R.string.fail_signin)
                    if (result != null && result.message != null) {
                        message = result.message
                    }
                    errorDialog(activity, R.string.fail_signin, message)
                } else if (func == Constants.NO_CONNECTION) {
                    GlobalData.Toast(activity, R.string.no_internet_connection)
                } else {
                    if (IsSuccess) {
                        val user = result!!.data
                        if (user != null) {
                            user.userType = Constants.user_type
                        }
                        UtilityApp.setUserData(user)
                        if (UtilityApp.getUserData() != null) {
                            updateToken()
                        }
                    } else {
                        Toast(getString(R.string.fail_signin))
                    }
                }            }

        }).loginHandle(memberModel)
    }
}