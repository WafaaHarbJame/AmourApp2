package com.ramez.shopp.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CategoryResultModel {

    @SerializedName("data")
    @Expose
    private ArrayList<CategoryModel> data = null;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;

    public ArrayList<CategoryModel> getData() {
        return data;
    }

    public void setData(ArrayList<CategoryModel> data) {
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
