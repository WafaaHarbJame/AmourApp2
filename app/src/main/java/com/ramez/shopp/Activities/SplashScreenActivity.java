package com.ramez.shopp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.DBFunction;
import com.ramez.shopp.Classes.SettingModel;
import com.ramez.shopp.Classes.SoicalLink;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.AddressModel;
import com.ramez.shopp.Models.AddressResultModel;
import com.ramez.shopp.Models.CartResultModel;
import com.ramez.shopp.Models.CategoryResultModel;
import com.ramez.shopp.Models.CountryDetailsModel;
import com.ramez.shopp.Models.Data;
import com.ramez.shopp.Models.DinnerModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MainModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProfileData;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.SettingCouponsModel;
import com.ramez.shopp.Models.Slider;
import com.ramez.shopp.Models.TotalPointModel;
import com.ramez.shopp.R;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class SplashScreenActivity extends ActivityBase {
    private static final int SPLASH_TIMER = 3500;
    int storeId, userId = 0;
    MemberModel user;
    LocalModel localModel;
    int cartNumber;
    int country_id = 0;
    private String lang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        startSplash();

    }


    private void startSplash() {

        setContentView(R.layout.activity_splash_screen);
        lang = UtilityApp.getLanguage() == null ? Locale.getDefault().getLanguage() : UtilityApp.getLanguage();

//        getSetting();

        localModel = UtilityApp.getLocalData();

        if (localModel != null && localModel.getCityId() != null) {
            storeId = Integer.parseInt(localModel.getCityId());
            country_id = localModel.getCountryId();
            if (localModel.getShortname() != null) {
                getLinks(Integer.parseInt(UtilityApp.getLocalData().getCityId()));
            }
            getCategories(Integer.parseInt(localModel.getCityId()));
            getDinners(lang);
            GetHomePage();
            getCouponSettings(country_id);
            getCountryDetail(localModel.getShortname());


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
                    getUserData(userId, storeId);
                    getTotalPoints(userId);
                    GetUserAddress(userId);

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

    public void getUserData(int user_id, int store_id) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            String message = getString(R.string.fail_to_get_data);

            ResultAPIModel<ProfileData> result = (ResultAPIModel<ProfileData>) obj;
            if (IsSuccess && result != null && result.data != null) {
                MemberModel memberModel = UtilityApp.getUserData();
                memberModel.setName(result.data.getName());
                memberModel.setEmail(result.data.getEmail());
                memberModel.setLoyalBarcode(result.data.getLoyalBarcode());
                memberModel.setProfilePicture(result.data.getLoyalBarcode());
                UtilityApp.setUserData(memberModel);
                getCarts(storeId, userId);
            } else {
                UtilityApp.logOut();
                Intent intent = new Intent(getActiviy(), RegisterLoginActivity.class);
                intent.putExtra(Constants.LOGIN, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }).getUserDetails(user_id, store_id);
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

            }


        }).getSetting();
    }


    private void getTotalPoints(int userId) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<TotalPointModel> result = (ResultAPIModel<TotalPointModel>) obj;

            if (result != null && result.isSuccessful() && result.data != null) {
                    TotalPointModel totalPointModel = result.data;
                    Log.i(getClass().getSimpleName(), "Log  totalPointModel call " + totalPointModel.points);
                    Log.i(getClass().getSimpleName(), "Log  totalPointModel call" + totalPointModel.value);

                    DBFunction.setTotalPoints(totalPointModel);



            }

        }).getTotalPoint(userId);
    }

    private void getCountryDetail(String shortName) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<CountryDetailsModel> result = (ResultAPIModel<CountryDetailsModel>) obj;

            if (result != null && result.isSuccessful() && result.data != null) {
                    CountryDetailsModel  countryDetailsModel = result.data;
                    DBFunction.setLoyal(countryDetailsModel);



            }

        }).getCountryDetail(shortName);
    }


    private void getCouponSettings(int countryId) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<SettingCouponsModel> result = (ResultAPIModel<SettingCouponsModel>) obj;

            if (result != null && result.data != null && result.status==200) {

                    SettingCouponsModel settingCouponsModel = result.data;
                    DBFunction.setCouponSettings(settingCouponsModel);


            }

        }).getSettings(countryId);
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

    public void getDinners(String lang) {
        UtilityApp.setDinnersData(null);
        Log.i("TAG", "Log dinners Size");
        Log.i("TAG", "Log countryid " + country_id);


        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<ArrayList<DinnerModel>> result = (ResultAPIModel<ArrayList<DinnerModel>>) obj;

            if (IsSuccess) {
                if (result.data != null && result.data.size() > 0) {
                    Log.i("TAG", "Log dinners Size" + result.data.size());
                    ArrayList<DinnerModel> dinnerModels = result.data;
                    UtilityApp.setDinnersData(dinnerModels);
                }


            }


        }).getDinnersList(lang);
    }


    public void getCarts(int storeId, int userId) {


        new DataFeacher(false, (obj, func, IsSuccess) -> {
            CartResultModel cartResultModel = (CartResultModel) obj;
            if (IsSuccess) {
                if (cartResultModel.getStatus() == 200) {

                    if (cartResultModel.getData() != null && cartResultModel.getData().getCartData()
                            != null && cartResultModel.getData().getCartData().size() > 0) {
                        Data data=cartResultModel.getData();
                        cartNumber = data.getCartCount();
                        UtilityApp.setCartCount(cartNumber);
                        int minimum_order_amount = data.getMinimumOrderAmount();
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

    public void getLinks(int store_id) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<SoicalLink> result = (ResultAPIModel<SoicalLink>) obj;


            if (IsSuccess) {
                SoicalLink soicalLink = result.data;
                UtilityApp.SetLinks(soicalLink);

            }


        }).getLinks(store_id);
    }

    public void GetHomePage() {
        Log.i(TAG, "Log GetMainPage new");
        Log.i(TAG, "Log country_id " + country_id);
        Log.i(TAG, "Log user_id " + userId);
        Log.i(TAG, "Log city_id " + storeId);

        new DataFeacher(false, (obj, func, IsSuccess) -> {

            MainModel result = (MainModel) obj;

            if (IsSuccess) {

                ArrayList<Slider> sliderList = new ArrayList<>();
                ArrayList<Slider> bannersList = new ArrayList<>();
                ;
                if (result.getSliders().size() > 0) {

                    for (int i = 0; i < result.getSliders().size(); i++) {
                        Slider slider = result.getSliders().get(i);
                        if (slider.getType() == 0) {
                            sliderList.add(slider);

                        } else {
                            bannersList.add(slider);

                        }

                    }

                    UtilityApp.setSliderData(sliderList);
                    UtilityApp.setBannerData(bannersList);


                }
            }


        }).GetMainPage(0, country_id, storeId, String.valueOf(userId));
    }


    public void GetUserAddress(int user_id) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
               AddressResultModel result = (AddressResultModel) obj;


                if (IsSuccess) {
                    if (result.getData() != null && result.getData().size() > 0) {
                        ArrayList<AddressModel> addressList=new ArrayList<>();
                        addressList=result.getData();
                        for (int i = 0; i <addressList.size() ; i++) {
                            AddressModel addressModel = addressList.get(i);
                            if(addressModel.getDefault()){
                                MemberModel user=UtilityApp.getUserData();
                                user.setLastSelectedAddress(addressModel.getId());
                                UtilityApp.setUserData(user);
                            }

                        }




                }
            }

        }).GetAddressHandle(user_id);
    }








}