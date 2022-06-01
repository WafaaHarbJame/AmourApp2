package com.amour.shop.Models.request

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.amour.shop.classes.FilterModel
import com.amour.shop.classes.SortModel


class ProductRequest(
    @SerializedName("category_id")
    @Expose
    var categoryId: Int,
    @SerializedName("country_id")
    @Expose
    var countryId: Int,
    @SerializedName("city_id")
    @Expose
    var cityId: Int,
    @SerializedName("filter")
    @Expose
    var filter: String?,
    @SerializedName("brand_id")
    @Expose
    var brandId: Int,
    @SerializedName("page_number")
    @Expose
    var pageNumber: Int,
    @SerializedName("page_size")
    @Expose
    var pageSize: Int,
    @SerializedName("kind_id")
    @Expose
    var kindId: Int,
    @SerializedName("srots")
    @Expose
    var srots: List<SortModel>?,
    @SerializedName("filters")
    @Expose
    var filters: List<FilterModel>?
) {

}