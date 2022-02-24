package com.ramez.shopp.classes

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName




class SortModel {
    @SerializedName("key")
    @Expose
    var key: String? = ""

    @SerializedName("isDescending")
    @Expose
    var isDescending = false

}