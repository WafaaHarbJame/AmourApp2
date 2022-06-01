package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileData {

    @SerializedName("id")
    @Expose
     Integer id;
    @SerializedName("name")
    @Expose
     String name;
    @SerializedName("email")
    @Expose
     String email;

    @SerializedName("loyalBarcode")
    @Expose
    String loyalBarcode;

    public String getLoyalBarcode() {
        return loyalBarcode;
    }

    public void setLoyalBarcode(String loyalBarcode) {
        this.loyalBarcode = loyalBarcode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    @SerializedName("profile_picture")
    @Expose

     String profilePicture;


}
