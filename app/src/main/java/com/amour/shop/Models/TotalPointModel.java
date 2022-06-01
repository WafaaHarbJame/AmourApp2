package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TotalPointModel {

    @SerializedName("points")
    @Expose
    public double points;
    @SerializedName("value")
    @Expose
    public double value;

}
