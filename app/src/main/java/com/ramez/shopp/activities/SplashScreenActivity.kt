package com.ramez.shopp.activities


import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.*
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import java.util.*
import kotlin.collections.ArrayList


class SplashScreenActivity : ActivityBase() {
    var storeId = 0
    var userId = 0
    var countryId = 0
    var cartNumber = 0
    var lang: String? = null
    var shortName: String? = null
    var user: MemberModel? = null
    var localModel: LocalModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        startSplash()
    }

    private fun startSplash() {
        setContentView(R.layout.activity_splash_screen)
        localModel = UtilityApp.getLocalData()
        lang =
            if (UtilityApp.getLanguage() == null) Locale.getDefault().language else UtilityApp.getLanguage()
        if (localModel?.cityId != null) {
            storeId = localModel?.cityId?.toIntOrNull()
                ?: Constants.default_storeId.toInt()
            countryId = localModel?.countryId ?: Constants.default_country_id
            shortName = localModel?.shortname ?: Constants.default_short_name
            getCategories(storeId)
            getDinners(lang)
            getHomePage()
            getLinks(storeId)
            getCountryDetail(shortName)
        }
        initData()
    }

    private fun initData() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (UtilityApp.isLogin()) {
                if (UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) {
                    storeId = localModel?.cityId?.toIntOrNull() ?: 0
                    user = UtilityApp.getUserData()
                    userId = user?.id ?: 0
                    getUserAddress(userId)
                    getFastQCarts(storeId, userId)
                    getUserData(userId, storeId)
                    getFastSetting(storeId,userId.toString())

                } else {
                    UtilityApp.logOut()
                    val intent =
                        Intent(activity, RegisterLoginActivity::class.java)
                    intent.putExtra(Constants.LOGIN, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            } else {
                if (!UtilityApp.isFirstRun()) {
                    val intent = Intent(activity, Constants.MAIN_ACTIVITY_CLASS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    startWelcomeActivity()
                }
            }
        }, SPLASH_TIMER.toLong())
    }

    private fun startWelcomeActivity() {
        startActivity(Intent(activity, ChangeLanguageActivity::class.java))
    }

    fun getUserData(user_id: Int, store_id: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<ProfileData?>?

                if (IsSuccess && result != null && result.data != null) {
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

                } else {
                    UtilityApp.logOut()
                    val intent =
                        Intent(activity, RegisterLoginActivity::class.java)
                    intent.putExtra(Constants.LOGIN, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }).getUserDetails(user_id, store_id)
    }

    private fun getTotalPoints(userId: Int) {
        if (UtilityApp.isLogin()) {
            if (UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) {
                user = UtilityApp.getUserData()
                this@SplashScreenActivity.userId = user?.id ?: 0
                DataFeacher(false, object : DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        val result = obj as ResultAPIModel<TotalPointModel?>?
                        if (result != null && result.isSuccessful && result.data != null) {
                            val totalPointModel = result.data
                            DBFunction.setTotalPoints(totalPointModel)
                        }
                    }

                }).getTotalPoint(userId)

            }

        }
    }

    private fun getCountryDetail(shortName: String?) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<CountryDetailsModel?>?
                if (result != null && result.isSuccessful && result.data != null) {
                    val countryDetailsModel = result.data
                    DBFunction.setLoyal(countryDetailsModel)

                    if (countryDetailsModel?.hasLoyal == true) {
                        getCouponSettings(countryId)

                        getTotalPoints(userId)
                    }
                }
            }

        }).getCountryDetail(shortName)
    }

    private fun getCouponSettings(countryId: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<SettingCouponsModel?>?
                if (result?.data != null && result.status == 200) {
                    val settingCouponsModel = result.data
                    DBFunction.setCouponSettings(settingCouponsModel)
                }
            }

        }).getSettings(countryId)
    }

    private fun getFastSetting(cityId:Int, userId1 :String) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<FastValidModel?>?
                if (result?.data != null && result.status == 200) {
                    val settingFastQModel = result.data
                    Log.i(javaClass.name, "Log Get Fastq splash " +settingFastQModel?.isIsValid)

                    DBFunction.setFastSetting(settingFastQModel)
                }
            }

        }).GetSetting(cityId,userId1)
    }



    fun getCategories(storeId: Int) {
        UtilityApp.setCategoriesData(null)
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as CategoryResultModel?
                if (IsSuccess) {
                    if (result?.data?.size ?: 0 > 0) {
                        val categoryModelList = result?.data
                        UtilityApp.setCategoriesData(categoryModelList)
                    }
                }
            }
        }).GetAllCategories(storeId)
    }

    private fun getDinners(lang: String?) {
        UtilityApp.setDinnersData(null)
        Log.i("TAG", "Log dinners Size")
        Log.i("TAG", "Log country id $countryId")
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                if (IsSuccess) {
                    val result = obj as ResultAPIModel<ArrayList<DinnerModel>?>

                    if (result.data?.size ?: 0 > 0) {
                        Log.i("TAG", "Log dinners Size" + result.data?.size)
                        val dinnerModels = result.data
                        UtilityApp.setDinnersData(dinnerModels)
                    }
                }
            }

        }).getDinnersList(lang)
    }

    fun getCarts(storeId: Int, userId: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    val cartResultModel = obj as CartResultModel?
                    if (cartResultModel?.status == 200 && cartResultModel.data.cartData != null &&
                            cartResultModel.data.cartData.size > 0
                    ) {

                        val data = cartResultModel.data
                        cartNumber = data.cartCount
                        UtilityApp.setCartCount(cartNumber)
                        val minimumOrderAmount = data.minimumOrderAmount
                        localModel?.minimum_order_amount = minimumOrderAmount
                        UtilityApp.setLocalData(localModel)

                    } else {
                        UtilityApp.setCartCount(0)
                    }
                    val intent = Intent(activity, Constants.MAIN_ACTIVITY_CLASS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }

        }).GetCarts(storeId, userId)
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

    private fun getHomePage() {
        val sliderList = ArrayList<Slider>()
        val bannersList = ArrayList<Slider>()
        Log.i(ContentValues.TAG, "Log getSliders ")
        Log.i(ContentValues.TAG, "Log getSliders country_id $countryId")
        Log.i(ContentValues.TAG, "Log getSliders user_id $userId")
        Log.i(ContentValues.TAG, "Log getSliders  city_id $storeId")
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                UtilityApp.setSliderData(null)
                UtilityApp.setBannerData(null)
                if (IsSuccess) {
                    sliderList.clear()
                    bannersList.clear()
                    val result = obj as MainModel

                    if (result.sliders.size > 0) {

                        for (i in result.sliders.indices) {

                            val slider = result.sliders[i]
                            if (slider.type == 0) {
                                sliderList.add(slider)
                            }
                            else  if (slider.type == 1) {
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

        }).GetMainPage(0, countryId, storeId, userId.toString())
    }

    fun getUserAddress(user_id: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    val result = obj as AddressResultModel
                    if (result.data != null && result.data.size > 0) {
                        val addressList: java.util.ArrayList<AddressModel>? = result.data
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

                    getCarts(storeId, userId)


                }
            }

        }).getFastQCarts(storeId, userId.toString())
    }


    companion object {
        private const val SPLASH_TIMER = 3500
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

}