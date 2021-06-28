package com.ramez.shopp.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryInfo {
    @SerializedName("is_delivery_time")
    @Expose
    public  String isDeliveryTime;
    @SerializedName("delivery_charges")
    @Expose
    public double deliveryCharges;
    @SerializedName("minimum_order_amount")
    @Expose
    public int minimumOrderAmount;
    @SerializedName("express_delivery_charges")
    @Expose
    public int expressDeliveryCharges;
}
