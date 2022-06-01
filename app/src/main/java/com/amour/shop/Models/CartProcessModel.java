package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CartProcessModel {
    @SerializedName("productCartQty")
    @Expose
    private Integer productCartQty;
    @SerializedName("cart_count")
    @Expose
    private int cartCount;

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    private Double total;
    private Double totalSavePrice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public Double getTotalSavePrice() {
        return totalSavePrice;
    }

    public void setTotalSavePrice(Double totalSavePrice) {
        this.totalSavePrice = totalSavePrice;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getProductCartQty() {
        return productCartQty;
    }

    public void setProductCartQty(Integer productCartQty) {
        this.productCartQty = productCartQty;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(Integer cartCount) {
        this.cartCount = cartCount;
    }

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


}
