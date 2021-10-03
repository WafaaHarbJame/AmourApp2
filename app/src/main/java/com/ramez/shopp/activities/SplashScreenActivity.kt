package com.ramez.shopp.activities


import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.DBFunction
import com.ramez.shopp.Classes.SoicalLink
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import java.util.*


class SplashScreenActivity : ActivityBase() {
    var storeId = 0
    var userId = 0
    var country_id = 0
    var cartNumber = 0
    var lang: String? = null
    var shortName: String? = null
    var user: MemberModel? = null
    var localModel: LocalModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
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
            country_id = localModel?.countryId ?: Constants.default_country_id
            shortName = localModel?.shortname ?: Constants.default_short_name
            getCategories(storeId)
            getDinners(lang)
            GetHomePage()
            getCouponSettings(country_id)
            getCountryDetail(shortName)
            getLinks(storeId)
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
                    getTotalPoints(userId)
                    GetUserAddress(userId)
                    getUserData(userId, storeId)
                } else {
                    UtilityApp.logOut()
                    val intent =
                        Intent(activiy, RegisterLoginActivity::class.java)
                    intent.putExtra(Constants.LOGIN, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            } else {
                if (!UtilityApp.isFirstRun()) {
                    val intent = Intent(activiy, Constants.MAIN_ACTIVITY_CLASS)
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
        startActivity(Intent(activiy, ChangeLanguageActivity::class.java))
    }

    fun getUserData(user_id: Int, store_id: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val message = getString(R.string.fail_to_get_data)
                val result = obj as ResultAPIModel<ProfileData?>?
                if (IsSuccess && result != null && result.data != null) {
                    val memberModel = UtilityApp.getUserData()
                    val profileData = result.data
                    val name = profileData?.name ?: ""
                    val email = profileData?.email ?: ""
                    val LoyalBarcode = profileData?.loyalBarcode ?: ""
                    val ProfilePicture = profileData?.profilePicture ?: ""
                    memberModel.name = name
                    memberModel.email = email
                    memberModel.loyalBarcode = LoyalBarcode
                    memberModel.profilePicture = ProfilePicture
                    UtilityApp.setUserData(memberModel)
                    getCarts(storeId, userId)
                } else {
                    UtilityApp.logOut()
                    val intent =
                        Intent(activiy, RegisterLoginActivity::class.java)
                    intent.putExtra(Constants.LOGIN, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }).getUserDetails(user_id, store_id)
    }

    private fun getTotalPoints(userId: Int) {
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

    private fun getCountryDetail(shortName: String?) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<CountryDetailsModel?>?
                if (result != null && result.isSuccessful && result.data != null) {
                    val countryDetailsModel = result.data
                    DBFunction.setLoyal(countryDetailsModel)
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

    fun getCategories(storeId: Int) {
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

    fun getDinners(lang: String?) {
        UtilityApp.setDinnersData(null)
        Log.i("TAG", "Log dinners Size")
        Log.i("TAG", "Log country id $country_id")
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<ArrayList<DinnerModel>?>
                if (IsSuccess) {
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
                    val intent = Intent(activiy,Constants.MAIN_ACTIVITY_CLASS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }

        }).GetCarts(storeId, userId)
    }

    fun getLinks(store_id: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<SoicalLink>
                if (IsSuccess) {
                    val soicalLink = result.data
                    UtilityApp.SetLinks(soicalLink)
                }
            }

        }).getLinks(store_id)
    }

    fun GetHomePage() {
        val sliderList = ArrayList<Slider>()
        val bannersList = ArrayList<Slider>()
        Log.i(ContentValues.TAG, "Log getSliders ")
        Log.i(ContentValues.TAG, "Log getSliders country_id $country_id")
        Log.i(ContentValues.TAG, "Log getSliders user_id $userId")
        Log.i(ContentValues.TAG, "Log getSliders  city_id $storeId")
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as MainModel
                UtilityApp.setSliderData(null)
                UtilityApp.setBannerData(null)
                if (IsSuccess) {
                    sliderList.clear()
                    bannersList.clear()
                    if (result.sliders.size > 0) {
                        for (i in result.sliders.indices) {
                            val slider = result.sliders[i]
                            if (slider.type == 0) {
                                sliderList.add(slider)
                            } else {
                                bannersList.add(slider)
                            }
                        }
                        if (sliderList.size > 0) {
                            UtilityApp.setSliderData(sliderList)
                        }
                        if (bannersList.size > 0) {
                            UtilityApp.setBannerData(bannersList)
                        }
                    }
                }
            }

        }).GetMainPage(0, country_id, storeId, userId.toString())
    }

    fun GetUserAddress(user_id: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as AddressResultModel
                if (IsSuccess) {
                    if (result.data != null && result.data.size > 0) {
                        var addressList: ArrayList<AddressModel>? = ArrayList()
                        addressList = result.data
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

    companion object {
        private const val SPLASH_TIMER = 3500
    }
}