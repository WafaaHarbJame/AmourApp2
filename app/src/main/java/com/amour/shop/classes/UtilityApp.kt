package com.amour.shop.classes

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.onesignal.OneSignal
import com.amour.shop.BuildConfig
import com.amour.shop.Models.*
import com.amour.shop.R
import com.amour.shop.RootApplication.Companion.instance
import com.amour.shop.Utils.LocaleUtils
import org.greenrobot.eventbus.EventBus
import java.util.*


object UtilityApp {
    val unique: String
        get() = Settings.Secure.getString(instance!!.contentResolver, Settings.Secure.ANDROID_ID)

    //        int versionCode = BuildConfig.VERSION_CODE;
    val appVersion: Int
        get() {
            var pinfo: PackageInfo? = null
            try {
                pinfo = instance!!.packageManager.getPackageInfo(
                    instance!!.packageName, 0
                )
                val versionNumber = pinfo.versionCode
                val versionName = pinfo.versionName
                Log.i("Utility", "Log versionNumber $versionNumber")
                Log.i("Utility", "Log versionName $versionName")
                Log.i("Utility", "Log VERSION_CODE " + BuildConfig.VERSION_CODE)
                return versionNumber
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }


            //        int versionCode = BuildConfig.VERSION_CODE;
            return 0
        }

    //            Log.i("Utility", "Log versionNumber " + versionNumber);


    //        int versionCode = BuildConfig.VERSION_CODE;
    fun getAppVersionStr(): String {
        var pinfo: PackageInfo? = null
        try {
            pinfo = instance!!.packageManager.getPackageInfo(
                instance!!.packageName, 0
            )
            val versionName = pinfo.versionName
            Log.i("Utility", "Log versionName $versionName")
            Log.i("Utility", "Log VERSION_CODE " + BuildConfig.VERSION_CODE)


            //            Log.i("Utility", "Log versionNumber " + versionNumber);
            return versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }


        //        int versionCode = BuildConfig.VERSION_CODE;
        return ""
    }

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
    fun getToken(): String {
        val FCMToken = OneSignal.getDeviceState()?.userId
        setFCMToken(FCMToken)
        return FCMToken ?: ""
    }
//    val token: String
//        get() {
//            //        final String[] FCMToken = new String[1];
//            //        OneSignal.idsAvailable((userId, registrationId) -> {
//            //            Log.d("debug", "Log User:" + userId);
//            //            if (registrationId != null)
//            //                FCMToken[0] = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
//            //
//            //            Log.i("Utility", "Log token one signal first :" + OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());
//            //            Log.i("Utility", "Log token firebase:" + UtilityApp.getFCMToken());
//            //
//            //        });
//            //        return FCMToken[0];
//            val FCMToken = OneSignal.getDeviceState()!!.userId
//            setFCMToken(FCMToken)
//            return FCMToken
//        }

    fun isFirstRun(): Boolean {
        return instance!!.sharedPManger!!.getDataBool(Constants.KEY_FIRST_RUN, true)
    }

    fun setIsFirstRun(isFirstRun: Boolean) {
        instance!!.sharedPManger!!.SetData(Constants.KEY_FIRST_RUN, isFirstRun)
    }

    fun isFirstLogin(): Boolean {
        return instance!!.sharedPManger!!.getDataBool(Constants.KEY_FIRST_LOGIN, true)
    }

    fun setIsFirstLogin(isFirstRun: Boolean) {
        instance!!.sharedPManger!!.SetData(Constants.KEY_FIRST_LOGIN, isFirstRun)
    }

    fun setBranchName(branchName: String?) {
        instance!!.sharedPManger!!.SetData(Constants.KEY_BRANCH_NAME, branchName)
    }

    fun getBranchName(): String {
        return instance!!.sharedPManger!!.getDataString(Constants.KEY_BRANCH_NAME, "") ?: ""
    }

    fun saveCountryBranches(countryId: Int, branchesList: List<CityModel?>?) {
        val jsonData = Gson().toJson(branchesList)
        instance!!.sharedPManger!!.SetData(Constants.KEY_BRANCHES_PREFIX + countryId, jsonData)
    }

    fun getCountryBranches(countryId: Int): MutableList<CityModel>? {
        val jsonData = instance!!.sharedPManger!!.getDataString(Constants.KEY_BRANCHES_PREFIX + countryId, "")
        return if (jsonData != null) Gson().fromJson(
            jsonData,
            object : TypeToken<MutableList<CityModel?>?>() {}.type
        ) else null
    }

    fun setUserToken(userToken: String?) {
        instance!!.sharedPManger!!.SetData(Constants.KEY_TOKEN, userToken)
    }

    fun getUserToken(): String? {
        return instance!!.sharedPManger?.getDataString(Constants.KEY_TOKEN, "")
    }

    fun getRefreshToken(): String? {
        return instance!!.sharedPManger?.getDataString(Constants.KEY_REFRESH_TOKEN, "")
    }

    fun setRefreshToken(refreshToken: String?) {
        instance!!.sharedPManger!!.SetData(Constants.KEY_REFRESH_TOKEN, refreshToken)
    }

    fun isLogin(): Boolean {
        val userToken = instance!!.sharedPManger!!.getDataString(Constants.KEY_MEMBER)
        return userToken != null
    }

    fun logOut() {
        instance?.sharedPManger?.SetData(Constants.KEY_MEMBER, null)
        instance?.sharedPManger?.SetData(Constants.KEY_LOGIN_PREFERANCE, null)
        instance!!.sharedPManger?.SetData(Constants.DB_TOTAL_POINTS, null)
        instance!!.sharedPManger?.SetData(Constants.DB_COUPON_SETTINGS, null)
        instance!!.sharedPManger?.SetData(Constants.DB_TOTAL_POINTS, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_CATEGORIES, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_CART_SIZE, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_CATEGORIES, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_DINNERS, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_BANNER, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_SLIDER, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_SOCIAL, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_SETTING, null)
        instance!!.sharedPManger?.SetData(Constants.DB_loyal, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_SCAN_SOUND, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_SCAN_AGAIN, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_CART_FASTQ_Total, null)
        instance!!.sharedPManger?.SetData(Constants.KEY_CART_FASTQ_SIZE, null)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_LOGIN_PREFERANCE)
        instance!!.sharedPManger?.RemoveData(Constants.DB_TOTAL_POINTS)
        instance!!.sharedPManger?.RemoveData(Constants.DB_COUPON_SETTINGS)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_CATEGORIES)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_CART_SIZE)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_DINNERS)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_BANNER)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_SLIDER)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_SOCIAL)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_SETTING)
        instance!!.sharedPManger?.RemoveData(Constants.DB_loyal)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_MEMBER)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_SCAN_SOUND)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_SCAN_AGAIN)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_CART_FASTQ_Total)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_CART_FASTQ_SIZE)
        instance!!.sharedPManger?.RemoveData(Constants.KEY_TOKEN)
    }

    fun setToShPref(key: String?, data: String?) {
        instance!!.sharedPManger!!.SetData(key, data)
    }

    fun setToShPref(key: String?, data: Boolean) {
        instance!!.sharedPManger!!.SetData(key, data)
    }

    fun getFromShPref(key: String?): String? {
        return instance!!.sharedPManger?.getDataString(key)
    }

    fun getFCMToken(): String? {
        return instance!!.sharedPManger?.getDataString(Constants.KEY_FIREBASE_TOKEN)
    }

    fun setFCMToken(fcmToken: String?) {
        instance!!.sharedPManger!!.SetData(Constants.KEY_FIREBASE_TOKEN, fcmToken)
    }

    fun getLanguage(): String? {
        return instance!!.sharedPManger!!.getDataString(Constants.KEY_MEMBER_LANGUAGE)
    }

    fun setLanguage(language: String?) {
        instance!!.sharedPManger?.SetData(Constants.KEY_MEMBER_LANGUAGE, language)
    }

    fun getNormalDelivery(): String? {
        return instance?.sharedPManger?.getDataString(Constants.KEY_NORMAL_DELIVERY)
    }

    fun setNormalDelivery(normalDelivery: String?) {
        instance?.sharedPManger!!.SetData(Constants.KEY_NORMAL_DELIVERY, normalDelivery)
    }

    fun setAppLanguage() {
        var lang = getLanguage()
        if (lang == null) lang = Constants.English
        LocaleUtils.setLocale(Locale(lang))
        LocaleUtils.updateConfig(instance, instance!!.resources.configuration)
    }

    fun getUserData(): MemberModel? {
        val userJsonData = instance!!.sharedPManger!!.getDataString(Constants.KEY_MEMBER)
        return Gson().fromJson(userJsonData, MemberModel::class.java)
    }

    fun setUserData(user: MemberModel?) {
        val userData = Gson().toJson(user)
        instance!!.sharedPManger!!.SetData(Constants.KEY_MEMBER, userData)
    }

    fun getLinks(): SoicalLink {
        val JsonData =
            instance!!.sharedPManger!!.getDataString(Constants.KEY_SOCIAL)
        return Gson().fromJson(JsonData, SoicalLink::class.java)
    }

    fun SetLinks(soicalLink: SoicalLink?) {
        val linkData = Gson().toJson(soicalLink)
        instance!!.sharedPManger!!.SetData(Constants.KEY_SOCIAL, linkData)
    }

    fun getCategories(): MutableList<CategoryModel>? {
        val dataString = instance!!.sharedPManger!!.getDataString(Constants.KEY_CATEGORIES)
        return if (dataString?.isNotEmpty() == true) Gson().fromJson(
            dataString,
            object : TypeToken<MutableList<CategoryModel?>?>() {}.type
        ) else null
    }

    fun setCategoriesData(countryModelList: List<CategoryModel>?) {
        val userData = Gson().toJson(countryModelList)
        instance!!.sharedPManger!!.SetData(Constants.KEY_CATEGORIES, userData)
    }


    fun setAllKindsData(categoryModels: List<KindCategoryModel?>?) {
        val userData = Gson().toJson(categoryModels)
        instance!!.sharedPManger!!.SetData(Constants.KEY_ALL_KINDS, userData)
    }

    fun getAllKinds(): MutableList<KindCategoryModel>? {
        val dataString = instance!!.sharedPManger!!.getDataString(Constants.KEY_ALL_KINDS)
        return if (dataString != null) Gson().fromJson(
            dataString,
            object : TypeToken<MutableList<KindCategoryModel?>?>() {}.type
        ) else null
    }

    fun getPaymentsData(): MutableList<PaymentModel?>? {
        val dataString = instance!!.sharedPManger!!.getDataString(Constants.KEY_PAYMENT)
        return Gson().fromJson(dataString, object : TypeToken<List<PaymentModel?>?>() {}.type)
    }

    fun getBrandsData(): MutableList<BrandModel> {
        val dataString = instance!!.sharedPManger!!.getDataString(Constants.KEY_BRANDS)
        val list = if (dataString != null) Gson().fromJson<MutableList<BrandModel>>(
            dataString,
            object : TypeToken<MutableList<BrandModel?>?>() {}.type
        ) else null
        val brandModels = mutableListOf<BrandModel>()
        if (list != null) {
            for (brandModel in list) {
                if (brandModel.image != null && !brandModel.image.isEmpty() || brandModel.image2 != null && !brandModel.image2.isEmpty()) {
                    brandModels.add(brandModel)
                }
            }
        }
        return brandModels
    }

    fun setBrandsData(brandsData: List<BrandModel?>?) {
        val paymentData = Gson().toJson(brandsData)
        instance!!.sharedPManger!!.SetData(Constants.KEY_BRANDS, paymentData)
    }

    fun setPaymentsData(paymentsData: MutableList<PaymentModel?>?) {
        val paymentData = Gson().toJson(paymentsData)
        instance!!.sharedPManger!!.SetData(Constants.KEY_PAYMENT, paymentData)
    }

    fun setSetting(user: SettingModel?) {
        val settingData = Gson().toJson(user)
        instance!!.sharedPManger!!.SetData(Constants.KEY_SETTING, settingData)
    }

    fun getSettingData(): SettingModel? {
        val settingJsonData =
            instance!!.sharedPManger!!.getDataString(Constants.KEY_SETTING)
        return Gson().fromJson(settingJsonData, SettingModel::class.java)
    }

    fun getCountriesData(): List<CountryModel> {
        val dataString = instance!!.sharedPManger!!.getDataString(Constants.KEY_COUNTRIES)
        return Gson().fromJson(dataString, object : TypeToken<List<CountryModel?>?>() {}.type)
    }

    fun setCountriesData(countryModelList: List<CountryModel?>?) {
        val userData = Gson().toJson(countryModelList)
        instance!!.sharedPManger!!.SetData(Constants.KEY_COUNTRIES, userData)
    }

    fun getLocalData(): LocalModel {
        val localJsonData = instance!!.sharedPManger!!.getDataString(Constants.KEY_Local)
        return if (localJsonData?.isNotEmpty() == true) {
            Gson().fromJson(localJsonData, LocalModel::class.java)
        } else {
            getDefaultLocalDataAmour()
        }
    }


    fun getDefaultLocalDataAmour(): LocalModel {
        val newLocal = LocalModel()
        newLocal.cityId = Constants.default_storeId
        newLocal.countryId = Constants.default_amour_country_id
//        newLocal.countryNameEn = context?.getString(R.string.Bahrain)
        newLocal.countryNameEn = "Bahrain"
//        newLocal.countryNameAr = context?.getString(R.string.Bahrain_ar)
        newLocal.countryNameAr = "البحرين"
        newLocal.currencyCode = Constants.BHD
//        newLocal.shortname = context?.getString(R.string.amour_shotname)
        newLocal.shortname = "DM"
        newLocal.phonecode = Constants.default_country_code
        newLocal.fractional = Constants.three
        return newLocal
    }

    fun setLocalData(model: LocalModel?) {
        val localData = Gson().toJson(model)
        instance!!.sharedPManger!!.SetData(Constants.KEY_Local, localData)
    }

    fun isEnglish(): Boolean {
        var isEnglish = false
        if (getLanguage() == Constants.English) isEnglish = true
        return isEnglish
    }

    fun getCartCount(): Int {
        return instance!!.sharedPManger!!.getDataInt(Constants.KEY_CART_SIZE, 0)
    }

    fun setCartCount(cartNumber: Int) {
        instance!!.sharedPManger!!.SetData(Constants.KEY_CART_SIZE, cartNumber)
    }

    fun getFastQCartCount(): Int {
        return instance!!.sharedPManger!!.getDataInt(Constants.KEY_CART_FASTQ_SIZE, 0)
    }

    fun setFastQCartCount(cartNumber: Int) {
        instance!!.sharedPManger!!.SetData(Constants.KEY_CART_FASTQ_SIZE, cartNumber)
    }

    fun getFastQCartTotal(): Float {
        return instance!!.sharedPManger!!.getDataFloat(Constants.KEY_CART_FASTQ_Total)
    }

    fun setFastQCartTotal(total: Float) {
        instance!!.sharedPManger!!.SetData(Constants.KEY_CART_FASTQ_Total, total)
    }

    fun getContinuousScan(): Boolean {
        return instance!!.sharedPManger!!.getDataBool(Constants.KEY_SCAN_AGAIN, false)
    }

    fun setContinuousScan(scanAgain: Boolean) {
        instance!!.sharedPManger!!.SetData(Constants.KEY_SCAN_AGAIN, scanAgain)
    }

    fun getUrl(): String? {
//        var url = ""
        return instance!!.sharedPManger?.getDataString(Constants.KEY_URL, GlobalData.BetaBaseAmourURL)
    }

    fun setUrl(url: String?) {
        instance!!.sharedPManger?.SetData(Constants.KEY_URL, url)
    }

    fun getMainAltUrl(isAlt: Boolean): String? {

        val currUrl: String =  GlobalData.BetaBaseAmourURL
        return currUrl
    }

    fun getScanSound(): Boolean {
        return instance!!.sharedPManger?.getDataBool(Constants.KEY_SCAN_SOUND, false) ?: false
    }

    fun setScanSound(scanSound: Boolean) {
        instance!!.sharedPManger!!.SetData(Constants.KEY_SCAN_SOUND, scanSound)
    }

    fun updateCart(type: Int, cartListSize: Int) {
        var cartNumber = getCartCount()
        if (type == 1) {
            // add
            cartNumber += 1
            setCartCount(cartNumber)
        } else if (type == 2) {
            // delete
            cartNumber -= 1
            setCartCount(cartNumber)
            if (cartListSize == 0) {
                Log.i("tag", "Log cartListSize$cartListSize")
                setCartCount(0)
            }
        }
        EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_UPDATE_CART))
    }

    fun getSliders(): MutableList<Slider?>? {
        val dataString = instance!!.sharedPManger!!.getDataString(Constants.KEY_SLIDER)
        return Gson().fromJson(dataString, object : TypeToken<MutableList<Slider?>?>() {}.type)
    }

    fun setSliderData(sliderData: MutableList<Slider?>?) {
        val userData = Gson().toJson(sliderData)
        instance!!.sharedPManger!!.SetData(Constants.KEY_SLIDER, userData)
    }

    fun getBanners(): MutableList<Slider?>? {
        val dataString = instance!!.sharedPManger!!.getDataString(Constants.KEY_BANNER)
        return Gson().fromJson(dataString, object : TypeToken<MutableList<Slider?>?>() {}.type)
    }

    fun setBannerData(bannerData: MutableList<Slider?>?) {
        val userData = Gson().toJson(bannerData)
        instance!!.sharedPManger!!.SetData(Constants.KEY_BANNER, userData)
    }

    fun getDinners(): MutableList<DinnerModel?>? {
        val dataString = instance!!.sharedPManger!!.getDataString(Constants.KEY_DINNERS)
        return Gson().fromJson(dataString, object : TypeToken<MutableList<DinnerModel?>?>() {}.type)
    }

    fun setDinnersData(bannerData: MutableList<DinnerModel?>?) {
        val userData = Gson().toJson(bannerData)
        instance!!.sharedPManger!!.SetData(Constants.KEY_DINNERS, userData)
    }
}
