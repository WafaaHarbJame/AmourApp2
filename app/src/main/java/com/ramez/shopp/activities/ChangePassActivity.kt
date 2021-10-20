package com.ramez.shopp.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.github.dhaval2404.form_validation.rule.EqualRule
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator.Companion.getInstance
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.GlobalData
import com.ramez.shopp.Classes.OtpModel
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.GeneralModel
import com.ramez.shopp.Models.MemberModel
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.databinding.ActivityChangePassBinding
import es.dmoral.toasty.Toasty


class ChangePassActivity : ActivityBase() {
    var binding: ActivityChangePassBinding? = null
    var mobileStr: String? = ""
    var reset_account = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePassBinding.inflate(layoutInflater)
        val view: View = binding!!.root
        setContentView(view)
        setTitle(R.string.text_edit_profile_change_password)
        val bundle = intent.extras
        if (bundle != null) {
            mobileStr = intent.getStringExtra(Constants.KEY_MOBILE)
            reset_account = intent.getBooleanExtra(Constants.reset_account, false)
        }
        if (reset_account) {
            binding!!.edtPassword.visibility = View.GONE
            binding!!.oldView.visibility = View.GONE
            binding!!.oldShowPassBut.visibility = View.GONE
            title = getString(R.string.reset_pass)
        }
        binding!!.oldShowPassBut.setOnClickListener { view1 ->
            if (binding!!.edtPassword.transformationMethod
                        .equals(PasswordTransformationMethod.getInstance())
            ) {
                (view1 as ImageView).setImageResource(R.drawable.ic_visibility_off)
                binding!!.edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                (view1 as ImageView).setImageResource(R.drawable.ic_visibility)
                binding!!.edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        binding!!.newShowPassBut.setOnClickListener { view1 ->
            if (binding!!.edtNewPassword.transformationMethod
                        .equals(PasswordTransformationMethod.getInstance())
            ) {
                (view1 as ImageView).setImageResource(R.drawable.ic_visibility_off)
                binding!!.edtNewPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                (view1 as ImageView).setImageResource(R.drawable.ic_visibility)
                binding!!.edtNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        binding!!.confirmShowPassBut.setOnClickListener { view1 ->
            if (binding!!.edtConfirmPassword.transformationMethod
                        .equals(PasswordTransformationMethod.getInstance())
            ) {
                (view1 as ImageView).setImageResource(R.drawable.ic_visibility_off)
                binding!!.edtConfirmPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                (view1 as ImageView).setImageResource(R.drawable.ic_visibility)
                binding!!.edtConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
        binding!!.confirmBut.setOnClickListener { view1 ->
            if (reset_account) {
                if (isValidFormForget) {
                    UpdatePassword()
                }
            } else {
                if (isValidForm) ChangePassword()
            }
        }
    }

    fun ChangePassword() {
        val memberModel: MemberModel
        val oldPasswordStr = NumberHandler.arabicToDecimal(binding!!.edtPassword.text.toString())
        val newPasswordStr = NumberHandler.arabicToDecimal(binding!!.edtNewPassword.text.toString())
        memberModel = if (UtilityApp.getUserData() != null) {
            UtilityApp.getUserData()
        } else {
            MemberModel()
        }
        memberModel.password = oldPasswordStr
        memberModel.new_password = newPasswordStr
        GlobalData.progressDialog(
            activiy,
            R.string.text_registration_change_password,
            R.string.please_wait_sending
        )
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as OtpModel?
                GlobalData.hideProgressDialog()
                if (func == Constants.ERROR) {
                    Toast(R.string.error_in_data)
                } else if (func == Constants.FAIL) {
                    Toast(R.string.fail_to_change_password)
                } else if (func == Constants.NO_CONNECTION) {
                    GlobalData.Toast(activiy, R.string.no_internet_connection)
                } else {
                    if (IsSuccess) {
                        if (result!!.status == 200) {
                            if (UtilityApp.getUserData() != null) {
                                val memberModel1 = UtilityApp.getUserData()
                                signOut(memberModel1)
                            } else {
                                val intent = Intent(activiy, SplashScreenActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        } else {
                            var message: String? = getString(R.string.fail_to_change_password)
                            if (result != null && result.message != null) {
                                message = result.message
                            }
                            GlobalData.errorDialog(activiy, R.string.reset_pass, message)
                        }
                    } else {
                        Toast(R.string.fail_to_change_password)
                    }
                }
            }

        }).ChangePasswordHandle(memberModel)
    }

    fun UpdatePassword() {
        val newPasswordStr = NumberHandler.arabicToDecimal(binding!!.edtNewPassword.text.toString())
        val memberModel = MemberModel()
        memberModel.password = newPasswordStr
        memberModel.new_password = newPasswordStr
        memberModel.mobileNumber = mobileStr
        GlobalData.progressDialog(
            activiy,
            R.string.text_registration_change_password,
            R.string.please_wait_sending
        )
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                GlobalData.hideProgressDialog()
                if (func == Constants.ERROR) {
                    Toast(R.string.error_in_data)
                } else if (func == Constants.FAIL) {
                    Toast(R.string.fail_to_change_password)
                } else if (func == Constants.NO_CONNECTION) {
                    GlobalData.Toast(activiy, R.string.no_internet_connection)
                } else {
                    if (IsSuccess) {
                        val result = obj as GeneralModel?
                        if (result!!.status == 200) {
                            Toasty.success(
                                activiy,
                                getString(R.string.success_update),
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                            val intent = Intent(activiy, RegisterLoginActivity::class.java)
                            intent.putExtra(Constants.LOGIN, true)
                            startActivity(intent)
                        } else {
                            var message: String? = getString(R.string.fail_to_change_password)
                            if (result != null && result.message != null) {
                                message = result.message
                            }
                            GlobalData.errorDialog(activiy, R.string.reset_pass, message)
                        }
                    } else {
                        Toast(R.string.fail_to_change_password)
                    }
                }
            }

        }).UpdatePasswordHandle(memberModel)
    }

    private val isValidForm: Boolean
        private get() {
            val formValidator = getInstance()
            return formValidator.addField(binding!!.edtPassword, NonEmptyRule(R.string.old_pass)).addField(
                binding!!.edtNewPassword, NonEmptyRule(R.string.new_pass)
            ).addField(binding!!.edtConfirmPassword, NonEmptyRule(R.string.enter_confirm_password))
                .addField(
                    binding!!.edtConfirmPassword, EqualRule(
                        java.lang.String.valueOf(binding!!.edtNewPassword.text),
                        R.string.password_confirm_not_match
                    )
                ).validate()
        }
    private val isValidFormForget: Boolean
        get() {
            val formValidator = getInstance()
            return formValidator.addField(binding!!.edtNewPassword, NonEmptyRule(R.string.new_pass)).addField(
                binding!!.edtConfirmPassword, NonEmptyRule(R.string.enter_confirm_password)
            ).addField(
                binding!!.edtConfirmPassword, EqualRule(
                    java.lang.String.valueOf(
                        binding!!.edtNewPassword.text
                    ), R.string.password_confirm_not_match
                )
            ).validate()
        }

    fun signOut(memberModel: MemberModel?) {
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (func == Constants.ERROR) {
                    Toast(R.string.fail_to_sign_out)
                } else if (func == Constants.FAIL) {
                    Toast(R.string.fail_to_sign_out)
                } else if (func == Constants.NO_CONNECTION) {
                    GlobalData.Toast(activiy, R.string.no_internet_connection)
                } else {
                    if (IsSuccess) {
                        UtilityApp.logOut()
                        GlobalData.Position = 0
                        val intent = Intent(activiy, SplashScreenActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        Toast(R.string.fail_to_sign_out)
                        val intent = Intent(activiy, SplashScreenActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }


        }).logOut(memberModel)
    }
}