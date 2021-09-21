package com.ramez.shopp.Classes;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.BuildConfig;
import com.ramez.shopp.Models.CountryModel;
import com.ramez.shopp.Models.DinnerModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MainModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.Slider;
import com.ramez.shopp.R;
import com.ramez.shopp.RootApplication;
import com.ramez.shopp.Utils.LocaleUtils;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;


public class UtilityApp {

    public static String getUnique() {

        String android_id = Settings.Secure.getString(RootApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

        return android_id;
    }

    public static int getAppVersion() {

        PackageInfo pinfo = null;
        try {
            pinfo = RootApplication.getInstance().getPackageManager().getPackageInfo(RootApplication.getInstance().getPackageName(), 0);

            int versionNumber = pinfo.versionCode;
            String versionName = pinfo.versionName;

            Log.i("Utility", "Log versionNumber " + versionNumber);
            Log.i("Utility", "Log versionName " + versionName);
            Log.i("Utility", "Log VERSION_CODE " + BuildConfig.VERSION_CODE);

            return versionNumber;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


//        int versionCode = BuildConfig.VERSION_CODE;


        return 0;
    }

    public static String getAppVersionStr() {

        PackageInfo pinfo = null;
        try {
            pinfo = RootApplication.getInstance().getPackageManager().getPackageInfo(RootApplication.getInstance().getPackageName(), 0);

            String versionName = pinfo.versionName;

            Log.i("Utility", "Log versionName " + versionName);
            Log.i("Utility", "Log VERSION_CODE " + BuildConfig.VERSION_CODE);


//            Log.i("Utility", "Log versionNumber " + versionNumber);

            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


//        int versionCode = BuildConfig.VERSION_CODE;


        return "";
    }


    public  static String getToken(){
        final String[] FCMToken = new String[1];
        OneSignal.idsAvailable((userId, registrationId) -> {
            Log.d("debug", "Log User:" + userId);
            if (registrationId != null)
                  FCMToken[0] =OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();

            Log.i("Utility", "Log token one signal first :" + OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
            Log.i("Utility", "Log token firebase:" + UtilityApp.getFCMToken());

        });
        return FCMToken[0];

    }

    public static boolean isFirstRun() {

        boolean isFirstRun = RootApplication.getInstance().getSharedPManger().getDataBool(Constants.KEY_FIRST_RUN, true);
        return isFirstRun;
    }

    public static void setIsFirstRun(boolean isFirstRun) {

        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_FIRST_RUN, isFirstRun);
    }


    public static boolean isFirstLogin(){

        boolean isFirstRun = RootApplication.getInstance().getSharedPManger().getDataBool(Constants.KEY_FIRST_LOGIN, true);
        return isFirstRun;
    }

    public static void setIsFirstLogin(boolean isFirstRun) {

        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_FIRST_LOGIN, isFirstRun);
    }


    public static boolean isLogin() {
        String userToken = RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_MEMBER);
        return userToken != null;
    }




    public static void logOut() {
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_MEMBER, null);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.KEY_LOGIN_PREFERANCE);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.DB_TOTAL_POINTS);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.DB_COUPON_SETTINGS);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.KEY_CATEGORIES);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.KEY_CART_SIZE);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.KEY_DINNERS);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.KEY_BANNER);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.KEY_SLIDER);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.KEY_SOCIAL);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.KEY_SETTING);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.DB_loyal);
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_CART_SIZE, 0);
        RootApplication.getInstance().getSharedPManger().RemoveData(Constants.KEY_MEMBER);




    }

    public static void setToShPref(String key, String data) {
        RootApplication.getInstance().getSharedPManger().SetData(key, data);
    }


    public static void setToShPref(String key, boolean data) {
        RootApplication.getInstance().getSharedPManger().SetData(key, data);
    }

    public static String getFromShPref(String key) {
        return RootApplication.getInstance().getSharedPManger().getDataString(key);
    }

    public static String getFCMToken() {
        return RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_FIREBASE_TOKEN);
    }

    public static void setFCMToken(String fcmToken) {
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_FIREBASE_TOKEN, fcmToken);
    }

    public static String getLanguage() {
        return RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_MEMBER_LANGUAGE);
    }

    public static void setLanguage(String language) {
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_MEMBER_LANGUAGE, language);
    }

    public static void setAppLanguage() {
        String lang = getLanguage();
        if (lang == null) lang = Constants.English;
        LocaleUtils.setLocale(new Locale(lang));
        LocaleUtils.updateConfig(RootApplication.getInstance(), RootApplication.getInstance().getResources().getConfiguration());

    }

    public static MemberModel getUserData() {
        String userJsonData = RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_MEMBER);
        MemberModel user = new Gson().fromJson(userJsonData, MemberModel.class);
        return user;
    }

    public static void setUserData(MemberModel user) {
        String userData = new Gson().toJson(user);
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_MEMBER, userData);
    }


    public static SoicalLink getLinks() {
        String JsonData = RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_SOCIAL);
        SoicalLink linkData = new Gson().fromJson(JsonData, SoicalLink.class);
        return linkData;
    }

    public static void SetLinks(SoicalLink soicalLink) {
        String linkData = new Gson().toJson(soicalLink);
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_SOCIAL, linkData);
    }


    public static ArrayList<CategoryModel> getCategories() {
        String dataString = RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_CATEGORIES);
        return new Gson().fromJson(dataString, new TypeToken<List<CategoryModel>>() {
        }.getType());

    }

    public static void setCategoriesData(ArrayList<CategoryModel> countryModelList) {
        String userData = new Gson().toJson(countryModelList);
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_CATEGORIES, userData);
    }

    public static void setSetting(SettingModel user) {
        String settingData = new Gson().toJson(user);
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_SETTING, settingData);
    }


    public static SettingModel getSettingData() {
        String settingJsonData = RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_SETTING);
        SettingModel settingModel = new Gson().fromJson(settingJsonData, SettingModel.class);
        return settingModel;
    }

    public static ArrayList<CountryModel> getCountriesData() {
        String dataString = RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_COUNTRIES);
        return new Gson().fromJson(dataString, new TypeToken<List<CountryModel>>() {
        }.getType());

    }

    public static void setCountriesData(ArrayList<CountryModel> countryModelList) {
        String userData = new Gson().toJson(countryModelList);
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_COUNTRIES, userData);
    }

    public static LocalModel getLocalData() {
        String localJsonData = RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_Local);
        return new Gson().fromJson(localJsonData, LocalModel.class);
    }

    public static LocalModel getDefaultLocalData(Context context) {
        LocalModel newLocal=new LocalModel();
        newLocal.setCityId(Constants.default_storeId);
        newLocal.setCountryId(Constants.default_country_id);
        newLocal.setCountryNameEn(context.getString(R.string.Bahrain));
        newLocal.setCountryNameAr(context.getString(R.string.Bahrain_ar));
        newLocal.setCurrencyCode(Constants.BHD);
        newLocal.setShortname(context.getString(R.string.bahrain_shotname));
        newLocal.setPhonecode(Constants.default_country_code);

        newLocal.setFractional(Constants.three);
        return newLocal;
    }

    public static void setLocalData(LocalModel model) {
        String localData = new Gson().toJson(model);
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_Local, localData);
    }




    public static Boolean isEnglish() {
        Boolean isEnglish = false;
        if (UtilityApp.getLanguage().equals(Constants.English)) isEnglish = true;
        return isEnglish;

    }


    public static int getCartCount() {
        return RootApplication.getInstance().getSharedPManger().getDataInt(Constants.KEY_CART_SIZE, 0);
    }

    public static void setCartCount(int cartNumber) {
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_CART_SIZE, cartNumber);
    }

    public static void updateCart(int type, int cartListSize) {
        int cartNumber = getCartCount();

        if (type == 1) {
            // add
            cartNumber += 1;
            setCartCount(cartNumber);

        } else if (type == 2) {
            // delete
            cartNumber -= 1;
            setCartCount(cartNumber);

            if (cartListSize == 0) {
                Log.i("tag", "Log cartListSize" + cartListSize);
                setCartCount(0);
            }

        }

        EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_UPDATE_CART));


    }


    public static ArrayList<Slider> getSliders() {
        String dataString = RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_SLIDER);
        return new Gson().fromJson(dataString, new TypeToken<List<Slider>>() {
        }.getType());

    }




    public static void setSliderData(ArrayList<Slider> sliderData) {
        String userData = new Gson().toJson(sliderData);
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_SLIDER, userData);
    }


    public static ArrayList<Slider> getBanners() {
        String dataString = RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_BANNER);
        return new Gson().fromJson(dataString, new TypeToken<List<Slider>>() {
        }.getType());

    }



    public static void setBannerData(ArrayList<Slider> bannerData) {
        String userData = new Gson().toJson(bannerData);
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_BANNER, userData);
    }



    public static ArrayList<DinnerModel> getDinners() {
        String dataString = RootApplication.getInstance().getSharedPManger().getDataString(Constants.KEY_DINNERS);
        return new Gson().fromJson(dataString, new TypeToken<List<DinnerModel>>() {
        }.getType());

    }

    public static void setDinnersData(ArrayList<DinnerModel> bannerData) {
        String userData = new Gson().toJson(bannerData);
        RootApplication.getInstance().getSharedPManger().SetData(Constants.KEY_DINNERS, userData);
    }









}
