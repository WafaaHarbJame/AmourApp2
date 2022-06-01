package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoModel {

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("attatchmentable_id")
    @Expose
    public String attatchmentableId;
    @SerializedName("attatchmentable_type")
    @Expose
    public String attatchmentableType;
    @SerializedName("image")
    @Expose
    public String image;

}
