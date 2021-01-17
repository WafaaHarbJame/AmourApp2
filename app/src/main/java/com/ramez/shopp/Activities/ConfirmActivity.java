package com.ramez.shopp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.google.firebase.iid.FirebaseInstanceId;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.OtpModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.GeneralModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.ActivityConfirmBinding;

public class ConfirmActivity extends ActivityBase {
    ActivityConfirmBinding binding;
    String mobileStr, passwordStr;
    Boolean verify_account, isStart = false;
    String FCMToken;
    CountDownTimer downTimer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.toolBar.mainTitleTxt.setText(getString(R.string.confirm_phone));

        binding.toolBar.backBtn.setOnClickListener(view1 -> {
            onBackPressed();
        });

        getDeviceToken();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobileStr = getIntent().getStringExtra(Constants.KEY_MOBILE);
            verify_account = getIntent().getBooleanExtra(Constants.verify_account, false);

        }


        binding.resendCodeTxt.setOnClickListener(view1 -> {
            SendOtp(mobileStr, true);
        });

        if (verify_account) {

            SendOtp(mobileStr, false);

        }


        downTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                if (!isStart)

                binding.resendCodeTxt.setEnabled(false);
                int time = (int) (l / 1000);
                String str = getString(R.string.resend_again) + " (" + time + ")";
                binding.resendCodeTxt.setText(str);

            }

            @Override
            public void onFinish() {

                binding.resendCodeTxt.setText(getString(R.string.resend_again));
                binding.resendCodeTxt.setEnabled(true);
                isStart =false;
            }
        };


        binding.confirmBut.setOnClickListener(view1 -> {
            String codeStr = NumberHandler.arabicToDecimal(binding.codeTxt.getText().toString());
            VerifyOtp(mobileStr, codeStr);

        });

    }

    public void VerifyOtp(String mobile, String otp) {
        GlobalData.progressDialog(getActiviy(), R.string.confirm_code, R.string.please_wait_sending);
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.hideProgressDialog();

            String message = getString(R.string.fail_to_get_data);
            GeneralModel result = (GeneralModel) obj;

            if (func.equals(Constants.ERROR)) {
                if (result != null) {
                    message = result.getMessage();

                }
                GlobalData.errorDialog(getActiviy(), R.string.confirm_code, message);


            } else if (func.equals(Constants.FAIL)) {
                if (result != null) {
                    message = result.getMessage();

                }
                GlobalData.errorDialog(getActiviy(), R.string.confirm_code, message);

            } else if (func.equals(Constants.NO_CONNECTION)) {

                GlobalData.Toast(getActiviy(), R.string.no_internet_connection);

            } else {
                if (IsSuccess) {
                    GeneralModel otpModel = (GeneralModel) obj;
                    Log.i("TAG", "Log otp verify " + otpModel.getMessage());

                    if (otpModel.getStatus() == 200) {
                        if (UtilityApp.getUserData() != null) {
                            UpdateToken();
                        }
                    } else {
                        message = otpModel.getMessage();
                        GlobalData.errorDialog(getActiviy(), R.string.confirm_code, message);

                    }


                } else {
                    GlobalData.errorDialog(getActiviy(), R.string.confirm_code, getString(R.string.fail_to_sen_otp));


                }
            }

        }).VerifyOtpHandle(mobile, otp);
    }


    public void SendOtp(String mobile, boolean isLoad) {
        if (isLoad) {
            GlobalData.progressDialog(getActiviy(), R.string.confirm_code, R.string.please_wait_sending);

        }

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isLoad) {
                GlobalData.hideProgressDialog();

            }
            if (func.equals(Constants.ERROR)) {
                Toast(R.string.error_in_data);
            } else if (func.equals(Constants.FAIL)) {
                Toast(R.string.fail_to_sen_otp);
            } else {
                if (IsSuccess) {
                    OtpModel otpModel = (OtpModel) obj;
                    Log.i("TAG", "Log otp " + otpModel.getData());
                    binding.codeTxt.setText("");
                    downTimer.start();

                } else {
                    Toast(R.string.fail_to_sen_otp);
                }
            }

        }).sendOpt(mobile);
    }


    private void getDeviceToken() {

        FCMToken = UtilityApp.getFCMToken();
        if (FCMToken == null) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
                FCMToken = instanceIdResult.getToken();
                UtilityApp.setFCMToken(FCMToken);
            });

        }

    }

    private void UpdateToken() {

        GlobalData.progressDialog(getActiviy(), R.string.confirm_code, R.string.please_wait_sending);

        MemberModel memberModel = UtilityApp.getUserData();
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.hideProgressDialog();

            GlobalData.hideProgressDialog();
            GeneralModel result = (GeneralModel) obj;
            if (func.equals(Constants.ERROR)) {
                String message = getString(R.string.fail_signin);
                if (result != null && result.getMessage() != null) {
                    message = result.getMessage();
                }
                Toast(message);

            } else {
                if (IsSuccess) {
                    startMain();

                }


            }


        }).UpdateTokenHandle(memberModel);

    }

    public void startMain() {
        Intent intent = new Intent(getActiviy(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActiviy().finish();

    }


}