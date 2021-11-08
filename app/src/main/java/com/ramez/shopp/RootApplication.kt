package com.ramez.shopp

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.androidnetworking.AndroidNetworking
import com.google.android.libraries.places.api.Places
import com.google.firebase.analytics.FirebaseAnalytics
import com.onesignal.OneSignal
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.ExampleNotificationOpenedHandler
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.MemberModel
import com.ramez.shopp.Models.ResultAPIModel
import com.ramez.shopp.Utils.LocaleUtils
import com.ramez.shopp.Utils.SharedPManger
import java.util.*


class RootApplication : Application() {
    var sharedPManger: SharedPManger? = null

    companion object {
        @get:Synchronized
        var instance: RootApplication? = null
            private set
        private const val ONESIGNAL_APP_ID = "06c038db-2891-4e93-b03f-ac3a308efc8e"
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
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Places.initialize(applicationContext, this.getString(R.string.mapKey), Locale.US)
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.init(this, "", ONESIGNAL_APP_ID)
        OneSignal.startInit(this)
            .setNotificationOpenedHandler(ExampleNotificationOpenedHandler(this))
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .init()
        val UUID = OneSignal.getPermissionSubscriptionState().subscriptionStatus.userId
        if (UUID != null) {
            UtilityApp.setFCMToken(UUID)
        }
        if (UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) {
            val updateToken = OneSignal.getPermissionSubscriptionState().subscriptionStatus.userId
            val memberModel = UtilityApp.getUserData()
            memberModel.deviceToken = updateToken
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

    private fun updateToken(memberModel: MemberModel) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    Log.i("TAG", "Log  UpdateToken Success ")
                }
            }

        }).UpdateTokenHandle(memberModel)
    }
}

