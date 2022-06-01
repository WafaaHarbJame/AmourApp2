package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegisterResultModel {

    @SerializedName("cart_count")
    @Expose
    private int cartCount;
    @SerializedName("fav_count")
    @Expose
    private int favCount;
    @SerializedName("otp")
    @Expose
    private String otp;
    @SerializedName("data")
    @Expose
    private MemberModel data;
    @SerializedName("user_address")
    @Expose
    private String userAddress;
    @SerializedName("refer_message")
    @Expose
    private String referMessage;
    @SerializedName("status")
    @Expose
    private int status;
    @SerializedName("message")
    @Expose
    private String message;

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

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public MemberModel getData() {
        return data;
    }

    public void setData(MemberModel data) {
        this.data = data;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getReferMessage() {
        return referMessage;
    }

    public void setReferMessage(String referMessage) {
        this.referMessage = referMessage;
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



