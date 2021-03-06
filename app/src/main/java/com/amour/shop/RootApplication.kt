package com.amour.shop

import  android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.androidnetworking.AndroidNetworking
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.onesignal.OneSignal
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.classes.Constants
import com.amour.shop.classes.ExampleNotificationOpenedHandler
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.MemberModel
import com.amour.shop.Utils.LocaleUtils
import com.amour.shop.Utils.SharedPManger
import java.util.*


class RootApplication : Application() {
    var sharedPManger: SharedPManger? = null

    companion object {
        @get:Synchronized
        var instance: RootApplication? = null
            private set

        //        private const val ONESIGNAL_APP_ID = "06c038db-2891-4e93-b03f-ac3a308efc8e"
        var firebaseAnalytics: FirebaseAnalytics? = null

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        sharedPManger = SharedPManger(this)
        FirebaseApp.initializeApp(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Places.initialize(applicationContext, this.getString(R.string.mapKey), Locale.US)

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
//        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.setAppId(getString(R.string.ONESIGNAL_APP_ID));
        OneSignal.setNotificationOpenedHandler(ExampleNotificationOpenedHandler(this))

        val UUID = OneSignal.getDeviceState()?.userId
        val UUID1 = OneSignal.getDeviceState()?.pushToken

        if (UUID != null) {
            UtilityApp.setFCMToken(UUID)
        }
        Log.d("debug", "Log token one signal   :$UUID")
        Log.d("debug", "Log token one signal2   :$UUID1")


        if ((UtilityApp.getUserData()?.id ?: 0) > 0) {
            val updateToken = OneSignal.getDeviceState()?.userId
            val memberModel = UtilityApp.getUserData()
            memberModel?.deviceToken = updateToken
            UtilityApp.setUserData(memberModel)
            updateToken(memberModel)
        }
        var appLanguage = UtilityApp.getLanguage()
        if (appLanguage == null) {
            appLanguage = Constants.English
        }
        UtilityApp.setLanguage(appLanguage)
        LocaleUtils.setLocale(Locale(appLanguage))
        LocaleUtils.updateConfig(instance, instance!!.resources.configuration)
        AndroidNetworking.initialize(applicationContext)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleUtils.updateConfig(instance, newConfig)
    }

    private fun updateToken(memberModel: MemberModel?) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    Log.i("TAG", "Log  UpdateToken Success ")
                }
            }

        }).updateTokenHandle(memberModel)
    }
}

