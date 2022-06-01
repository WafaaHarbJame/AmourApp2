package com.amour.shop.Models.request

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BranchesByCountryRequest {
    @SerializedName("country_id")
    @Expose
    var countryId: Int = 0
}