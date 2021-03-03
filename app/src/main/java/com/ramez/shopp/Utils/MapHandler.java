package com.ramez.shopp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;


import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by wokman on 4/2/2017.
 */

public class MapHandler {

    public static double[] getGPSData(String gps) {
        double[] data = new double[2];
        if (gps != null) {
            String[] gpsSplit = gps.split(",");
            for (int i = 0; i < gpsSplit.length; i++)
                data[i] = NumberHandler.getDouble(gpsSplit[i]);
        }

        return data;
    }

    public static String getGpsAddress(Context c, double latitude, double longitude) {
        try {

            List<Address> addresses;
            String lang = UtilityApp.getLanguage() != null ? UtilityApp.getLanguage() : Constants.English;
            Geocoder geocoder = new Geocoder(c, new Locale(lang));

            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address;
            Address allAddress = addresses.get(0);
//            if (addresses.get(0) != null)
            Log.i("MapHandler","Log getAdminArea "+allAddress.getAdminArea());
            Log.i("MapHandler","Log getSubAdminArea "+allAddress.getSubAdminArea());
            Log.i("MapHandler","Log getCountryName "+allAddress.getCountryName());
            Log.i("MapHandler","Log getFeatureName "+allAddress.getFeatureName());
            Log.i("MapHandler","Log getLocality "+allAddress.getLocality());
            Log.i("MapHandler","Log getSubLocality "+allAddress.getSubLocality());
            Log.i("MapHandler","Log getPremises "+allAddress.getPremises());
            Log.i("MapHandler","Log getThoroughfare "+allAddress.getThoroughfare());

            address = allAddress.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            else
//                address = c.getResources().getString(R.string.no_address);
            return address;
        } catch (Exception e) {
            e.printStackTrace();
            return c.getResources().getString(R.string.no_address);
        }
//        return c.getResources().getString(R.string.no_address);
    }

    public static String GetMapImage(String size, double latitude, double longitude) {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&size=" + size + "&sensor=false&zoom=17&scale=2&markers=color:red|label:|" + latitude + "," + longitude;
    }

    public static void OpenGoogleMapIntent(Activity activity, String advTitle, double lat, double lng) {

        double latitude = lat;
        double longitude = lng;
        String label = advTitle + "";
        String uriBegin = "geo:" + latitude + "," + longitude;
        String query = latitude + "," + longitude + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        activity.startActivity(intent);

    }

    public static boolean vailAddress(Context context, String latitude, String longitude) {
        boolean isValid;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = "", city = "", state = "", country = "", postalCode = "", knownName = "";
            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName();
            } else {
                isValid = false;
            }


            if (knownName != null) {
                if (!knownName.isEmpty()) {
                    isValid = true;
                    Log.d("MapHandler", "vailAddress: knownName: " + knownName);
                } else {
                    isValid = false;
                    Log.d("MapHandler", "vailAddress: isEmpty");
                }
            } else {
                isValid = false;
                Log.d("MapHandler", "vailAddress: null");
            }

        } catch (IOException e) {
            e.printStackTrace();
            isValid = false;
            Log.d("MapHandler", "vailAddress: IOException");
        }
        return isValid;
    }

    public static String getPhysicalLocation(Context context, String latitude, String longitude) {

        String strAdd = "";
        Locale locale = new Locale(Locale.getDefault().getLanguage());
        Geocoder geocoder = new Geocoder(context, locale);
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    Double.parseDouble(latitude),
                    Double.parseDouble(longitude),
                    1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            } else {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }



}
