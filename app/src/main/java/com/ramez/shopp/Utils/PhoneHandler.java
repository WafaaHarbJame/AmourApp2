package com.ramez.shopp.Utils;

import android.text.TextUtils;
import android.util.Patterns;

import com.ramez.shopp.RootApplication;

import java.util.List;
import java.util.Set;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * Created by wokman on 11/11/2016.
 */

public class PhoneHandler {

    public static boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }

    public static boolean validateUsing_libphonenumber(String countryCode, String phNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(RootApplication.Companion.getInstance());
        String isoCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            //phoneNumber = phoneNumberUtil.parse(phNumber, "IN");  //if you want to pass region code
//            System.out.println("Log isoCode " + isoCode);
            phoneNumber = phoneNumberUtil.parse(phNumber, isoCode);
        } catch (NumberParseException e) {
            System.err.println(e);
        }

        boolean isValid = phoneNumberUtil.isValidNumber(phoneNumber);
        return isValid;
//        if (isValid) {
////            String internationalFormat = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
////            Toast.makeText(this, "Phone Number is Valid " + internationalFormat, Toast.LENGTH_SHORT).show();
//            return true;
//        } else {
////            Toast.makeText(this, "Phone Number is Invalid " + phoneNumber, Toast.LENGTH_SHORT).show();
//            return false;
//        }
    }

    public static List<String> getRegionCodesForCountryCode(String countryCode) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(RootApplication.Companion.getInstance());
        List<String> regionCodes = phoneNumberUtil.getRegionCodesForCountryCode(Integer.parseInt(countryCode));
        return regionCodes;
    }

    public static void getSupportedCallingCodes(String countryCode) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.createInstance(RootApplication.Companion.getInstance());
//        Set<Integer> callingCodes = phoneNumberUtil.getSupportedCallingCodes();
        String regionCode = phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
//        System.out.println("Log callingCodes " + callingCodes);
        System.out.println("Log regionCode " + regionCode);
        System.out.println("Log getExampleNumber " + phoneNumberUtil.getNddPrefixForRegion(regionCode,false));
//        return regionCodes;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


}
