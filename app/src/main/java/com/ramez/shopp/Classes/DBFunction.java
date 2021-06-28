package com.ramez.shopp.Classes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ramez.shopp.Models.CountryDetailsModel;
import com.ramez.shopp.Models.SettingCouponsModel;
import com.ramez.shopp.Models.TotalPointModel;

public class DBFunction {
//    static Realm realm = Realm.getDefaultInstance();


    public static TotalPointModel getTotalPoints() {

        String json = getFromDB(Constants.DB_TOTAL_POINTS);
        if (json != null) {
            return new Gson().fromJson(json, new TypeToken<TotalPointModel>() {
            }.getType());
        } else {
            return null;
        }
    }



    public static CountryDetailsModel getLoyal() {

        String json = getFromDB(Constants.DB_loyal);
        if (json != null) {
            return new Gson().fromJson(json, new TypeToken<CountryDetailsModel>() {
            }.getType());
        } else {
            return null;
        }
    }

    public static SettingCouponsModel getCouponSettings() {

        String json = getFromDB(Constants.DB_COUPON_SETTINGS);
        if (json != null) {
            return new Gson().fromJson(json, new TypeToken<SettingCouponsModel>() {
            }.getType());
        } else {
            return null;
        }
    }

    /*************************************************************/

    public static void setTotalPoints(TotalPointModel model) {

        String json = new Gson().toJson(model);

        setDB(Constants.DB_TOTAL_POINTS, json);

    }


    public static void setLoyal(CountryDetailsModel model) {

        String json = new Gson().toJson(model);

        setDB(Constants.DB_loyal, json);

    }

    public static void setCouponSettings(SettingCouponsModel model) {

        String json = new Gson().toJson(model);

        setDB(Constants.DB_COUPON_SETTINGS, json);

    }


    /**********************************************************************/
    private static String getFromDB(String type) {
        return UtilityApp.getFromShPref(type);
//        DBModel dbModel = realm.where(DBModel.class).equalTo("type", type).findFirst();
//        if (dbModel != null) {
//            return dbModel.dataModel;
//        } else {
//            return null;
//        }
    }


    private static void setDB(String type, String data) {

        UtilityApp.setToShPref(type, data);

    }

}
