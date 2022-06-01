package com.amour.shop.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ameer on 6/22/2017.
 */

public class ErrorModel {

    @SerializedName("status")
    @Expose
    public int status;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("details")
    @Expose
    public String[] details = null;

}
