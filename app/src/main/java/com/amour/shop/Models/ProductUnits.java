package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.amour.shop.classes.Constants;
import com.amour.shop.classes.UtilityApp;

import java.io.Serializable;

public class ProductUnits implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("h_name")
    @Expose
    private String hName;

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

    public String getHName() {
        return hName;
    }

    public void setHName(String hName) {
        this.hName = hName;
    }

    public String getUnitName() {

        return  UtilityApp.INSTANCE.getLanguage().equals(Constants.Arabic)? hName :name;
    }


}