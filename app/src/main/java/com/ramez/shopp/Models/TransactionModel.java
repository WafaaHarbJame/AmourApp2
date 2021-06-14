package com.ramez.shopp.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionModel {

    @SerializedName("billAmt")
    @Expose
    public double billAmt;
    @SerializedName("currentPoints")
    @Expose
    public double currentPoints;
    @SerializedName("billDate")
    @Expose
    public String billDate;

}
