package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Slider implements Serializable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("image2")
    @Expose
    private String image2;
    @SerializedName("reffrence")
    @Expose
    private Object reffrence;
    @SerializedName("reffrenceType")
    @Expose
    private int reffrenceType;
    @SerializedName("type")
    @Expose
    private int type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public Object getReffrence() {
        return reffrence;
    }

    public void setReffrence(Object reffrence) {
        this.reffrence = reffrence;
    }

    public int getReffrenceType() {
        return reffrenceType;
    }

    public void setReffrenceType(int reffrenceType) {
        this.reffrenceType = reffrenceType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Slider(int id, String image, String image2, Object reffrence, int reffrenceType, int type) {
        this.id = id;
        this.image = image;
        this.image2 = image2;
        this.reffrence = reffrence;
        this.reffrenceType = reffrenceType;
        this.type = type;
    }
}