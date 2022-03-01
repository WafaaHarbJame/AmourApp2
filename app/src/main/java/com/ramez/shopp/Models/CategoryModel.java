package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.classes.UtilityApp;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryModel implements Serializable {
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
    private ArrayList<ChildCat> childCat = null;
    @SerializedName("image2")
    @Expose
    private String image2;
    @SerializedName("image3")
    @Expose
    private String image3;
    @SerializedName("image4")
    @Expose
    private String image4;

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

    public ArrayList<ChildCat> getChildCat() {
        return childCat;
    }

    public void setChildCat(ArrayList<ChildCat> childCat) {
        this.childCat = childCat;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public CategoryModel(Integer id) {
        this.id = id;

    }

    public CategoryModel() {
    }

    public String getCategoryName() {
        if (UtilityApp.getLanguage().equals(Constants.English)) {
            return name;

        } else {
            return hName;
        }
    }

    public String getCatImage() {
        if (UtilityApp.getLanguage().equals(Constants.English)) {
            return image2;

        } else {
            return image;
        }
    }

    public String getNewCat() {
        if (UtilityApp.getLanguage().equals(Constants.English)) {
            return image4==null?getCatImage():image4;

        } else {
            return image3==null?getCatImage():image3;
        }
    }


}
