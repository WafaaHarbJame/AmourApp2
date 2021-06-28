package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDefaultAddress implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("full_address")
    @Expose
    private String fullAddress;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }
}
