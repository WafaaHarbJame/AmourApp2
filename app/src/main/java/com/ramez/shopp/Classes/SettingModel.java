package com.ramez.shopp.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SettingModel {
    @SerializedName("about")
    @Expose
    private String about="";
    @SerializedName("privacy")
    @Expose
    private String privacy="";
    @SerializedName("conditions")
    @Expose
    private String conditions="";

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

}
