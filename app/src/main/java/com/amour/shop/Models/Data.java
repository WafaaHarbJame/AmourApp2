package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable {

    @SerializedName("total_amount")
    @Expose
    private double totalAmount;
    @SerializedName("minimum_order_amount")
    @Expose
    private int minimumOrderAmount;
    @SerializedName("cart_count")
    @Expose
    private int cartCount;
    @SerializedName("cart_data")
    @Expose
    private ArrayList<CartModel> cartData = null;

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(int minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public ArrayList<CartModel> getCartData() {
        return cartData;
    }

    public void setCartData(ArrayList<CartModel> cartData) {
        this.cartData = cartData;
    }
}