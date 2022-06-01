package com.amour.shop.Models;

import com.amour.shop.classes.UtilityApp;

public class CurrencyModel {
    private int id;
    private String name_ar;
    private String name_en;

    public CurrencyModel(int id, String name_ar, String name_en) {
        this.id = id;
        this.name_ar = name_ar;
        this.name_en = name_en;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName_ar() {
        return name_ar;
    }

    public void setName_ar(String name_ar) {
        this.name_ar = name_ar;
    }

    public String getName_en() {
        return name_en;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }

    public String getName() {

        return UtilityApp.INSTANCE.isEnglish()? name_en : name_ar;
    }
}
