package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AreasModel  implements Comparable<AreasModel>, Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name_en")
    @Expose
    private String nameEn;
    @SerializedName("name_ae")
    @Expose
    private String nameAe;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameAe() {
        return nameAe;
    }

    public void setNameAe(String nameAe) {
        this.nameAe = nameAe;
    }

    public AreasModel(Integer id, String nameEn, String nameAe) {
        this.id = id;
        this.nameEn = nameEn;
        this.nameAe = nameAe;
    }

    @Override

    public int compareTo(AreasModel areasModel) {
        return getId().compareTo(areasModel.getId());
    }

}
