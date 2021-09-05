package com.ramez.shopp;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.androidnetworking.AndroidNetworking;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.ExampleNotificationOpenedHandler;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Utils.LocaleUtils;
import com.ramez.shopp.Utils.SharedPManger;

import java.util.Locale;


public class RootApplication extends Application {

    private static RootApplication rootApplication;
    private static final String ONESIGNAL_APP_ID = "06c038db-2891-4e93-b03f-ac3a308efc8e";
    public   static  FirebaseAnalytics firebaseAnalytics;

    SharedPManger sharedPManger;


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized RootApplication getInstance() {
        return rootApplication;
    }

    public SharedPManger getSharedPManger() {
        return sharedPManger;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        rootApplication = this;
        sharedPManger = new SharedPManger(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Fresco.initialize(this);

        Places.initialize(getApplicationContext(), this.getString(R.string.mapKey), Locale.US);

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        OneSignal.init(this,"",ONESIGNAL_APP_ID);

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler(this))
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();
        String UUID = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
        if (UUID != null) {
            UtilityApp.setFCMToken(UUID);

        }


        if (UtilityApp.getUserData() != null && UtilityApp.getUserData().getId() != null) {
            String updateToken = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
            MemberModel memberModel = UtilityApp.getUserData();
            memberModel.setDeviceToken(updateToken);
            UtilityApp.setUserData(memberModel);
            UpdateToken(memberModel);
        }



        String appLanguage = UtilityApp.getLanguage();
        if (appLanguage == null) {
            appLanguage = Constants.English;

        }
        UtilityApp.setLanguage(appLanguage);
        LocaleUtils.setLocale(new Locale(appLanguage));
        LocaleUtils.updateConfig(rootApplication, rootApplication.getResources().getConfiguration());

        AndroidNetworking.initialize(getApplicationContext());


    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.updateConfig(rootApplication, newConfig);
    }

    private void UpdateToken(MemberModel memberModel) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<String> result = (ResultAPIModel) obj;
            if (IsSuccess) {
                Log.i("TAG", "Log  UpdateToken Success ");


            }


        }).UpdateTokenHandle(memberModel);

    }

}

