package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OrdersResultModel {

    @SerializedName("order_id")
    @Expose
    private int orderId;
    @SerializedName("amount")
    @Expose
    private int amount;
    @SerializedName("cart_count")
    @Expose
    private int cartCount;
    @SerializedName("fav_count")
    @Expose
    private int favCount;
    @SerializedName("order_code")
    @Expose
    private String orderCode;
    @SerializedName("paymentResp")
    @Expose
    private PaymentResp paymentResp;
    @SerializedName("data")
    @Expose
    private Object data;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("trackid")
    @Expose
    private String trackid;

    @SerializedName("creditCradUrl")
    @Expose
    private String creditCradUrl;
    @SerializedName("session_id")
    @Expose
    private String session_id;

    public String getCreditCradUrl() {
        return creditCradUrl;
    }

    public void setCreditCradUrl(String creditCradUrl) {
        this.creditCradUrl = creditCradUrl;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getTrackid() {
        return trackid;
    }

    public void setTrackid(String trackid) {
        this.trackid = trackid;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCartCount() {
        return cartCount;
    }

    public void setCartCount(int cartCount) {
        this.cartCount = cartCount;
    }

    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public PaymentResp getPaymentResp() {
        return paymentResp;
    }

    public void setPaymentResp(PaymentResp paymentResp) {
        this.paymentResp = paymentResp;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

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
}