package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ramez.shopp.classes.UtilityApp;

public class DeliveryTypeModel {
    private int id;
    private String methodName;
    private int UnselectedImage;
    private int selectedImage;
    private String deliveryTime;
    private Double deliveryPrice;

    public DeliveryTypeModel(int id, String methodName, int unselectedImage, int selectedImage, String deliveryTime, Double deliveryPrice) {
        this.id = id;
        this.methodName = methodName;
        UnselectedImage = unselectedImage;
        this.selectedImage = selectedImage;
        this.deliveryTime = deliveryTime;
        this.deliveryPrice = deliveryPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getUnselectedImage() {
        return UnselectedImage;
    }

    public void setUnselectedImage(int unselectedImage) {
        UnselectedImage = unselectedImage;
    }

    public int getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(int selectedImage) {
        this.selectedImage = selectedImage;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }
}
