package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainModel implements Serializable {

    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("fav_count")
    @Expose
    private int favCount;
    @SerializedName("cart_count")
    @Expose
    private int cartCount;
    @SerializedName("delivery_charges")
    @Expose
    private int deliveryCharges;
    @SerializedName("minimum_order_amount")
    @Expose
    private int minimumOrderAmount;
    @SerializedName("data")
    @Expose
    private ArrayList<Object> data = null;
    @SerializedName("sliders")
    @Expose
    private ArrayList<Slider> sliders = null;
    @SerializedName("featured")
    @Expose
    private ArrayList<ProductModel> featured = null;
    @SerializedName("quick_products")
    @Expose
    private ArrayList<ProductModel> quickProducts = null;
    @SerializedName("offered_products")
    @Expose
    private ArrayList<ProductModel> offeredProducts = null;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public int getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(int deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public int getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(int minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public ArrayList<Object> getData() {
        return data;
    }

    public void setData(ArrayList<Object> data) {
        this.data = data;
    }

    public ArrayList<Slider> getSliders() {
        return sliders;
    }

    public void setSliders(ArrayList<Slider> sliders) {
        this.sliders = sliders;
    }

    public ArrayList<ProductModel> getFeatured() {
        return featured;
    }

    public void setFeatured(ArrayList<ProductModel> featured) {
        this.featured = featured;
    }

    public ArrayList<ProductModel> getQuickProducts() {
        return quickProducts;
    }

    public void setQuickProducts(ArrayList<ProductModel> quickProducts) {
        this.quickProducts = quickProducts;
    }

    public ArrayList<ProductModel> getOfferedProducts() {
        return offeredProducts;
    }

    public void setOfferedProducts(ArrayList<ProductModel> offeredProducts) {
        this.offeredProducts = offeredProducts;
    }
}

