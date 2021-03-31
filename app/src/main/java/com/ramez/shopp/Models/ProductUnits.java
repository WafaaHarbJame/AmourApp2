package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;

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

        return  UtilityApp.getLanguage().equals(Constants.Arabic)? hName :name;
    }


}