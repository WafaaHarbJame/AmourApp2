package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuickDeliveryRespond {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("name_ar")
    @Expose
    private String nameAr;
    @SerializedName("minimumOrder")
    @Expose
    private int minimumOrder;
    @SerializedName("hasExpressDelivery")
    @Expose
    private boolean hasExpressDelivery;
    @SerializedName("expressDeliveryCharge")
    @Expose
    private int expressDeliveryCharge;
    @SerializedName("expressDeliverydescription")
    @Expose
    private String expressDeliverydescription;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public int getMinimumOrder() {
        return minimumOrder;
    }

    public void setMinimumOrder(int minimumOrder) {
        this.minimumOrder = minimumOrder;
    }

    public boolean isHasExpressDelivery() {
        return hasExpressDelivery;
    }

    public void setHasExpressDelivery(boolean hasExpressDelivery) {
        this.hasExpressDelivery = hasExpressDelivery;
    }

    public int getExpressDeliveryCharge() {
        return expressDeliveryCharge;
    }

    public void setExpressDeliveryCharge(int expressDeliveryCharge) {
        this.expressDeliveryCharge = expressDeliveryCharge;
    }

    public String getExpressDeliverydescription() {
        return expressDeliverydescription;
    }

    public void setExpressDeliverydescription(String expressDeliverydescription) {
        this.expressDeliverydescription = expressDeliverydescription;
    }


}
