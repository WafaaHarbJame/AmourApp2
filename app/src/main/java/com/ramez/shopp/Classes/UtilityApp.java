package com.ramez.shopp.Classes;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OneSignal;
import com.ramez.shopp.BuildConfig;
import com.ramez.shopp.Models.CountryModel;
import com.ramez.shopp.Models.DinnerModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.Slider;
import com.ramez.shopp.R;
import com.ramez.shopp.RootApplication;
import com.ramez.shopp.Utils.LocaleUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class UtilityApp {

    public static String getUnique() {

        String android_id = Settings.Secure.getString(RootApplication.Companion.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

        return android_id;
    }

    public static int getAppVersion() {

        PackageInfo pinfo = null;
        try {
            pinfo = RootApplication.Companion.getInstance().getPackageManager().getPackageInfo(RootApplication.Companion.getInstance().getPackageName(), 0);

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
            pinfo = RootApplication.Companion.getInstance().getPackageManager().getPackageInfo(RootApplication.Companion.getInstance().getPackageName(), 0);

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


    public static String getToken() {
//        final String[] FCMToken = new String[1];
//        OneSignal.idsAvailable((userId, registrationId) -> {
//            Log.d("debug", "Log User:" + userId);
//            if (registrationId != null)
//                FCMToken[0] = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
//
//            Log.i("Utility", "Log token one signal first :" + OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
//            Log.i("Utility", "Log token firebase:" + UtilityApp.getFCMToken());
//
//        });
//        return FCMToken[0];

        String FCMToken= OneSignal.getDeviceState().getUserId();
        UtilityApp.setFCMToken(FCMToken);
        return FCMToken;

    }

    public static boolean isFirstRun() {

        boolean isFirstRun = RootApplication.Companion.getInstance().getSharedPManger().getDataBool(Constants.KEY_FIRST_RUN, true);
        return isFirstRun;
    }

    public static void setIsFirstRun(boolean isFirstRun) {

        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_FIRST_RUN, isFirstRun);
    }


    public static boolean isFirstLogin() {

        boolean isFirstRun = RootApplication.Companion.getInstance().getSharedPManger().getDataBool(Constants.KEY_FIRST_LOGIN, true);
        return isFirstRun;
    }


    public static void setIsFirstLogin(boolean isFirstRun) {

        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_FIRST_LOGIN, isFirstRun);
    }


    public static void setBranchName(String branchName) {

        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_BRANCH_NAME, branchName);
    }

    public static String getBranchName() {

        return RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_BRANCH_NAME, "");
    }

    public static boolean isLogin() {
        String userToken = RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_MEMBER);
        return userToken != null;
    }


    public static void logOut() {
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_MEMBER, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_LOGIN_PREFERANCE, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.DB_TOTAL_POINTS, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.DB_COUPON_SETTINGS, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.DB_TOTAL_POINTS, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_CATEGORIES, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_CART_SIZE, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_CATEGORIES, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_DINNERS, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_BANNER, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_SLIDER, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_SOCIAL, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_SETTING, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.DB_loyal, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_SCAN_SOUND, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_SCAN_AGAIN, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_CART_FASTQ_Total, null);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_CART_FASTQ_SIZE, null);

        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_LOGIN_PREFERANCE);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.DB_TOTAL_POINTS);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.DB_COUPON_SETTINGS);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_CATEGORIES);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_CART_SIZE);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_DINNERS);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_BANNER);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_SLIDER);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_SOCIAL);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_SETTING);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.DB_loyal);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_MEMBER);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_SCAN_SOUND);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_SCAN_AGAIN);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_CART_FASTQ_Total);
        RootApplication.Companion.getInstance().getSharedPManger().RemoveData(Constants.KEY_CART_FASTQ_SIZE);


    }

    public static void setToShPref(String key, String data) {
        RootApplication.Companion.getInstance().getSharedPManger().SetData(key, data);
    }


    public static void setToShPref(String key, boolean data) {
        RootApplication.Companion.getInstance().getSharedPManger().SetData(key, data);
    }

    public static String getFromShPref(String key) {
        return RootApplication.Companion.getInstance().getSharedPManger().getDataString(key);
    }

    public static String getFCMToken() {
        return RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_FIREBASE_TOKEN);
    }

    public static void setFCMToken(String fcmToken) {
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_FIREBASE_TOKEN, fcmToken);
    }

    public static String getLanguage() {
        return RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_MEMBER_LANGUAGE);
    }

    public static void setLanguage(String language) {
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_MEMBER_LANGUAGE, language);
    }

    public static void setAppLanguage() {
        String lang = getLanguage();
        if (lang == null) lang = Constants.English;
        LocaleUtils.setLocale(new Locale(lang));
        LocaleUtils.updateConfig(RootApplication.Companion.getInstance(), RootApplication.Companion.getInstance().getResources().getConfiguration());

    }

    public static MemberModel getUserData() {
        String userJsonData = RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_MEMBER);
        return new Gson().fromJson(userJsonData, MemberModel.class);
    }

    public static void setUserData(MemberModel user) {
        String userData = new Gson().toJson(user);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_MEMBER, userData);
    }


    public static SoicalLink getLinks() {
        String JsonData = RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_SOCIAL);
        SoicalLink linkData = new Gson().fromJson(JsonData, SoicalLink.class);
        return linkData;
    }

    public static void SetLinks(SoicalLink soicalLink) {
        String linkData = new Gson().toJson(soicalLink);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_SOCIAL, linkData);
    }


    public static ArrayList<CategoryModel> getCategories() {
        String dataString = RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_CATEGORIES);
        return new Gson().fromJson(dataString, new TypeToken<List<CategoryModel>>() {
        }.getType());

    }

    public static void setCategoriesData(ArrayList<CategoryModel> countryModelList) {
        String userData = new Gson().toJson(countryModelList);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_CATEGORIES, userData);
    }

    public static void setSetting(SettingModel user) {
        String settingData = new Gson().toJson(user);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_SETTING, settingData);
    }


    public static SettingModel getSettingData() {
        String settingJsonData = RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_SETTING);
        SettingModel settingModel = new Gson().fromJson(settingJsonData, SettingModel.class);
        return settingModel;
    }

    public static ArrayList<CountryModel> getCountriesData() {
        String dataString = RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_COUNTRIES);
        return new Gson().fromJson(dataString, new TypeToken<List<CountryModel>>() {
        }.getType());

    }

    public static void setCountriesData(ArrayList<CountryModel> countryModelList) {
        String userData = new Gson().toJson(countryModelList);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_COUNTRIES, userData);
    }

    public static LocalModel getLocalData() {
        String localJsonData = RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_Local);
        return new Gson().fromJson(localJsonData, LocalModel.class);
    }

    public static LocalModel getDefaultLocalData(Context context) {
        LocalModel newLocal = new LocalModel();
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
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_Local, localData);
    }


    public static Boolean isEnglish() {
        Boolean isEnglish = false;
        if (UtilityApp.getLanguage().equals(Constants.English))
            isEnglish = true;
        return isEnglish;

    }


    public static int getCartCount() {
        return RootApplication.Companion.getInstance().getSharedPManger().getDataInt(Constants.KEY_CART_SIZE, 0);
    }

    public static void setCartCount(int cartNumber) {
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_CART_SIZE, cartNumber);
    }


    public static int getFastQCartCount() {
        return RootApplication.Companion.getInstance().getSharedPManger().getDataInt(Constants.KEY_CART_FASTQ_SIZE, 0);
    }

    public static void setFastQCartCount(int cartNumber) {
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_CART_FASTQ_SIZE, cartNumber);
    }


    public static float getFastQCartTotal() {
        return RootApplication.Companion.getInstance().getSharedPManger().getDataFloat(Constants.KEY_CART_FASTQ_Total);
    }


    public static void setFastQCartTotal(float total) {
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_CART_FASTQ_Total, total);
    }


    public static boolean getContinuousScan() {
        return RootApplication.Companion.getInstance().getSharedPManger().getDataBool(Constants.KEY_SCAN_AGAIN, false);
    }

    public static void setContinuousScan(boolean scanAgain) {
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_SCAN_AGAIN, scanAgain);
    }

    public static String getUrl() {
        return RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_URL,GlobalData.BetaBaseURL1);
    }

    public static void setUrl(String url) {
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_URL, url);
    }

    public static boolean getScanSound() {
        return RootApplication.Companion.getInstance().getSharedPManger().getDataBool(Constants.KEY_SCAN_SOUND, false);
    }

    public static void setScanSound(boolean scanSound) {
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_SCAN_SOUND, scanSound);
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
        String dataString = RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_SLIDER);
        return new Gson().fromJson(dataString, new TypeToken<List<Slider>>() {
        }.getType());

    }


    public static void setSliderData(ArrayList<Slider> sliderData) {
        String userData = new Gson().toJson(sliderData);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_SLIDER, userData);
    }


    public static ArrayList<Slider> getBanners() {
        String dataString = RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_BANNER);
        return new Gson().fromJson(dataString, new TypeToken<List<Slider>>() {
        }.getType());

    }


    public static void setBannerData(ArrayList<Slider> bannerData) {
        String userData = new Gson().toJson(bannerData);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_BANNER, userData);
    }


    public static ArrayList<DinnerModel> getDinners() {
        String dataString = RootApplication.Companion.getInstance().getSharedPManger().getDataString(Constants.KEY_DINNERS);
        return new Gson().fromJson(dataString, new TypeToken<List<DinnerModel>>() {
        }.getType());

    }

    public static void setDinnersData(ArrayList<DinnerModel> bannerData) {
        String userData = new Gson().toJson(bannerData);
        RootApplication.Companion.getInstance().getSharedPManger().SetData(Constants.KEY_DINNERS, userData);
    }


}
