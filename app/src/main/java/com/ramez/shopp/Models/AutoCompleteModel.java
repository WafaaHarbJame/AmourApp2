package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.classes.UtilityApp;

public class AutoCompleteModel {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("h_name")
    @Expose
    private String hName;
    @SerializedName("get_product_variations")
    @Expose
    private Object getProductVariations;
    @SerializedName("product_brand")
    @Expose
    private Object productBrand;

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

    public Object getGetProductVariations() {
        return getProductVariations;
    }

    public void setGetProductVariations(Object getProductVariations) {
        this.getProductVariations = getProductVariations;
    }

    public Object getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(Object productBrand) {
        this.productBrand = productBrand;
    }

    public String getDataName(){
        if(UtilityApp.getLanguage().equals(Constants.English)){
            return name;

        }
        else {
            return hName;
        }
    }
}
