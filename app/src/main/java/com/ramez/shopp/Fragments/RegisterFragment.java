package com.ramez.shopp.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.ViewPager;

import com.github.dhaval2404.form_validation.rule.EqualRule;
import com.github.dhaval2404.form_validation.rule.NonEmptyRule;
import com.github.dhaval2404.form_validation.validation.FormValidator;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.onesignal.OneSignal;
import com.ramez.shopp.Activities.ChooseCityActivity;
import com.ramez.shopp.Activities.ConditionActivity;
import com.ramez.shopp.Activities.ConfirmActivity;
import com.ramez.shopp.Activities.TermsActivity;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.OtpModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.LoginResultModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.Utils.SharedPManger;
import com.ramez.shopp.databinding.FragmentRegisterBinding;

public class RegisterFragment extends FragmentBase {
    String FCMToken;
    String CountryCode = "+966";
    boolean select_country = false;
    String country_name = "BH";
    String city_id = "7263";
    String prefix = "973";
    SharedPManger sharedPManger;
    private FragmentRegisterBinding binding;
    private ViewPager viewPager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        viewPager = container.findViewById(R.id.viewPager);

        getDeviceToken();
        sharedPManger = new SharedPManger(getActivityy());

        binding.loginBut.setOnClickListener(view1 -> {
            startLogin();


        });


        binding.showPassBut.setOnClickListener(view1 -> {

            if (binding.edtPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility_off);
                binding.edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility);
                binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });



        binding.showConfirmPassBut.setOnClickListener(view1 -> {

            if (binding.edtConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility_off);
                binding.edtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility);
                binding.edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        binding.registerBut.setOnClickListener(view1 -> {
            if (isValidForm()) {
                RegisterUser();

            }
        });

        binding.privacyBut.setOnClickListener(view1 -> {
            startConditionActivity();


        });

        binding.termsBut.setOnClickListener(view1 -> {
            startTermsActivity();


        });

        return view;
    }

    private void RegisterUser() {
        final String mobileStr = NumberHandler.arabicToDecimal(binding.edtPhoneNumber.getText().toString());
        final String passwordStr = NumberHandler.arabicToDecimal(binding.edtPassword.getText().toString());
        final String nameStr = NumberHandler.arabicToDecimal(binding.edtFirstName.getText().toString());
        final String emailStr = NumberHandler.arabicToDecimal(binding.edtEmail.getText().toString());
        LocalModel localModel = UtilityApp.getLocalData();

        country_name = localModel.getShortname();
        CountryCode = String.valueOf(localModel.getPhonecode());
        city_id = localModel.getCityId();

        MemberModel memberModel = new MemberModel();
        memberModel.setMobileNumber(mobileStr);
        memberModel.setPassword(passwordStr);
        memberModel.setName(nameStr);
        memberModel.setEmail(emailStr);
        memberModel.setCity(city_id);
        memberModel.setCountry(country_name);
        memberModel.setDeviceToken(FCMToken);
        memberModel.setDeviceId(UtilityApp.getUnique());
        memberModel.setDeviceType(Constants.deviceType);
        memberModel.setPrefix(CountryCode);
        memberModel.setUserType(Constants.user_type);

        GlobalData.progressDialog(getActivityy(), R.string.register, R.string.please_wait_register);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.hideProgressDialog();
            LoginResultModel result = (LoginResultModel) obj;
            if (func.equals(Constants.ERROR)) {
                String message = getString(R.string.fail_register);
                if (result != null && result.getMessage() != null) {
                    message = result.getMessage();
                }
                GlobalData.errorDialog(getActivityy(), R.string.register, message);
            } else if (func.equals(Constants.NO_CONNECTION)) {
                GlobalData.Toast(getActivityy(), R.string.no_internet_connection);
            } else {
                if (IsSuccess) {
                        Log.i("TAG", "Log getStatus " + result.getStatus());

                        if (result.getStatus() == 200) {
                            MemberModel user = result.data;
                            if(user!=null){
                                user.setUserType(Constants.user_type);

                            }

                            UtilityApp.setUserData(user);
                            SendOtp(mobileStr, passwordStr);

                        } else {
                            String message = getString(R.string.fail_register);
                            if (result.getMessage() != null) {
                                message = result.getMessage();
                            }
                            GlobalData.errorDialog(getActivityy(), R.string.register, message);


                        }


                } else {
                    Toast(getString(R.string.fail_register));

                }
            }


        }).RegisterHandle(memberModel);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void startLogin() {
        viewPager.setCurrentItem(1);

    }

    private final boolean isValidForm() {

        FormValidator formValidator = FormValidator.Companion.getInstance();

        return formValidator.addField(binding.edtFirstName, new NonEmptyRule(getString(R.string.enter_name))).addField(binding.edtPhoneNumber, new NonEmptyRule(getString(R.string.enter_phone_number))).
                addField(binding.edtPassword, new NonEmptyRule(getString(R.string.enter_password))).

                addField(binding.edtConfirmPassword, new NonEmptyRule(getString(R.string.enter_confirm_password)), new EqualRule(String.valueOf(binding.edtPassword.getText()), R.string.password_confirm_not_match)).validate();

    }

    private void getDeviceToken() {

        FCMToken = UtilityApp.getFCMToken();
        if (FCMToken == null) {

            OneSignal.idsAvailable((userId, registrationId) -> {
                Log.d("debug", "Log User:" + userId);
                if (registrationId != null)
                    FCMToken=OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
                UtilityApp.setFCMToken(FCMToken);

                Log.d("debug", "Log token one signal first :" + OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
                Log.d("debug", "Log token firebase:" + UtilityApp.getFCMToken());

            });

        }

    }


    private void GoToChooseCity() {
        Intent intent = new Intent(getActivityy(), ChooseCityActivity.class);
        startActivity(intent);
        getActivityy().finish();

    }


    public void SendOtp(String mobile, String password) {
        final String mobileStr = NumberHandler.arabicToDecimal(binding.edtPhoneNumber.getText().toString());
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (func.equals(Constants.ERROR)) {
                Toast(R.string.error_in_data);
            } else if (func.equals(Constants.FAIL)) {
                Toast(R.string.fail_to_sen_otp);
            } else {
                if (IsSuccess) {
                    OtpModel otpModel = (OtpModel) obj;
                    if (otpModel.getData() != null) {
                        Log.i("TAG", "Log otp " + otpModel.getData());

                    }
                    Intent intent = new Intent(getActivityy(), ConfirmActivity.class);
                    intent.putExtra(Constants.KEY_MOBILE, mobileStr);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                } else {
                    Toast(R.string.fail_to_sen_otp);
                }
            }

        }).sendOpt(mobile);
    }


    private void startTermsActivity() {
        Intent intent = new Intent(getActivityy(), TermsActivity.class);
        startActivity(intent);
    }

    private void startConditionActivity() {
        Intent intent = new Intent(getActivityy(), ConditionActivity.class);
        startActivity(intent);
    }

}