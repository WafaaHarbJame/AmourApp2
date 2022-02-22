package com.ramez.shopp.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FilterModel {
    @SerializedName("key")
    @Expose
    private val key: String? = null

    @SerializedName("value")
    @Expose
    private val value: String? = null
}