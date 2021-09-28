package com.ramez.shopp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.dhaval2404.form_validation.rule.EqualRule;
import com.github.dhaval2404.form_validation.rule.NonEmptyRule;
import com.github.dhaval2404.form_validation.validation.FormValidator;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.OtpModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.GeneralModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.R;
import com.ramez.shopp.Utils.NumberHandler;
import com.ramez.shopp.databinding.ActivityChangePassBinding;

import es.dmoral.toasty.Toasty;

public class ChangePassActivity extends ActivityBase {
    ActivityChangePassBinding binding;
    String mobileStr = "";
    Boolean reset_account = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePassBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setTitle(R.string.text_edit_profile_change_password);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobileStr = getIntent().getStringExtra(Constants.KEY_MOBILE);
            reset_account = getIntent().getBooleanExtra(Constants.reset_account, false);

        }


        if (reset_account) {
            binding.edtPassword.setVisibility(View.GONE);
            binding.oldView.setVisibility(View.GONE);
            binding.oldShowPassBut.setVisibility(View.GONE);
            setTitle(getString(R.string.reset_pass));
        }


        binding.oldShowPassBut.setOnClickListener(view1 -> {

            if (binding.edtPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility_off);
                binding.edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility);
                binding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });



        binding.newShowPassBut.setOnClickListener(view1 -> {

            if (binding.edtNewPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility_off);
                binding.edtNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility);
                binding.edtNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });


        binding.confirmShowPassBut.setOnClickListener(view1 -> {

            if (binding.edtConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility_off);
                binding.edtConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view1)).setImageResource(R.drawable.ic_visibility);
                binding.edtConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        binding.confirmBut.setOnClickListener(view1 -> {

            if (reset_account) {
                if (isValidFormForget()) {
                    UpdatePassword();

                }
            } else {
                if (isValidForm()) ChangePassword();

            }

        });


    }


    public void ChangePassword() {
        MemberModel memberModel;

        final String oldPasswordStr = NumberHandler.arabicToDecimal(binding.edtPassword.getText().toString());
        final String newPasswordStr = NumberHandler.arabicToDecimal(binding.edtNewPassword.getText().toString());
        if (UtilityApp.getUserData() != null) {
            memberModel = UtilityApp.getUserData();

        } else {
            memberModel = new MemberModel();

        }
        memberModel.setPassword(oldPasswordStr);
        memberModel.setNew_password(newPasswordStr);

        GlobalData.progressDialog(getActiviy(), R.string.text_registration_change_password, R.string.please_wait_sending);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            OtpModel result = (OtpModel) obj;

            GlobalData.hideProgressDialog();
            if (func.equals(Constants.ERROR)) {
                Toast(R.string.error_in_data);
            } else if (func.equals(Constants.FAIL)) {
                Toast(R.string.fail_to_change_password);
            } else if (func.equals(Constants.NO_CONNECTION)) {
                GlobalData.Toast(getActiviy(), R.string.no_internet_connection);
            } else {
                if (IsSuccess) {
                    if (result.getStatus() == 200) {

                        if (UtilityApp.getUserData() != null) {
                            MemberModel memberModel1 = UtilityApp.getUserData();
                            signOut(memberModel1);
                        } else {

                            Intent intent = new Intent(getActiviy(), SplashScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        }


                    } else {

                        String message = getString(R.string.fail_to_change_password);
                        if (result != null && result.getMessage() != null) {
                            message = result.getMessage();
                        }

                        GlobalData.errorDialog(getActiviy(), R.string.reset_pass, message);
                    }


                } else {
                    Toast(R.string.fail_to_change_password);
                }
            }

        }).ChangePasswordHandle(memberModel);
    }

    public void UpdatePassword() {

        final String newPasswordStr = NumberHandler.arabicToDecimal(binding.edtNewPassword.getText().toString());
        MemberModel memberModel = new MemberModel();

        memberModel.setPassword(newPasswordStr);
        memberModel.setNew_password(newPasswordStr);
        memberModel.setMobileNumber(mobileStr);

        GlobalData.progressDialog(getActiviy(), R.string.text_registration_change_password, R.string.please_wait_sending);
        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GeneralModel result = (GeneralModel) obj;

            GlobalData.hideProgressDialog();
            if (func.equals(Constants.ERROR)) {
                Toast(R.string.error_in_data);
            } else if (func.equals(Constants.FAIL)) {
                Toast(R.string.fail_to_change_password);
            } else if (func.equals(Constants.NO_CONNECTION)) {
                GlobalData.Toast(getActiviy(), R.string.no_internet_connection);
            } else {
                if (IsSuccess) {


                    if (result.getStatus() == 200) {
                        Toasty.success(getActiviy(), getString(R.string.success_update), Toast.LENGTH_SHORT, true).show();
                        Intent intent = new Intent(this, RegisterLoginActivity.class);
                        intent.putExtra(Constants.LOGIN, true);
                        startActivity(intent);

                    } else {

                        String message = getString(R.string.fail_to_change_password);
                        if (result != null && result.getMessage() != null) {
                            message = result.getMessage();
                        }

                        GlobalData.errorDialog(getActiviy(), R.string.reset_pass, message);
                    }


                } else {
                    Toast(R.string.fail_to_change_password);
                }
            }

        }).UpdatePasswordHandle(memberModel);
    }


    private final boolean isValidForm() {
        FormValidator formValidator = FormValidator.Companion.getInstance();

        return formValidator.addField(binding.edtPassword, new NonEmptyRule(R.string.old_pass)).
                addField(binding.edtNewPassword, new NonEmptyRule(R.string.new_pass)).
                addField(binding.edtConfirmPassword, new NonEmptyRule(R.string.enter_confirm_password))
                .addField(binding.edtConfirmPassword, new EqualRule(String.valueOf(binding.edtNewPassword.getText()),
                        R.string.password_confirm_not_match)).validate();
    }


    private final boolean isValidFormForget() {
        FormValidator formValidator = FormValidator.Companion.getInstance();

        return formValidator.
                addField(binding.edtNewPassword, new NonEmptyRule(R.string.new_pass)).addField(binding.edtConfirmPassword, new NonEmptyRule(R.string.enter_confirm_password)).addField(binding.edtConfirmPassword, new EqualRule(String.valueOf(binding.edtNewPassword.getText()), R.string.password_confirm_not_match)).validate();


    }


    public void signOut(MemberModel memberModel) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (func.equals(Constants.ERROR)) {
                Toast(R.string.fail_to_sign_out);
            } else if (func.equals(Constants.FAIL)) {
                Toast(R.string.fail_to_sign_out);
            } else if (func.equals(Constants.NO_CONNECTION)) {
                GlobalData.Toast(getActiviy(), R.string.no_internet_connection);
            } else {

                if (IsSuccess) {
                    UtilityApp.logOut();
                    GlobalData.Position = 0;
                    Intent intent = new Intent(getActiviy(), SplashScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast(R.string.fail_to_sign_out);
                    Intent intent = new Intent(getActiviy(), SplashScreenActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

        }).logOut(memberModel);
    }


}