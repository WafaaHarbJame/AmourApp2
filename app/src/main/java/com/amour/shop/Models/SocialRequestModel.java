package com.amour.shop.Models;

/**
 * Created by  on 6/22/2017.
 */

public class SocialRequestModel {
    private String name;
    private String email;
    private String facebook_key;
    private String google_key;
    private int device_platform;
    private String device_token;
    private int country_id;
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFacebookKey() {
        return facebook_key;
    }

    public void setFacebookKey(String facebook_key) {
        this.facebook_key = facebook_key;
    }

    public String getGoogleKey() {
        return google_key;
    }

    public void setGoogleKey(String google_key) {
        this.google_key = google_key;
    }

    public int getDevicePlatform() {
        return device_platform;
    }

    public void setDevicePlatform(int device_platform) {
        this.device_platform = device_platform;
    }

    public String getDeviceToken() {
        return device_token;
    }

    public void setDeviceToken(String device_token) {
        this.device_token = device_token;
    }

    public int getCountryId() {
        return country_id;
    }

    public void setCountryId(String country_code) {
        this.country_id = Integer.parseInt(country_code);
    }

    public void setCountryId(int country_code) {
        this.country_id = country_code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
