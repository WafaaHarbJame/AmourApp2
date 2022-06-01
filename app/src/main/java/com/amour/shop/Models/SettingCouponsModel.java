package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SettingCouponsModel {
    @SerializedName("minimumPoints")
    @Expose
    public int minimumPoints;
    @SerializedName("value")
    @Expose
    public double value;


}
