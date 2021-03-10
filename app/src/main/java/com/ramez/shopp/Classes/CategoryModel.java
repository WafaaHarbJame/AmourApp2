package com.ramez.shopp.Classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ramez.shopp.Models.ChildCat;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryModel implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("h_name")
    @Expose
    private String hName;
    @SerializedName("image")
    @Expose
    private String image = "http";

    @SerializedName("image2")
    @Expose
    private String image2 = "http";

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("parent_id")
    @Expose
    private Object parentId;
    @SerializedName("child_cat")
    @Expose
    private ArrayList<ChildCat> childCat = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHName() {
        return hName;
    }

    public void setHName(String hName) {
        this.hName = hName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getParentId() {
        return parentId;
    }

    public void setParentId(Object parentId) {
        this.parentId = parentId;
    }

    public ArrayList<ChildCat> getChildCat() {
        return childCat;
    }

    public void setChildCat(ArrayList<ChildCat> childCat) {
        this.childCat = childCat;
    }

    public String getCatImage() {
        if (UtilityApp.getLanguage().equals(Constants.English)) {
            return image2;

        } else {
            return image;
        }
    }

    public String gethName() {
        return hName;
    }

    public void sethName(String hName) {
        this.hName = hName;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }
}
