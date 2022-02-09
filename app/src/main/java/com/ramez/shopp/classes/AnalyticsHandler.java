package com.ramez.shopp.classes;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ramez.shopp.RootApplication;

public class AnalyticsHandler {

    public static void AddToCart(int id, String currency, double quantity) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEMS, String.valueOf(id));
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, quantity);
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle);
    }


    public static void ViewCart(int id, String currency, double quantity) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEMS, String.valueOf(id));
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, quantity);
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_CART, bundle);

    }    public static void APP_OPEN() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "open");
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

    }

    public static void ViewItem(int id, String currency, double quantity) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEMS, String.valueOf(id));
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, quantity);
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

    }

    public static void RemoveFromCart(int id, String currency, double quantity) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEMS, String.valueOf(id));
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, quantity);
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.REMOVE_FROM_CART, bundle);
    }

    public static void PurchaseEvent(String couponCodeId, String currency, int paymentMethodId, Double deliveryFees, String orderId, String total) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.AFFILIATION, "affiliation");
        bundle.putString(FirebaseAnalytics.Param.COUPON, couponCodeId);
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putDouble(FirebaseAnalytics.Param.SHIPPING, paymentMethodId);
        bundle.putDouble(FirebaseAnalytics.Param.TAX, deliveryFees);
        bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, orderId);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, Double.parseDouble(total));
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.PURCHASE, bundle);
    }

    public static void checkOut(String couponCodeId, String currency, String ITEMS, String total) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.COUPON, couponCodeId);
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putString(FirebaseAnalytics.Param.ITEMS, ITEMS);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, Double.parseDouble(total));
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle);
    }


    public static void AddToWishList(int id, String currency, double quantity) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEMS, String.valueOf(id));
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, quantity);
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, bundle);
    }



    public static void addShippingInfo(String couponCodeId, String currency, String ITEMS, String total) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.COUPON, couponCodeId);
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, currency);
        bundle.putString(FirebaseAnalytics.Param.ITEMS, ITEMS);
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, Double.parseDouble(total));
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.ADD_SHIPPING_INFO, bundle);
    }

    public static void searchEvent(String searchText) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchText);
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
    }

    public static void ViewSearchResult(String searchText) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchText);
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS, bundle);
    }


    public static void selectItem(String item) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEMS, item);
        bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_ID, item);
        bundle.putString(FirebaseAnalytics.Param.ITEM_LIST_NAME, item);
        RootApplication.Companion.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle);
    }
}
