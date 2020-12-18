package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;

import java.io.Serializable;
import java.util.List;

public class ChildCat implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
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
    private List<CategoryModel> childCat = null;

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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<CategoryModel> getChildCat() {
        return childCat;
    }

    public void setChildCat(List<CategoryModel> childCat) {
        this.childCat = childCat;
    }

    public String getCatName(){
        if(UtilityApp.getLanguage().equals(Constants.English)){
            return name;

        }
        else {
            return hName;
        }
    }

}
