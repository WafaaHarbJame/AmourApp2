package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.amour.shop.classes.Constants;
import com.amour.shop.classes.UtilityApp;

import java.io.Serializable;
import java.util.ArrayList;

public class KindCategoryModel implements Serializable {
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("h_name")
    @Expose
    private String hName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("parent_id")
    @Expose
    private Integer parentId;
    @SerializedName("child_cat")
    @Expose
    private ArrayList<KindCategoryModel> childCat = null;
    @SerializedName("image2")
    @Expose
    private String image2;
    @SerializedName("image3")
    @Expose
    private Object image3;
    @SerializedName("image4")
    @Expose
    private Object image4;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String gethName() {
        return hName;
    }

    public void sethName(String hName) {
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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public ArrayList<KindCategoryModel> getChildCat() {
        return childCat;
    }

    public void setChildCat(ArrayList<KindCategoryModel> childCat) {
        this.childCat = childCat;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public Object getImage3() {
        return image3;
    }

    public void setImage3(Object image3) {
        this.image3 = image3;
    }

    public Object getImage4() {
        return image4;
    }

    public void setImage4(Object image4) {
        this.image4 = image4;
    }

    public KindCategoryModel(Integer id) {
        this.id = id;

    }

    public KindCategoryModel() {
    }

    public String getCategoryName() {
        if (UtilityApp.INSTANCE.getLanguage().equals(Constants.English)) {
            return name;

        } else {
            return hName;
        }
    }

    public String getCatImage() {
        if (UtilityApp.INSTANCE.getLanguage().equals(Constants.English)) {
            return image2;

        } else {
            return image;
        }
    }

    }
