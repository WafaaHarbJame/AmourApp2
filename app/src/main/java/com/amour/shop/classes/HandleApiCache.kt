package com.amour.shop.classes

import android.content.ContentValues
import android.util.Log
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.Models.*
import com.amour.shop.R
import java.util.ArrayList

class HandleApiCache {
    fun callApis(countryId: Int, cityId: Int, shortName: String?) {
        getCategories(cityId)
        getSlider(cityId)
        getPaymentMethod(cityId)
        getKinds()
        getAllBrands(cityId)
        getDinners(UtilityApp.getLanguage())
        getLinks(cityId)
    }

    fun getCategories(storeId: Int) {
        UtilityApp.setCategoriesData(null)
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as CategoryResultModel?
                if (IsSuccess) {
                    if (result?.data?.isNotEmpty() == true) {
                        UtilityApp.setCategoriesData(result.data)
                    }
                }
            }
        }).GetAllCategories(storeId)
    }

    private fun getDinners(lang: String?) {
        UtilityApp.setDinnersData(null)
        Log.i("TAG", "Log dinners Size")
//        Log.i("TAG", "Log country id $countryId")
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                if (IsSuccess) {
                    val result = obj as ResultAPIModel<MutableList<DinnerModel?>?>
                    val dinnerModels = result.data

                    Log.i("TAG", "Log dinners Size" + result.data?.size)
                    if (dinnerModels?.isNotEmpty() == true) {
                        UtilityApp.setDinnersData(dinnerModels)
                    }
                }
            }

        }).getDinnersList(lang ?: "")
    }

    private fun getLinks(store_id: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    val result = obj as ResultAPIModel<SoicalLink>
                    val socialLink = result.data
                    UtilityApp.SetLinks(socialLink)
                }
            }

        }).getLinks(store_id)
    }

    private fun getKinds() {
//        UtilityApp.setAllKindsData(null)
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    val result =
                        obj as ResultAPIModel<ArrayList<KindCategoryModel?>?>
                    if (result.data?.size ?: 0 > 0) {
                        val categoryModelList = result.data
                        UtilityApp.setAllKindsData(categoryModelList)
                    }
                }
            }
        }).getAllKindsList()
    }

    private fun getPaymentMethod(storeId: Int) {
        UtilityApp.setPaymentsData(null)
        DataFeacher(
            false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                    if (IsSuccess) {
                        val result = obj as PaymentResultModel?
                        if (result?.data != null && result.data.size > 0) {
                            var paymentList: MutableList<PaymentModel>? = null
                            paymentList = result.data
                            for (i in paymentList!!.indices) {

                                val paymentModel = paymentList[i]

                                when (paymentModel.id) {
                                    1 -> {
                                        paymentModel.image = R.drawable.cash
                                    }
                                    2 -> {
                                        paymentModel.image = R.drawable.card
                                    }
                                    3 -> {
                                        paymentModel.image = R.drawable.benefit
                                    }
                                    4 -> {
                                        paymentModel.image = R.drawable.card
                                    }
                                    5 -> {
                                        paymentModel.image = R.drawable.benefit_web
                                    }

                                    6 -> {
                                        paymentModel.image = R.drawable.benefit
                                    }

                                    7 -> {
                                        paymentModel.image = R.drawable.benefit_web
                                    }
                                }
                            }
                            Log.i(javaClass.name, "Log paymentList ${paymentList.size}")

                            UtilityApp.setPaymentsData(paymentList)
                        }
                    }
                }


            }
        ).getPaymentMethod(storeId)
    }

    fun getAllBrands(storeId: Int) {
        UtilityApp.setBrandsData(null)
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        val result = obj as ResultAPIModel<ArrayList<BrandModel>?>?
                        val allBrandList = result?.data
                        if (allBrandList?.isNotEmpty() == true) {
                            UtilityApp.setBrandsData(allBrandList)
                        }
                    }


                }
            }).GetAllBrands(storeId)
    }


    private fun getSlider(cityId: Int) {
        val sliderList = mutableListOf<Slider?>()
        val bannersList = mutableListOf<Slider?>()
        Log.i(ContentValues.TAG, "Log getSliders ")
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
//                UtilityApp.setSliderData(null)
//                UtilityApp.setBannerData(null)
                if (IsSuccess) {
                    sliderList.clear()
                    bannersList.clear()
                    val result = obj as ResultAPIModel<MutableList<Slider>?>
                    if (result.data?.isNotEmpty() == true) {
                        result.data?.forEach { slider ->
                            if (slider.type == 0) {
                                sliderList.add(slider)
                            } else if (slider.type == 1) {
                                bannersList.add(slider)
                            }
                        }
                        Log.i(javaClass.name, "Log sliderList ${sliderList.size}")
                        Log.i(javaClass.name, "Log sliderList banner ${bannersList.size}")

                        if (sliderList.size > 0) {
                            UtilityApp.setSliderData(sliderList)
                        }
                        if (bannersList.size > 0) {
                            UtilityApp.setBannerData(bannersList)
                        }
                    }
                }
            }

        }).getSliders(cityId)
    }
}