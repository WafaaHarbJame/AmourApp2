package com.amour.shop.Models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class PaymentResp {
    @SerializedName("status")
    @Expose
var status: String? = null

    @SerializedName("result")
    @Expose
var result: String? = null

    @SerializedName("error")
    @Expose
var error: Any? = null

    @SerializedName("errorText")
    @Expose
var errorText: Any? = null

    @SerializedName("exception")
    @Expose
var exception: Any? = null
    
}