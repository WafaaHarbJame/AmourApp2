package com.ramez.shopp.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SoicalLink {
    @SerializedName("whatsapp_link")
    @Expose
    private String whatsappLink;
    @SerializedName("facebook_link")
    @Expose
    private String facebookLink;
    @SerializedName("help_link")
    @Expose
    private String helpLink;
    @SerializedName("twitter_link")
    @Expose
    private String twitterLink;
    @SerializedName("instagram_link")
    @Expose
    private String instagramLink;

    public String getWhatsappLink() {
        return whatsappLink;
    }

    public void setWhatsappLink(String whatsappLink) {
        this.whatsappLink = whatsappLink;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    public String getHelpLink() {
        return helpLink;
    }

    public void setHelpLink(String helpLink) {
        this.helpLink = helpLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getInstagramLink() {
        return instagramLink;
    }

    public void setInstagramLink(String instagramLink) {
        this.instagramLink = instagramLink;
    }
}
