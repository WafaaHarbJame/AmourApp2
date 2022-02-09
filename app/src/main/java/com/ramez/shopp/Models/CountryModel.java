package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.classes.UtilityApp;

import java.io.Serializable;



public class CountryModel implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("shortname")
    @Expose
    private String shortname;
    @SerializedName("phonecode")
    @Expose
    private Integer phonecode;
    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("fractional")
    @Expose
    private Integer fractional;
    @SerializedName("flag")
    @Expose
    private int flag;
    @SerializedName("countryNameAr")
    @Expose
    private String countryNameAr;

    public String getCountryNameEn() {
        return countryNameEn;
    }

    public void setCountryNameEn(String countryNameEn) {
        this.countryNameEn = countryNameEn;
    }

    @SerializedName("countryNameEn")
    @Expose
    private String countryNameEn;
    public String getCountryNameAr() {
        return countryNameAr;
    }

    public void setCountryNameAr(String countryNameAr) {
        this.countryNameAr = countryNameAr;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getFractional() {
        return fractional;
    }

    public void setFractional(Integer fractional) {
        this.fractional = fractional;
    }

    public CountryModel(Integer id,String countryNameAr, String countryNameEn, String shortname, Integer phonecode, String currencyCode, Integer fractional, int flag) {

        this.id = id;
        this.shortname = shortname;
        this.phonecode = phonecode;
        this.currencyCode = currencyCode;
        this.fractional = fractional;
        this.flag = flag;
        this.countryNameAr = countryNameAr;
        this.countryNameEn = countryNameEn;
    }



    public String getCountryName() {
        if (UtilityApp.getLanguage().equals(Constants.English)) {
            return countryNameEn;

        } else {
            return countryNameAr;
        }
    }
}






