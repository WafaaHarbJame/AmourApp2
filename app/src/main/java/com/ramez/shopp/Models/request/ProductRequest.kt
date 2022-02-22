package com.ramez.shopp.Models.request

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import com.ramez.shopp.classes.FilterModel
import com.ramez.shopp.classes.SortModel


class ProductRequest {
    constructor(
        categoryId: Int,
        countryId: Int,
        cityId: Int,
        filter: String?,
        brandId: Int,
        pageNumber: Int,
        pageSize: Int,
        kindId: Int,
        srots: List<SortModel>?,
        filters: List<FilterModel>?
    ) {
        this.categoryId = categoryId
        this.countryId = countryId
        this.cityId = cityId
        this.filter = filter
        this.brandId = brandId
        this.pageNumber = pageNumber
        this.pageSize = pageSize
        this.kindId = kindId
        this.srots = srots
        this.filters = filters
    }

    @SerializedName("category_id")
    @Expose
   var categoryId = 0

    @SerializedName("country_id")
    @Expose
   var countryId = 0

    @SerializedName("city_id")
    @Expose
   var cityId = 0

    @SerializedName("filter")
    @Expose
   var filter: String? = null

    @SerializedName("brand_id")
    @Expose
   var brandId = 0

    @SerializedName("page_number")
    @Expose
   var pageNumber = 0

    @SerializedName("page_size")
    @Expose
   var pageSize = 0

    @SerializedName("kind_id")
    @Expose
   var kindId = 0

    @SerializedName("srots")
    @Expose
   var srots: List<SortModel>? = null

    @SerializedName("filters")
    @Expose
   var filters: List<FilterModel>? = null

}