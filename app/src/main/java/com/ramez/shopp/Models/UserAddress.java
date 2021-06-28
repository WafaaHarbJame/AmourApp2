package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserAddress implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("address_type")
    @Expose
    private String addressType;
    @SerializedName("default_address")
    @Expose
    private Integer defaultAddress;
    @SerializedName("full_address")
    @Expose
    private String fullAddress;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public Integer getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(Integer defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

}
