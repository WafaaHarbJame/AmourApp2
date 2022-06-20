package com.amour.shop


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.classes.*
import com.amour.shop.Models.*
import com.amour.shop.activities.ActivityBase
import com.amour.shop.activities.ChangeLanguageActivity
import com.amour.shop.activities.RegisterLoginActivity
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
            getCacheData()

        }
        initData()
    }

    private fun getCacheData() {
        HandleApiCache().callApis(countryId, storeId, shortName)
        getCityList(countryId, activity)
        getCountryDetail(shortName)
    }

    private fun initData() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (UtilityApp.isLogin()) {
                if (UtilityApp.getUserData() != null && UtilityApp.getUserData()?.id != null) {
                    println("Log splash to checkToken")
                    storeId = localModel?.cityId?.toIntOrNull() ?: 0
                    user = UtilityApp.getUserData()
                    userId = user?.id ?: 0

                    checkToken()

//                    getUserAddress(userId)
//                    getFastQCarts(storeId, userId)
//                    getUserData(userId, storeId)
//                    getFastSetting(storeId,userId.toString())


                } else {
                    println("Log splash to registerLogin")
                    UtilityApp.logOut()
                    val intent =
                        Intent(activity, RegisterLoginActivity::class.java)
                    intent.putExtra(Constants.LOGIN, true)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            } else {
                if (!UtilityApp.isFirstRun()) {
                    println("Log splash to main")
                    val intent = Intent(activity, Constants.MAIN_ACTIVITY_CLASS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    println("Log splash to welcome")
                    startWelcomeActivity()
                }
            }
        }, SPLASH_TIMER.toLong())
    }


    private fun startWelcomeActivity() {
        startActivity(Intent(activity, ChangeLanguageActivity::class.java))
    }


    private fun getTotalPoints(userId: Int) {
        if (UtilityApp.isLogin()) {
            if (UtilityApp.getUserData() != null && UtilityApp.getUserData()?.id != null) {
                user = UtilityApp.getUserData()
                this@SplashScreenActivity.userId = user?.id ?: 0
                DataFeacher(false, object : DataFetcherCallBack {
                    override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                        if (IsSuccess) {
                            val result = obj as ResultAPIModel<TotalPointModel?>?
                            if (result != null && result.isSuccessful && result.data != null) {
                                val totalPointModel = result.data
                                DBFunction.setTotalPoints(totalPointModel)
                            }
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
                        if (userId > 0) {
                            getCouponSettings(countryId)
                            getTotalPoints(userId)

                        }
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
                    val result = obj as ResultAPIModel<MutableList<DinnerModel?>?>
                    val dinnerModels = result.data

                    Log.i("TAG", "Log dinners Size" + result.data?.size ?: "0")
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
                    val result = obj as ResultAPIModel<SoicalLink>?
                    val socialLink = result?.data
                    UtilityApp.SetLinks(socialLink)
                }
            }

        }).getLinks(store_id)
    }


    companion object {
        private const val SPLASH_TIMER = 3500
    }

    private fun refreshToken(token: String, refreshToken: String) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (IsSuccess) {
                    val result = obj as ResultAPIModel<RefreshTokenModel?>?
                    if (result?.status == 200 && result.data != null) {
                        UtilityApp.setRefreshToken(result.data?.refreshToken)
                        UtilityApp.setUserToken(result.data?.token)
                        Log.i(javaClass.name, "Log updated token ${result.data?.token}")
                        Log.i(javaClass.name, "Log updated refreshToken ${result.data?.refreshToken}")

                        LocalsData(activity).callAuthApis(userId, storeId)

                    } else if (result?.status == 501) {
                        LocalsData(activity).callAuthApis(userId, storeId)

                    } else {
                        openMainActivity()
                    }

                } else {
                    openMainActivity()
                }


            }

        }).refreshToken(token, refreshToken)
    }

    fun openMainActivity() {
        val intent = Intent(activity, Constants.MAIN_ACTIVITY_CLASS)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun loginAgain() {
        val intent = Intent(activity, RegisterLoginActivity::class.java)
        intent.putExtra(Constants.LOGIN, true)
        startActivity(intent)

    }

    private fun checkToken() {
        val userToken = UtilityApp.getUserToken()
        val refreshToken = UtilityApp.getRefreshToken()

        if (userToken?.isNotEmpty() == true && refreshToken?.isNotEmpty() == true) {
            refreshToken(userToken, refreshToken)
        } else {
            loginAgain()
        }
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
                            var paymentList: List<PaymentModel>? = null
                            paymentList = result.data
                            for (i in paymentList!!.indices) {

                                val paymentModel = paymentList[i]

                                when (paymentModel.id) {
                                    1 -> {
                                        paymentModel.image = R.drawable.cash
                                    }
                                    2 -> {
                                        paymentModel.image = R.drawable.click_collect
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

    private fun getCityList(country_id: Int, activity: Activity) {
        Log.i("TAG", "Log country_id$country_id")
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as CityModelResult?
                if (IsSuccess) {
                    val list: MutableList<CityModel>? = result?.data

                    if (list?.isNotEmpty() == true) {
                        UtilityApp.saveCountryBranches(country_id, list)
                    }
                }

            }
        }).cityHandle(country_id, activity)
    }


}



