package com.ramez.shopp.Models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class ScanResult {

    @SerializedName("status")
    @Expose
     val status = 0

    @SerializedName("message")
    @Expose
     val message: String? = null

    @SerializedName("data")
    @Expose
     val data: ScanModel? = null
}