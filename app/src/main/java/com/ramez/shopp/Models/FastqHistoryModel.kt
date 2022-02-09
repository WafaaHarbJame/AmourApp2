package com.ramez.shopp.Models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FastqHistoryModel {
    @Expose
    @SerializedName("id")
    var id: Int? = 0

    @Expose
    @SerializedName("countryNameAr")
    var countryNameAr: String? = null

    @Expose
    @SerializedName("countryNameEn")
    var countryNameEn: String? = null
}
