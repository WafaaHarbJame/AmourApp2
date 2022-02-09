package com.ramez.shopp.classes

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Models.*
import com.ramez.shopp.activities.RegisterLoginActivity
import java.util.ArrayList

class LocalsData(var activity: Activity?) {

    fun callAuthApis(userId: Int, storeId: Int) {

        getUserAddress(userId)
        getFastQCarts(storeId, userId)
        getUserData(userId, storeId)
        getFastSetting(storeId, userId.toString())
        getCarts(storeId, userId)

    }

    private fun getUserAddress(user_id: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                if (IsSuccess) {
                    val result = obj as AddressResultModel?
                    if (result?.status == 200 && result.data != null && result.data.size > 0) {
                        val addressList: ArrayList<AddressModel>? = result.data
                        if (addressList != null && addressList.size > 0) for (i in addressList.indices) {
                            val addressModel = addressList[i]
                            if (addressModel.default) {
                                val user = UtilityApp.getUserData()
                                if (user != null && addressModel.id != null) user.setLastSelectedAddress(
                                    addressModel.id
                                )
                                UtilityApp.setUserData(user)
                            }
                        }
                    }
                }
//                else{
//
//                    val intent = Intent(activity, MainActivity::class.java)
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                    activity?.startActivity(intent)
//                }
            }
        }).GetAddressHandle(user_id)
    }

    private fun getFastQCarts(storeId: Int, userId: Int) {

        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                if (IsSuccess) {

                    val cartFastQModel = obj as ResultAPIModel<ArrayList<CartFastQModel?>?>?
                    if (cartFastQModel?.status == 200 && cartFastQModel.data?.isNotEmpty() == true) {

                        val data = cartFastQModel.data
                        val fastCartNumber = data?.size ?: 0
                        UtilityApp.setFastQCartCount(fastCartNumber)

                        calculateSubTotalPrice(data)
                        Log.i(javaClass.name, "Log fast Cart count $fastCartNumber")


                    } else {
                        Log.i(javaClass.name, "Log fast Cart count 0")

                        UtilityApp.setFastQCartCount(0)
                        UtilityApp.setFastQCartTotal(0f)
                    }

                }
            }

        }).getFastQCarts(storeId, userId.toString())
    }

    fun getUserData(user_id: Int, store_id: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    val result = obj as ResultAPIModel<ProfileData?>?

                    if (result?.data != null) {
                        val memberModel = UtilityApp.getUserData()
                        val profileData = result.data
                        val name = profileData?.name ?: ""
                        val email = profileData?.email ?: ""
                        val loyalBarcode = profileData?.loyalBarcode ?: ""
                        val profilePicture = profileData?.profilePicture ?: ""

                        memberModel?.name = name
                        memberModel?.email = email
                        memberModel?.loyalBarcode = loyalBarcode
                        memberModel?.profilePicture = profilePicture
                        UtilityApp.setUserData(memberModel)
                    }


                } else {
                    UtilityApp.logOut()
                    val intent =
                        Intent(activity, RegisterLoginActivity::class.java)
                    intent.putExtra(Constants.LOGIN, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    activity?.startActivity(intent)
                }
            }
        }).getUserDetails(user_id, store_id)
    }

    fun getFastSetting(cityId: Int, userId1: String) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                if (IsSuccess) {
                    val result = obj as ResultAPIModel<FastValidModel?>?
                    if (result?.status == 200 && result.data != null) {

                        val settingFastQModel = result.data
                        Log.i(javaClass.name, "Log Get Fastq splash " + settingFastQModel?.isIsValid)

                        DBFunction.setFastSetting(settingFastQModel)
                    }
                }
            }

        }).GetSetting(cityId, userId1)
    }

    fun calculateSubTotalPrice(cartList: ArrayList<CartFastQModel?>?): Double {
        var subTotal = 0.0
        for (product in cartList ?: mutableListOf()) {

            subTotal = subTotal.plus(product?.price?.times(product.qty) ?: 0.0)

        }
        UtilityApp.setFastQCartTotal(subTotal.toFloat())
        println("Log subTotal result $subTotal")
        return subTotal
    }

    private fun getCarts(storeId: Int, userId: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                if (IsSuccess) {
                    val cartResultModel = obj as CartResultModel?
                    if (cartResultModel?.status == 200 && cartResultModel.data.cartData != null &&
                            cartResultModel.data.cartData.size > 0
                    ) {

                        val data = cartResultModel.data
                        var cartNumber = 0
                        cartNumber = data.cartCount
                        UtilityApp.setCartCount(cartNumber)
                        val minimumOrderAmount = data.minimumOrderAmount
                        val localModel: LocalModel? = UtilityApp.getLocalData()
                        localModel?.minimum_order_amount = minimumOrderAmount
                        UtilityApp.setLocalData(localModel)

                    } else {
                        UtilityApp.setCartCount(0)
                    }

                }
                val intent = Intent(activity, Constants.MAIN_ACTIVITY_CLASS)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                activity?.startActivity(intent)
                activity?.finish()
            }

        }).GetCarts(storeId, userId)
    }



}