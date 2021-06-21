package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MemberModel implements Serializable {
    public int lastSelectedAddress;


    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    private String password;

    private String new_password;
    private String userType;
    @SerializedName("device_id")
    @Expose
    private String deviceId;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("city")
    @Expose
    private int  city;
    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("profile_picture")
    @Expose
    private String profilePicture;
    private int  storeId;

    @SerializedName("loyalBarcode")
    @Expose
    private String loyalBarcode;


    public String getLoyalBarcode() {
        return loyalBarcode;
    }

    public void setLoyalBarcode(String loyalBarcode) {
        this.loyalBarcode = loyalBarcode;
    }



//    @SerializedName("registerType")
//    @Expose
//    private String registerType;

    public String getFacebook_key() {
        return facebook_key;
    }

    public void setFacebook_key(String facebook_key) {
        this.facebook_key = facebook_key;
    }

    public String getGoogle_key() {
        return google_key;
    }

    public void setGoogle_key(String google_key) {
        this.google_key = google_key;
    }

    private String facebook_key;
    private String google_key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLastSelectedAddress() {
        return lastSelectedAddress;
    }

    public void setLastSelectedAddress(int lastSelectedAddress) {
        this.lastSelectedAddress = lastSelectedAddress;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
//
//    public String getRegisterType() {
//        return registerType;
//    }
//
//    public void setRegisterType(String registerType) {
//        this.registerType = registerType;
//    }
}


