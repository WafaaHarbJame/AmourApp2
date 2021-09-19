package com.ramez.shopp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.dhaval2404.form_validation.rule.NonEmptyRule;
import com.github.dhaval2404.form_validation.validation.FormValidator;
import com.onesignal.OneSignal;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.OtpModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.ActivityConformPhoneBinding;

public class ConfirmPhoneActivity extends ActivityBase {
    private ActivityConformPhoneBinding binding;
    String mobileStr;
    boolean reset_account=false;
    LocalModel localModel;
    String CountryCode = "+966";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConformPhoneBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActiviy());
        CountryCode= String.valueOf(localModel.getPhonecode());
        String intro=GlobalData.getIntro(CountryCode);
        Log.i(getClass().getSimpleName(),"Log get  Intro "+intro);
        binding.edtPhoneNumber.setHint(intro);


        binding.toolBar.backBtn.setOnClickListener(view1 -> {
            onBackPressed();
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobileStr = getIntent().getStringExtra(Constants.KEY_MOBILE);
            reset_account = getIntent().getBooleanExtra(Constants.reset_account,false);


        }


        binding.confirmBut.setOnClickListener(view1 -> {
            if (isValidForm()) {
                String mobileStr = NumberHandler.arabicToDecimal(binding.edtPhoneNumber.getText().toString());
                    MemberModel memberModel=new MemberModel();
                    memberModel.setUserType(Constants.user_type);
                    memberModel.setMobileNumber(mobileStr);
                    ForgetPassword(memberModel);
                }


        });



        setTitle(R.string.forget_pass);



    }

    private void GoToConfirm() {
        String mobileStr = NumberHandler.arabicToDecimal(binding.edtPhoneNumber.getText().toString());
        Intent intent = new Intent(getActiviy(), ConfirmActivity.class);
        intent.putExtra(Constants.verify_account, false);
        intent.putExtra(Constants.reset_account, reset_account);
        intent.putExtra(Constants.KEY_MOBILE, mobileStr);
        startActivity(intent);
//        finish();

    }

    private boolean isValidForm() {
        FormValidator formValidator = FormValidator.Companion.getInstance();
        return formValidator.addField(binding.edtPhoneNumber, new NonEmptyRule(getString(R.string.enter_phone_number))).validate();
    }

    public void SendOtp(String mobile) {
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (func.equals(Constants.ERROR)) {
                Toast(R.string.error_in_data);
            } else if (func.equals(Constants.FAIL)) {
                Toast(R.string.fail_to_sen_otp);
            } else {
                if (IsSuccess) {
                    OtpModel otpModel = (OtpModel) obj;

                    if(otpModel.getStatus()==200 ) {
                        Log.i("TAG", "Log otp " + otpModel.getData());
                        GoToConfirm();
                    }
                    else {

                    String message= otpModel.getMessage() != null ?otpModel.getMessage() : getString(R.string.fail_to_sen_otp);
                            Toast(message);


                    }



                } else {
                    Toast(R.string.fail_to_sen_otp);
                }
            }

        }).sendOpt(mobile);
    }

    public void ForgetPassword(MemberModel memberModel) {
        GlobalData.progressDialog(
                getActiviy(),
                R.string.forget_pass,
                R.string.please_wait_sending);
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.hideProgressDialog();
            if (func.equals(Constants.ERROR)) {
                Toast(R.string.error_in_data);
            } else if (func.equals(Constants.FAIL)) {
                Toast(R.string.fail_to_rest_password);
            }

            else if (func.equals(Constants.NO_CONNECTION)) {
                GlobalData.Toast(getActiviy(), R.string.no_internet_connection);
            }
            else {
                if (IsSuccess) {
                    OtpModel otpModel = (OtpModel) obj;
                    Log.i(getClass().getSimpleName(), "Log OtpModel status " +otpModel.getStatus());

                    if(otpModel.getStatus()==200 ) {
                        SendOtp(memberModel.getMobileNumber());
                    }
                    else {
                        String message= otpModel.getMessage() != null ?otpModel.getMessage() : getString(R.string.fail_to_rest_password);
                        Toast(message);

                    }


                } else {
                    Toast(R.string.fail_to_rest_password);
                }
            }

        }).ForgetPasswordHandle(memberModel);
    }


}