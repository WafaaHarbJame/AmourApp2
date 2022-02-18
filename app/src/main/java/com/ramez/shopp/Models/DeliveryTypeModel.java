package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ramez.shopp.classes.UtilityApp;

public class DeliveryTypeModel {
    private int id;
    private String methodEn;
    private String methodAr;
    private int image;

    public DeliveryTypeModel(int id, String methodEn, String methodAr, int image) {
        this.id = id;
        this.methodEn = methodEn;
        this.methodAr = methodAr;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
    public String getName() {

        return UtilityApp.isEnglish()? methodEn : methodAr;
    }
}
