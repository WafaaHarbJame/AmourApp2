package com.amour.shop.Models;

import com.google.android.material.slider.Slider;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MainModelResult {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("fav_count")
    @Expose
    private Integer favCount;
    @SerializedName("cart_count")
    @Expose
    private Integer cartCount;
    @SerializedName("delivery_charges")
    @Expose
    private Integer deliveryCharges;
    @SerializedName("minimum_order_amount")
    @Expose
    private Integer minimumOrderAmount;
    @SerializedName("data")
    @Expose
    private List<Object> data = null;
    @SerializedName("sliders")
    @Expose
    private List<Slider> sliders = null;
    @SerializedName("featured")
    @Expose
    private List<Featured> featured = null;
    @SerializedName("quick_products")
    @Expose
    private List<QuickProduct> quickProducts = null;
    @SerializedName("offered_products")
    @Expose
    private List<OfferedProduct> offeredProducts = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getFavCount() {
        return favCount;
    }

    public void setFavCount(Integer favCount) {
        this.favCount = favCount;
    }

    public Integer getCartCount() {
        return cartCount;
    }

    public void setCartCount(Integer cartCount) {
        this.cartCount = cartCount;
    }

    public Integer getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(Integer deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public Integer getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(Integer minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public List<Slider> getSliders() {
        return sliders;
    }

    public void setSliders(List<Slider> sliders) {
        this.sliders = sliders;
    }

    public List<Featured> getFeatured() {
        return featured;
    }

    public void setFeatured(List<Featured> featured) {
        this.featured = featured;
    }

    public List<QuickProduct> getQuickProducts() {
        return quickProducts;
    }

    public void setQuickProducts(List<QuickProduct> quickProducts) {
        this.quickProducts = quickProducts;
    }

    public List<OfferedProduct> getOfferedProducts() {
        return offeredProducts;
    }

    public void setOfferedProducts(List<OfferedProduct> offeredProducts) {
        this.offeredProducts = offeredProducts;
    }


}
