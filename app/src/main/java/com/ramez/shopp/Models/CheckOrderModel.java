package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CheckOrderModel implements Serializable {
    @SerializedName("user_address")
    @Expose
    private UserDefaultAddress userAddress;
    @SerializedName("delivery_times")
    @Expose
    private List<DeliveryTime> deliveryTimes = null;
    @SerializedName("minimum_order_amount")
    @Expose
    private int minimumOrderAmount;
    @SerializedName("delivery_charges")
    @Expose
    private double deliveryCharges;
    @SerializedName("is_delivery_time")
    @Expose
    private String isDeliveryTime;
    @SerializedName("total_amount")
    @Expose
    private double totalAmount;
    @SerializedName("cost")
    @Expose
    private double cost;
    @SerializedName("express_delivery_charges")
    @Expose
    private double expressDeliveryCharges;

    public UserDefaultAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(UserDefaultAddress userAddress) {
        this.userAddress = userAddress;
    }

    public List<DeliveryTime> getDeliveryTimes() {
        return deliveryTimes;
    }

    public void setDeliveryTimes(List<DeliveryTime> deliveryTimes) {
        this.deliveryTimes = deliveryTimes;
    }

    public int getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(int minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public double getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(double deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getIsDeliveryTime() {
        return isDeliveryTime;
    }

    public void setIsDeliveryTime(String isDeliveryTime) {
        this.isDeliveryTime = isDeliveryTime;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getExpressDeliveryCharges() {
        return expressDeliveryCharges;
    }

    public void setExpressDeliveryCharges(double expressDeliveryCharges) {
        this.expressDeliveryCharges = expressDeliveryCharges;
    }
}
