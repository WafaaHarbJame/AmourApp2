package com.ramez.shopp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CartModel;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.SettingModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.CartResultModel;
import com.ramez.shopp.Models.CategoryResultModel;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProfileData;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static android.content.ContentValues.TAG;

public class SplashScreenActivity extends ActivityBase {
    private static final int SPLASH_TIMER = 3000;
    int storeId, userId;
    MemberModel user;
    LocalModel localModel;
    int cartNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        startSplash();

    }


    private void startSplash() {

        setContentView(R.layout.activity_splash_screen);
        getSetting();

        localModel = UtilityApp.getLocalData();

        if (UtilityApp.getLocalData() != null) {
            getCategories(Integer.parseInt(localModel.getCityId()));

        }
        initData();

    }

    private void initData() {

        new Handler().postDelayed(() -> {

            if (UtilityApp.isLogin()) {
                if (UtilityApp.getUserData() != null) {
                    localModel = UtilityApp.getLocalData();
                    storeId = Integer.parseInt(localModel.getCityId());
                    user = UtilityApp.getUserData();
                    userId = user.getId();
                    Log.i(TAG, "Log user_id " + userId);
                    Log.i(TAG, "Log storeId " + storeId);
                    getUserData(userId);


                }

            } else {

                if (!UtilityApp.isFirstRun()) {
                    Intent intent = new Intent(getActiviy(), MainActivity.class);
                    intent.putExtra(Constants.LOGIN, true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    startWelcomeActivity();
                }
            }


        }, SPLASH_TIMER);
    }

    private void startWelcomeActivity() {
        startActivity(new Intent(getActiviy(), ChangeLanguageActivity.class));

    }


    public void getUserData(int user_id) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ProfileData> result = (ResultAPIModel<ProfileData>) obj;
            String message = getString(R.string.fail_to_get_data);

            if (func.equals(Constants.ERROR)) {

                Intent intent = new Intent(getActiviy(), RegisterLoginActivity.class);
                intent.putExtra(Constants.LOGIN, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } else if (func.equals(Constants.FAIL)) {

                Intent intent = new Intent(getActiviy(), RegisterLoginActivity.class);
                intent.putExtra(Constants.LOGIN, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);


            } else if (func.equals(Constants.NO_CONNECTION)) {
                //  Toasty.error(getActiviy(),R.string.no_internet_connection, Toast.LENGTH_SHORT, true).show();

            } else if (IsSuccess) {
                MemberModel memberModel = UtilityApp.getUserData();
                if(result!=null&&result.data!=null){
                    memberModel.setName(result.data.getName());
                    memberModel.setEmail(result.data.getEmail());
                    memberModel.setProfilePicture(result.data.getProfilePicture());
                    UtilityApp.setUserData(memberModel);
                    getCarts(storeId, userId);

                }


            } else {
                Intent intent = new Intent(getActiviy(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }


        }).getUserDetails(user_id);
    }

    public void getSetting() {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<SettingModel> result = (ResultAPIModel<SettingModel>) obj;

            if (func.equals(Constants.ERROR)) {

                // Toasty.error(getActiviy(),R.string.error_in_data, Toast.LENGTH_SHORT, true).show();

            } else if (func.equals(Constants.FAIL)) {

                //Toasty.error(getActiviy(),R.string.fail_to_get_data, Toast.LENGTH_SHORT, true).show();


            } else if (func.equals(Constants.NO_CONNECTION)) {
                //   Toasty.error(getActiviy(),R.string.no_internet_connection, Toast.LENGTH_SHORT, true).show();


            }

            if (IsSuccess) {
                SettingModel settingModel = new SettingModel();
                if (result.data != null) {
                    settingModel.setAbout(result.data.getAbout());
                    settingModel.setConditions(result.data.getConditions());
                    settingModel.setPrivacy(result.data.getPrivacy());
                    UtilityApp.setSetting(settingModel);
                }

            } else {
                // Toasty.error(getActiviy(),R.string.error_in_data, Toast.LENGTH_SHORT, true).show();

            }


        }).getSetting();
    }

    public void getCategories(int storeId) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {

            CategoryResultModel result = (CategoryResultModel) obj;

            if (IsSuccess) {
                if (result.getData() != null && result.getData().size() > 0) {
                    ArrayList<CategoryModel> categoryModelList = result.getData();
                    UtilityApp.setCategoriesData(categoryModelList);
                }

            }

        }).GetAllCategories(storeId);
    }


    public void getCarts(int storeId, int userId) {


        new DataFeacher(false, (obj, func, IsSuccess) -> {
            CartResultModel cartResultModel = (CartResultModel) obj;
            String message = getString(R.string.fail_to_get_data);

            if (IsSuccess) {
                if (cartResultModel.getStatus() == 200) {

                    if (cartResultModel.getData() != null && cartResultModel.getData().getCartData() != null && cartResultModel.getData().getCartData().size() > 0) {
                        cartNumber = cartResultModel.getCartCount();
                        UtilityApp.setCartCount(cartNumber);
                       int minimum_order_amount = cartResultModel.getMinimumOrderAmount();
                        localModel.setMinimum_order_amount(minimum_order_amount);
                        UtilityApp.setLocalData(localModel);

                        Intent intent = new Intent(getActiviy(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } else {
                        UtilityApp.setCartCount(0);
                        Intent intent = new Intent(getActiviy(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                } else {
                    UtilityApp.setCartCount(0);
                    Intent intent = new Intent(getActiviy(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                finish();


            }

        }).GetCarts(storeId, userId);
    }

}