package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderNewModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("order_code")
    @Expose
    private String orderCode;
    @SerializedName("store_id")
    @Expose
    private Integer storeId;
    @SerializedName("delivery_date")
    @Expose
    private String deliveryDate;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("delivery_status")
    @Expose
    private String deliveryStatus;
    @SerializedName("store_name")
    @Expose
    private String storeName;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Integer getStoreId() {
        return storeId;
    }

    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

}
