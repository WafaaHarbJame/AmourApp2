package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;

import java.io.Serializable;
import java.util.ArrayList;

public class MostSearchModel implements Serializable {
    @SerializedName("h_name")
    @Expose
    private String hName;
    @SerializedName("name")
    @Expose
    private String name;

    public String gethName() {
        return hName;
    }

    public void sethName(String hName) {
        this.hName = hName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTagName() {
        if (UtilityApp.getLanguage().equals(Constants.English)) {
            return name;

        } else {
            return hName;
        }
    }

    public MostSearchModel(String hName, String name) {
        this.hName = hName;
        this.name = name;
    }
}
