package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryDetailsModel {
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("shortname")
    @Expose
    public String shortname;
    @SerializedName("phonecode")
    @Expose
    public int phonecode;
    @SerializedName("currency_code")
    @Expose
    public String currencyCode;
    @SerializedName("fractional")
    @Expose
    public int fractional;
    @SerializedName("mobileLength")
    @Expose
    public int mobileLength;
    @SerializedName("hasLoyal")
    @Expose
    public boolean hasLoyal;;

}
