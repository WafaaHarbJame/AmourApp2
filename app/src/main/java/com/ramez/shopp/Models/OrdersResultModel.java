package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OrdersResultModel {

    @SerializedName("data")
    @Expose
    private List<OrderProductModel> data = null;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("order_id")
    @Expose
    private Integer order_id;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public List<OrderProductModel> getData() {
        return data;
    }

    public void setData(List<OrderProductModel> data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}