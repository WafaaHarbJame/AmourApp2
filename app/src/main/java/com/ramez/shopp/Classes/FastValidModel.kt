package com.ramez.shopp.Classes

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class FastValidModel {
    @SerializedName("isValid")
    @Expose
    var isIsValid = false
        private set

    fun setIsValid(isValid: Boolean) {
        isIsValid = isValid
    }
}