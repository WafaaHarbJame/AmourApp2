package com.ramez.shopp.Models

import android.os.Parcel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.UtilityApp
import java.io.Serializable


class CartFastQModel() :  Serializable {
    @SerializedName("isScale")
    @Expose
    val isScale = false

    @SerializedName("id")
    @Expose
    val id = 0

    @SerializedName("user_id")
    @Expose
    val userId = 0

    @SerializedName("sgOrderId")
    @Expose
    val sgOrderId = 0

    @SerializedName("barcode")
    @Expose
    val barcode: String? = null

    @SerializedName("qty")
    @Expose
    var qty = 0

    @SerializedName("price")
    @Expose
    val price = 0.0

    @SerializedName("des")
    @Expose
    val des: String? = null

    @SerializedName("des2")
    @Expose
    val des2: String? = null

    @SerializedName("valid_to")
    @Expose
    val validTo: String? = null

    @SerializedName("valid_from")
    @Expose
    val validFrom: String? = null

    @SerializedName("prom_price")
    @Expose
    val promPrice = 0.0

    @SerializedName("retail")
    @Expose
    val retail = 0.0

    @SerializedName("order")
    @Expose
    val order: Any? = null

    @SerializedName("storeId")
    @Expose
    val storeId = 0

    constructor(parcel: Parcel) : this() {
        qty = parcel.readInt()
    }

    fun name(): String? {
        return if (UtilityApp.getLanguage() == Constants.English) {
            des
        } else {
            des2
        }
    }

}