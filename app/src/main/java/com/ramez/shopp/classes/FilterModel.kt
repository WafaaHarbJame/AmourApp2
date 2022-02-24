package com.ramez.shopp.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.ramez.shopp.Models.ChildCat
import java.io.Serializable

class FilterModel : Serializable {
    @SerializedName("key")
    @Expose
    var key: String = ""

    @SerializedName("value")
    @Expose
    var value: String? = ""
}

