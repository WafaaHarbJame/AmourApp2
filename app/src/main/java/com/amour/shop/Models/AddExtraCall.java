package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddExtraCall {
    @SerializedName("qty")
    @Expose
    public int qty;
    @SerializedName("barcode")
    @Expose
    public String barcode;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("user_id")
    @Expose
    public int userId;
    @SerializedName("store_id")
    @Expose
    public int storeId;

}
