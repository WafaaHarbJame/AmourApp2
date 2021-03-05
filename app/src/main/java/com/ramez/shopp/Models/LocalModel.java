package com.ramez.shopp.Models;


import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;

public class LocalModel {

    private Integer countryId;
    private String countryName;
    private String countryNameAr;
    private String shortname="BH";
    private Integer phonecode;
    private String currencyCode;
    private String cityId;
    private int fractional;
    private int minimum_order_amount=0;
    private String countryNameEn;


    public String getCountryNameAr() {
        return countryNameAr;
    }

    public String getCountryNameEn() {
        return countryNameEn;
    }

    public void setCountryNameEn(String countryNameEn) {
        this.countryNameEn = countryNameEn;
    }

    public void setCountryNameAr(String countryNameAr) {
        this.countryNameAr = countryNameAr;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public Integer getPhonecode() {
        return phonecode;
    }

    public void setPhonecode(Integer phonecode) {
        this.phonecode = phonecode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public int getFractional() {
        return fractional;
    }

    public void setFractional(int fractional) {
        this.fractional = fractional;
    }

    public int getMinimum_order_amount() {
        return minimum_order_amount;
    }

    public void setMinimum_order_amount(int minimum_order_amount) {
        this.minimum_order_amount = minimum_order_amount;
    }

    public String getCountryName() {
        if (UtilityApp.getLanguage().equals(Constants.English)) {
            return countryNameEn;

        } else {
            return countryNameAr;
        }
    }
}