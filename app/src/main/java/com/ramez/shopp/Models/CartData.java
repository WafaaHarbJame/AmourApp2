package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CartData {
    @SerializedName("cart_data")
    @Expose
    private ArrayList<CartModel> cartData = null;
    @SerializedName("user_address")
    @Expose
    private UserAddress userAddress;

    public ArrayList<CartModel> getCartData() {
        return cartData;
    }

    public void setCartData(ArrayList<CartModel> cartData) {
        this.cartData = cartData;
    }

    public UserAddress getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(UserAddress userAddress) {
        this.userAddress = userAddress;
    }
}
