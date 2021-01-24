package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ramez.shopp.Classes.UtilityApp;

public class PaymentModel {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("shortname")
    @Expose
    private String shortname;
    @SerializedName("method_en")
    @Expose
    private String methodEn;
    @SerializedName("method_ar")
    @Expose
    private String methodAr;

    @SerializedName("image")
    @Expose
    private int image;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getMethodEn() {
        return methodEn;
    }

    public void setMethodEn(String methodEn) {
        this.methodEn = methodEn;
    }

    public String getMethodAr() {
        return methodAr;
    }

    public void setMethodAr(String methodAr) {
        this.methodAr = methodAr;
    }

    public String getName() {

        return UtilityApp.isEnglish()? methodEn : methodAr;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
