package com.amour.shop.ApiHandler

import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.amour.shop.BuildConfig
import com.amour.shop.Models.*
import com.amour.shop.Models.request.BranchesByCountryRequest
import com.amour.shop.Models.request.ProductRequest
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData
import com.amour.shop.classes.UtilityApp
import mobi.foo.benefitinapp.data.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.NoRouteToHostException
import java.net.UnknownHostException
import java.util.*


class DataFeacher {
    val TAG = "Log"
    val LOGIN_URL = "/" + GlobalData.COUNTRY + GlobalData.grocery + "api/v9/Account/login"
    val LOGIN_AMOUR_URL = "/" + GlobalData.COUNTRY_AMOUR + GlobalData.amour + "api/v9/Account/login"

    var dataFetcherCallBack: DataFetcherCallBack? = null
    var mainApiService: ApiInterface? = null
    var mainCall: Call<Any?>? = null
    var altApiService: ApiInterface? = null
    var altCall: Call<Any?>? = null

    var isCallAltApi = false
    var lang: String? = null
    var headerMap: MutableMap<String, Any> = HashMap()
    private var callbackApi: Callback<Any?>
    private val activity: Activity? = null

    constructor() {
        mainApiService =
            ApiClient.getClient(UtilityApp.getMainAltUrl(false))?.create(ApiInterface::class.java)!!


        dataFetcherCallBack =
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                }
            }
        val token = UtilityApp.getToken()
        headerMap["ApiKey"] = Constants.api_key
        headerMap["device_type"] = Constants.deviceType
        headerMap["app_version"] = UtilityApp.appVersion
        headerMap["token"] = token
        headerMap["Accept"] = "application/json"
        headerMap["Content-Type"] = "application/json"
        val accessToken = UtilityApp.getUserToken()
        if (accessToken?.isNotEmpty() == true) {
            headerMap["Authorization"] = Constants.TOKEN_PREFIX + accessToken
        }
        callbackApi = object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                if (response.isSuccessful) {

//                    ResultAPIModel result = (ResultAPIModel) response.body();
                    if (dataFetcherCallBack != null) dataFetcherCallBack!!.Result(
                        response.body(),
                        Constants.success,
                        true
                    )

                } else {
                    var errorModel: ResultAPIModel<*>? = null
                    try {
                        val error = response.errorBody()!!.string()
                        Log.e("Log", "Log error $error")
                        errorModel = Gson().fromJson(error, object : TypeToken<ResultAPIModel<*>?>() {}.type)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (response.code() == 404 || response.code() == 443) {

                        dataFetcherCallBack!!.Result(errorModel, Constants.ERROR, false)



                    } else if (response.code() == 401) {
                        dataFetcherCallBack!!.Result(errorModel, Constants.UnAuthorize, false)
                    } else {
                        dataFetcherCallBack!!.Result(errorModel, Constants.ERROR, false)
                    }
                }
            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {
                t.printStackTrace()
                if ((t is UnknownHostException || t is NoRouteToHostException) && dataFetcherCallBack != null) {
                    dataFetcherCallBack!!.Result(null, Constants.NO_CONNECTION, false)
                } else {
                    if (dataFetcherCallBack != null) dataFetcherCallBack!!.Result(null, Constants.FAIL, false)
                }
            }
        }
    }

    constructor(isLong: Boolean, dataFetcherCallBack: DataFetcherCallBack?) {
        this.dataFetcherCallBack = dataFetcherCallBack

        mainApiService =
            if (isLong) ApiClient.getLongClient(UtilityApp.getMainAltUrl(false))
                ?.create(ApiInterface::class.java)!!
            else ApiClient.getClient(UtilityApp.getMainAltUrl(false))?.create(ApiInterface::class.java)!!


        val token = UtilityApp.getToken()
        headerMap["ApiKey"] = Constants.api_key
        headerMap["device_type"] = Constants.deviceType
        headerMap["app_version"] = UtilityApp.getAppVersionStr()
        headerMap["token"] = token
        headerMap["Accept"] = "application/json"
        headerMap["Content-Type"] = "application/json"
        val accessToken = UtilityApp.getUserToken()
        if (!accessToken?.isEmpty()!!) {
            headerMap["Authorization"] = Constants.TOKEN_PREFIX + accessToken
            //            headerMap.put("Authorization",Constants.TOKEN_PREFIX +"euuJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRhbGFsLmF3YWRpMkBnbWFpbC5jb20iLCJuYW1lIjoiMzMzMTA0ODgiLCJuYW1laWQiOiIxMTgiLCJqdGkiOiI1MmYyZmM5ZS0yNjhlLTQ4OWEtYjJhYi0wYjhjODkwNzNmMTkiLCJuYmYiOjE2NDEzODI4NDksImV4cCI6MTY0MTQwMjY0OSwiaWF0IjoxNjQxMzgyODQ5LCJpc3MiOiJodHRwczovL3Jpc3RlaC5jb20iLCJhdWQiOiJodHRwczovL3Jpc3RlaC5jb20vIn0.7eL6bF9uVQ3a6b6QH9QoNq1BY2nYhFLvxYnDV2D_5V8");
        }
        callbackApi = object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                val url = call.request().url.toUrl().path
                println("Log url " + call.request().url)
                println("Log response time " + (response.raw().receivedResponseAtMillis - response.raw().sentRequestAtMillis))
                if (response.isSuccessful) {
                    var curUrl = LOGIN_AMOUR_URL

                    if (url == curUrl) {
                        val result = response.body() as LoginResultModel?
                        if (dataFetcherCallBack != null) {
                            if (result != null && result.status == 0) {
                                dataFetcherCallBack.Result(response.body(), Constants.ERROR, false)
                            } else {
                                dataFetcherCallBack.Result(response.body(), Constants.success, true)
                            }
                        }
                    } else {
                        dataFetcherCallBack?.Result(response.body(), Constants.success, true)
                    }
                } else {
                    var errorModel: ResultAPIModel<*>? = null
                    try {
                        val error = response.errorBody()!!.string()
                        Log.i("Log", "Log errorApiUrl $url")
                        Log.e("Log", "Log error $error")
                        Log.e("Log", "Log error code  " + response.code())
                        errorModel = Gson().fromJson(error, object : TypeToken<ResultAPIModel<*>?>() {}.type)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (response.code() == 404 || response.code() == 443) {

                        dataFetcherCallBack!!.Result(errorModel, Constants.ERROR, false)



                    } else if (response.code() == 401) {
                        dataFetcherCallBack!!.Result(errorModel, Constants.UnAuthorize, false)
                    } else {
                        dataFetcherCallBack!!.Result(errorModel, Constants.ERROR, false)
                    }
                }
            }

            override fun onFailure(call: Call<Any?>?, t: Throwable) {
                t.printStackTrace()
                if ((t is UnknownHostException || t is NoRouteToHostException) && dataFetcherCallBack != null) {
                    dataFetcherCallBack.Result(null, Constants.NO_CONNECTION, false)
                } else {
                    dataFetcherCallBack?.Result(null, Constants.FAIL, false)
                }
            }
        }
    }



    private fun changeUrl(): String {
        val url = UtilityApp.getUrl()
        var currUrl = ""
        currUrl = if (url == GlobalData.BetaBaseRamezURL1) {
            GlobalData.BetaBaseRamezURL2
        } else {
            GlobalData.BetaBaseRamezURL1
        }
        UtilityApp.setUrl(currUrl)
        return currUrl
    }

    fun retryRequestWithNewUrl() {
        isCallAltApi = true
        altCall?.enqueue(callbackApi)

    }

    fun loginHandle(memberModel: MemberModel) {
        val params: MutableMap<String, Any?> = HashMap()
        params["mobile_number"] = memberModel.mobileNumber
        params["password"] = memberModel.password
        params["user_type"] = memberModel.userType
        params["device_type"] = Constants.deviceType
        params["device_token"] = memberModel.deviceToken + ""
        params["device_id"] = memberModel.deviceToken + ""
        params["city_id"] = memberModel.city
        Log.i(TAG, "Log loginHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log getMobileNumber " + memberModel.mobileNumber)
        Log.i(TAG, "Log password " + memberModel.password)
        Log.i(TAG, "Log device_type " + "android")
        Log.i(TAG, "Log device_token " + memberModel.deviceToken)
        Log.i(TAG, "Log getDeviceId " + memberModel.deviceId)
        Log.i(TAG, "Log user_type " + memberModel.userType)
        Log.i(TAG, "Log StoreId " + memberModel.city)
        val call = mainApiService?.loginUserHandle(headerMap, params) as Call<Any?>?
        altCall = altApiService?.loginUserHandle(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun refreshToken(token: String, refreshToken: String) {
        val params: MutableMap<String, Any?> = HashMap()
        params["token"] = token
        params["refreshToken"] = refreshToken
        Log.i(TAG, "Log RefreshToken")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log token $token")
        Log.i(TAG, "Log refreshToken $refreshToken")
        val call = mainApiService?.refreshToken(headerMap, params) as Call<Any?>?
        altCall = altApiService?.refreshToken(headerMap, params) as Call<Any?>?

        call?.enqueue(callbackApi)
    }

    fun logOut(memberModel: MemberModel?) {
        Log.i(TAG, "Log logOut")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id " + memberModel?.id)
        val call = mainApiService?.logout(headerMap, memberModel?.id ?: 0, Constants.user_type) as Call<Any?>?
        altCall = altApiService?.logout(headerMap, memberModel?.id ?: 0, Constants.user_type) as Call<Any?>?

        call?.enqueue(callbackApi)
    }

    fun registerHandle(memberModel: MemberModel) {
        val params: MutableMap<String, Any?> = HashMap()
        params["mobile_number"] = memberModel.mobileNumber + ""
        params["password"] = memberModel.password + ""
        params["user_type"] = Constants.user_type + ""
        params["device_type"] = memberModel.deviceType + ""
        params["device_token"] = memberModel.deviceToken + ""
        params["name"] = memberModel.name + ""
        params["country"] = memberModel.country + ""
        params["city"] = memberModel.city.toString() + ""
        params["email"] = memberModel.email + ""
        params["device_id"] = memberModel.deviceId + ""
        params["prefix"] = memberModel.prefix + ""

        Log.i(TAG, "Log RegisterHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log username " + memberModel.name)
        Log.i(TAG, "Log password " + memberModel.password)
        Log.i(TAG, "Log device_type " + "android")
        Log.i(TAG, "Log device_token " + memberModel.deviceToken)
        Log.i(TAG, "Log city ID  " + memberModel.city)
        Log.i(TAG, "Log country Name " + memberModel.country)
        Log.i(TAG, "Log mobile_number " + memberModel.mobileNumber)
        Log.i(TAG, "Log device_id " + memberModel.deviceId)
        Log.i(TAG, "Log prefix " + memberModel.prefix)
        Log.i(TAG, "Log device_token " + memberModel.deviceToken)
        Log.i(TAG, "Log EMAIL " + "")
        val call = mainApiService?.registerUserHandle(headerMap, params) as Call<Any?>?
        altCall = altApiService?.registerUserHandle(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun forgetPasswordHandle(memberModel: MemberModel) {
        val params: MutableMap<String, Any?> = HashMap()
        params["mobile_number"] = memberModel.mobileNumber
        params["user_type"] = memberModel.userType
        Log.i(TAG, "Log ForgetPasswordHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log mobile_number " + memberModel.mobileNumber)
        Log.i(TAG, "Log user_type " + memberModel.userType)
        val call = mainApiService?.ForgetPasswordHandle(headerMap, params) as Call<Any?>?
        altCall = altApiService?.ForgetPasswordHandle(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun changePasswordHandle(memberModel: MemberModel?) {
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = memberModel?.id
        params["password"] = memberModel?.password
        params["new_password"] = memberModel?.new_password
        Log.i(TAG, "Log ChangePasswordHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id " + memberModel?.id)
        Log.i(TAG, "Log password " + memberModel?.password)
        Log.i(TAG, "Log new_password " + memberModel?.new_password)
        val call = mainApiService?.changePasswordHandle(headerMap, params) as Call<Any?>?
        altCall = altApiService?.changePasswordHandle(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)


    }

    fun updatePasswordHandle(memberModel: MemberModel) {
        val params: MutableMap<String, Any?> = HashMap()

        params["mobile_number"] = memberModel.mobileNumber
        params["password"] = memberModel.password
        params["re_password"] = memberModel.new_password
        Log.i(TAG, "Log UpdatePasswordHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log mobile_number " + memberModel.mobileNumber)
        Log.i(TAG, "Log password " + memberModel.password)
        Log.i(TAG, "Log re_password " + memberModel.new_password)
        Log.i(TAG, "Log updatePasswordHandle params $params")
        val call = mainApiService?.updatePasswordHandle(headerMap, params) as Call<Any?>?
        altCall = altApiService?.updatePasswordHandle(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun updateTokenHandle(memberModel: MemberModel?) {
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = memberModel?.id
        params["device_token"] = memberModel?.deviceToken
        params["user_type"] = Constants.user_type
        Log.i(TAG, "Log UpdateTokenHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id " + memberModel?.id)
        Log.i(TAG, "Log device_token " + memberModel?.deviceToken)
        Log.i(TAG, "Log headerMap $headerMap")
        val call = mainApiService?.updateTokenHandle(headerMap, params) as Call<Any?>?
        altCall = altApiService?.updateTokenHandle(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }


    fun cityHandle(country_id: Int, activity: Activity?) {
        var countryCode = ""
        val localModel = UtilityApp.getLocalData()
        countryCode = if (localModel.shortname != null) {
            localModel.shortname
        } else {
            GlobalData.COUNTRY
        }
        Log.i(TAG, "Log UtilityApp.getUrl()  ${UtilityApp.getUrl()}")
        var apiPath = ""
        apiPath = GlobalData.amour
        countryCode = Constants.default_amour_short_name

        val url = UtilityApp.getUrl() + countryCode + apiPath + "api/v9/Locations/citiesByCountry"
        lang = if (UtilityApp.getLanguage() != null) {
            UtilityApp.getLanguage()
        } else {
            Locale.getDefault().language
        }
        val params: MutableMap<String, Any> = HashMap()
        params["country_id"] = country_id
        val bodyRequest = BranchesByCountryRequest()
        bodyRequest.countryId = country_id
        Log.i(TAG, "Log CityHandle")
        Log.i(TAG, "Log url $url")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log country_id $country_id")
        Log.i(TAG, "Log lang $lang")

//        Call call = apiService.GetCity(url, headerMap, params);
        val call = mainApiService?.getCity(url, headerMap, bodyRequest) as Call<Any?>?
        altCall = altApiService?.getCity(url, headerMap, bodyRequest) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getCountryDetail(shortName: String?) {
        Log.i(TAG, "Log getCountryDetail")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log lan $lang")
        Log.i(TAG, "Log shortName $shortName")
        val params: MutableMap<String, Any?> = HashMap()
        lang = UtilityApp.getLanguage()
        if (lang != null) {
            params["lan"] = lang!!
        } else {
            params["lan"] = Locale.getDefault().language
        }
        params["shortname"] = shortName
        val call = mainApiService?.getCountryDetail(headerMap, params) as Call<Any?>?
        altCall = altApiService?.getCountryDetail(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun sendOpt(mobile_number: String) {
        Log.i(TAG, "Log sendOpt")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log mobile_number $mobile_number")
        val call = mainApiService?.getOptHandle(headerMap, mobile_number) as Call<Any?>?
        altCall = altApiService?.getOptHandle(headerMap, mobile_number) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun verifyOtpHandle(mobile_number: String, otp: String) {
        Log.i(TAG, "Log VerifyOtpHandle")
        Log.i(TAG, "Log VerifyOtpHandle headerMap $headerMap")
        Log.i(TAG, "Log VerifyOtpHandle mobile_number $mobile_number")
        Log.i(TAG, "Log VerifyOtpHandle  otp $otp")
        Log.i(TAG, "Log otp $otp")
        val otpModel = OtpModel()
        otpModel.mobileNumber = mobile_number
        otpModel.otp = otp
        val call = mainApiService?.otpVerifyUserHandle(headerMap, otpModel) as Call<Any?>?
        altCall = altApiService?.otpVerifyUserHandle(headerMap, otpModel) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getAddressHandle(user_id: Int?) {
        Log.i(TAG, "Log GetAddressHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id $user_id")
        val call = mainApiService?.getUserAddress(headerMap, user_id ?: 0) as Call<Any?>?
        altCall = altApiService?.getUserAddress(headerMap, user_id ?: 0) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun setDefaultAddress(user_id: Int, address_id: Int) {
        Log.i(TAG, "Log setDefaultAddress")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id $user_id")
        Log.i(TAG, "Log addressId $address_id")
        val call = mainApiService?.setDefaultAddress(headerMap, user_id, address_id) as Call<Any?>?
        altCall = altApiService?.setDefaultAddress(headerMap, user_id, address_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getAddressByIdHandle(address_id: Int) {
        Log.i(TAG, "Log GetAddressByIdHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log address_id $address_id")
        val call = mainApiService?.getAddressById(headerMap, address_id) as Call<Any?>?
        altCall = altApiService?.getAddressById(headerMap, address_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun createAddressHandle(addressModel: AddressModel) {
        Log.i(TAG, "Log CreateAddressHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log mobile_number " + addressModel.mobileNumber)
        Log.i(TAG, "Log user id  " + addressModel.userId)
        Log.i(TAG, "Log name " + addressModel.name)
        Log.i(TAG, "Log area id  " + addressModel.areaId)
        Log.i(TAG, "Log state id  " + addressModel.state)
        Log.i(TAG, "Log getBlock   " + addressModel.block)
        Log.i(TAG, "Log getStreetDetails  " + addressModel.streetDetails)
        Log.i(TAG, "Log getHouseNo  " + addressModel.houseNo)
        Log.i(TAG, "Log apartment_no  " + addressModel.apartmentNo)
        Log.i(TAG, "Log phone_prefix  " + addressModel.phonePrefix)
        Log.i(TAG, "Log mobile_number  " + addressModel.mobileNumber)
        Log.i(TAG, "Log longitude  " + addressModel.longitude)
        Log.i(TAG, "Log getLatitude  " + addressModel.latitude)
        Log.i(TAG, "Log google_address  " + addressModel.googleAddress)
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = addressModel.userId
        params["name"] = addressModel.name
        params["area_id"] = addressModel.areaId
        params["state_id"] = addressModel.state
        params["block"] = addressModel.block
        params["street_details"] = addressModel.streetDetails
        params["house_no"] = addressModel.houseNo
        params["apartment_no"] = addressModel.apartmentNo
        params["phone_prefix"] = addressModel.phonePrefix
        params["mobile_number"] = addressModel.mobileNumber
        params["longitude"] = addressModel.longitude
        params["latitude"] = addressModel.latitude
        params["google_address"] = addressModel.googleAddress
        val call = mainApiService?.createNewAddress(headerMap, params) as Call<Any?>?
        altCall = altApiService?.createNewAddress(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getAreasHandle(country_id: Int) {
        Log.i(TAG, "Log GetAreasHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log country_id $country_id")
        val call = mainApiService?.GetAreas(headerMap, country_id) as Call<Any?>?
        altCall = altApiService?.GetAreas(headerMap, country_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun deleteAddressHandle(address_id: Int) {
        Log.i(TAG, "Log deleteAddressHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log address_id $address_id")
        val call = mainApiService?.deleteAddress(headerMap, address_id) as Call<Any?>?
        altCall = altApiService?.deleteAddress(headerMap, address_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getSettings(countryId: Int) {
        Log.i(TAG, "Log settingCouponsModel")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log country_id $countryId")
        val call = mainApiService?.getSettings(headerMap, countryId) as Call<Any?>?
        altCall = altApiService?.getSettings(headerMap, countryId) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getTotalPoint(userId: Int) {
        Log.i(TAG, "Log getTotalPoint")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log userid $userId")
        val call = mainApiService?.getTotalPoint(headerMap, userId) as Call<Any?>?
        altCall = altApiService?.getTotalPoint(headerMap, userId) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getCoupons(userId: Int) {
        Log.i(TAG, "Log getCoupons")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log userid $userId")
        val call = mainApiService?.getCoupons(headerMap, userId) as Call<Any?>?
        altCall = altApiService?.getCoupons(headerMap, userId) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getTrans(userId: Int) {
        Log.i(TAG, "Log getTrans")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log userid $userId")
        val call = mainApiService?.getTrans(headerMap, userId) as Call<Any?>?
        altCall = altApiService?.getTrans(headerMap, userId) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun generateCoupon(userId: Int, points: Int) {
        val params: MutableMap<String, Any?> = HashMap()
        params["userid"] = userId
        params["points"] = points
        Log.i(TAG, "Log generateCoupon")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log userid $userId")
        Log.i(TAG, "Log points $points")
        val call = mainApiService?.generateCoupon(headerMap, params) as Call<Any?>?
        altCall = altApiService?.generateCoupon(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun GetMainPage(category_id: Int, country_id: Int, city_id: Int, user_id: String) {
        Log.i(TAG, "Log GetMainPage")
        Log.i(TAG, "Log GetMainPage headerMap $headerMap")
        Log.i(TAG, "Log GetMainPage country_id $country_id")
        Log.i(TAG, "Log GetMainPage category_id $category_id")
        Log.i(TAG, "Log GetMainPage user_id $user_id")
        Log.i(TAG, "Log GetMainPage city_id $city_id")
        val call =
            mainApiService?.getMainPage(headerMap, category_id, country_id, city_id, user_id) as Call<Any?>?
        altCall =
            altApiService?.getMainPage(headerMap, category_id, country_id, city_id, user_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getSliders(city_id: Int) {
        Log.i(TAG, "Log getSliders")
        Log.i(TAG, "Log getSliders headerMap $headerMap")
        Log.i(TAG, "Log getSliders city_id $city_id")
        val call = mainApiService?.getSliders(headerMap, city_id) as Call<Any?>?
        altCall = altApiService?.getSliders(headerMap, city_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun GetSingleProduct(country_id: Int, city_id: Int, product_id: Int, user_id: String) {
//        product_id = ;

        val params = mutableMapOf<String, Any?>().apply {
            this["country_id"] = country_id
            this["city_id"] = city_id
            this["product_id"] = product_id
            this["user_id"] = user_id

        }
        Log.i(TAG, "Log GetSingleProduct")
        Log.i(TAG, "Log  GetSingleProduct headerMap $headerMap")
        Log.i(TAG, "Log  GetSingleProduct country_id $country_id")
        Log.i(TAG, "Log  GetSingleProduct product_id $product_id")
        Log.i(TAG, "Log  GetSingleProduct user_id $user_id")
        Log.i(TAG, "Log  GetSingleProduct city_id $city_id")
        val call = mainApiService?.getSignalProducts(headerMap, params) as Call<Any?>?
        altCall = altApiService?.getSignalProducts(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun GetAllCategories(sotre_id: Int) {
        Log.i(TAG, "Log GetAllCategories")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log country_id $sotre_id")
        val call = mainApiService?.getAllCategories(headerMap, sotre_id) as Call<Any?>?
        altCall = altApiService?.getAllCategories(headerMap, sotre_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getAllKindsList() {
        Log.i(TAG, "Log getAllKindsList")
        Log.i(TAG, "Log headerMap $headerMap")
        val call = mainApiService?.getAllKindsList(headerMap) as Call<Any?>?
        altCall = altApiService?.getAllKindsList(headerMap) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun GetAllBrands(sotre_id: Int) {
        Log.i(TAG, "Log GetAllBrands")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log country_id $sotre_id")
        val call = mainApiService?.getAllBrands(headerMap, sotre_id) as Call<Any?>?
        altCall = altApiService?.getAllBrands(headerMap, sotre_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun addToFavoriteHandle(user_id: Int?, store_ID: Int?, product_id: Int?) {
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = user_id
        params["store_ID"] = store_ID
        params["product_id"] = product_id
        Log.i(TAG, "Log addToFavoriteHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id $user_id")
        Log.i(TAG, "Log store_ID $store_ID")
        Log.i(TAG, "Log product_id $product_id")
        val call = mainApiService?.addFavouriteProduct(headerMap, params) as Call<Any?>?
        altCall = altApiService?.addFavouriteProduct(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun deleteFromFavoriteHandle(user_id: Int, store_ID: Int, product_id: Int) {
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = user_id
        params["store_ID"] = store_ID
        params["product_id"] = product_id
        Log.i(TAG, "Log deleteFromFavoriteHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id $user_id")
        Log.i(TAG, "Log store_ID $store_ID")
        Log.i(TAG, "Log product_id $product_id")
        val call = mainApiService?.deleteFavouriteProduct(headerMap, params) as Call<Any?>?
        altCall = altApiService?.deleteFavouriteProduct(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun addCartHandle(productId: Int, product_barcode_id: Int, quantity: Int, userId: Int, storeId: Int) {
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = userId
        params["store_ID"] = storeId
        params["product_id"] = productId
        params["quantity"] = quantity
        params["product_barcode_id"] = product_barcode_id
        Log.i(TAG, "Log addCartHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id $userId")
        Log.i(TAG, "Log store_ID $storeId")
        Log.i(TAG, "Log product_id $productId")
        Log.i(TAG, "Log product_barcode_id $product_barcode_id")
        Log.i(TAG, "Log quantity $quantity")
        val call = mainApiService?.addToCart(headerMap, params) as Call<Any?>?
        altCall = altApiService?.addToCart(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun updateCartHandle(
        productId: Int,
        product_barcode_id: Int,
        quantity: Int,
        userId: Int,
        storeId: Int,
        cart_id: Int,
        update_quantity: String
    ) {
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = userId
        params["store_ID"] = storeId
        params["product_id"] = productId
        params["quantity"] = quantity
        params["cart_id"] = cart_id
        params["update_type"] = update_quantity
        params["product_barcode_id"] = product_barcode_id
        Log.i(TAG, "Log updateCartHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id $userId")
        Log.i(TAG, "Log cart_id $cart_id")
        Log.i(TAG, "Log store_ID $storeId")
        Log.i(TAG, "Log product_id $productId")
        Log.i(TAG, "Log product_barcode_id $product_barcode_id")
        Log.i(TAG, "Log quantity $quantity")
        val call = mainApiService?.updateCart(headerMap, params) as Call<Any?>?
        altCall = altApiService?.updateCart(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun deleteCartHandle(
        productId: Int?,
        product_barcode_id: Int?,
        cart_id: Int?,
        userId: Int?,
        storeId: Int?
    ) {
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = userId
        params["store_ID"] = storeId
        params["product_id"] = productId
        params["cart_id"] = cart_id
        params["product_barcode_id"] = product_barcode_id
        Log.i(TAG, "Log deleteCartHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id $userId")
        Log.i(TAG, "Log store_ID $storeId")
        Log.i(TAG, "Log product_id $productId")
        Log.i(TAG, "Log product_barcode_id $product_barcode_id")
        Log.i(TAG, "Log cart_id $cart_id")
        val call = mainApiService?.deleteCartItems(headerMap, params) as Call<Any?>?
        altCall = altApiService?.deleteCartItems(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun updateRemarkCartHandle(cart_id: Int, remark: String) {
        val params: MutableMap<String, Any?> = HashMap()
        params["cart_id"] = cart_id
        params["remark"] = remark
        Log.i(TAG, "Log updateRemarkCartHandle")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log remark $remark")
        Log.i(TAG, "Log cart_id $cart_id")
        val call = mainApiService?.updateRemark(headerMap, params) as Call<Any?>?
        altCall = altApiService?.updateRemark(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun GetCarts(sotre_id: Int, user_id: Int) {
        Log.i(TAG, "Log GetCarts")
        Log.i(TAG, "Log GetCarts headerMap $headerMap")
        Log.i(TAG, "Log GetCarts storeId  $sotre_id")
        Log.i(TAG, "Log GetCarts userId $user_id")
        val call = mainApiService?.getACarts(headerMap, user_id, sotre_id) as Call<Any?>?
        altCall = altApiService?.getACarts(headerMap, user_id, sotre_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getDeliveryTimeList(sotre_id: Int?, user_id: Int?) {
        Log.i(TAG, "Log checkCart")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log sotre_id $sotre_id")
        Log.i(TAG, "Log user_id $user_id")
        val call = mainApiService?.checkCart(headerMap, user_id, sotre_id) as Call<Any?>?
        altCall = altApiService?.checkCart(headerMap, user_id, sotre_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun updateProfile(memberModel: MemberModel?) {
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = memberModel?.id
        params["name"] = memberModel?.name
        params["email"] = memberModel?.email
        val country = if (memberModel?.country != null) memberModel.country else Constants.default_short_name
        params["country"] = country
        params["state"] = "1"
        params["city"] = memberModel?.city
        Log.i(TAG, "Log updateProfile")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log name " + memberModel?.name)
        Log.i(TAG, "Log email " + memberModel?.email)
        Log.i(TAG, "Log country " + memberModel?.country)
        Log.i(TAG, "Log city " + memberModel?.city)
        val call = mainApiService?.updateProfile(headerMap, params) as Call<Any?>?
        altCall = altApiService?.updateProfile(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }


    fun getUserDetails(user_id: Int, store_id: Int) {
        Log.i(TAG, "Log getUserDetails")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id $user_id")
        Log.i(TAG, "Log store_id $store_id")
        val call = mainApiService?.getUserDetail(headerMap, user_id, store_id) as Call<Any?>?
        altCall = altApiService?.getUserDetail(headerMap, user_id, store_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun barcodeSearch(
        country_id: Int,
        city_id: Int,
        user_id: String,
        filter: String,
        page_number: Int,
        page_size: Int
    ) {
        Log.i(TAG, "Log barcodeSearch")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log country_id $country_id")
        Log.i(TAG, "Log city_id $city_id")
        Log.i(TAG, "Log user_id $user_id")
        Log.i(TAG, "Log filter $filter")
        Log.i(TAG, "Log page_number $page_number")
        Log.i(TAG, "Log page_size $page_size")
        val call = mainApiService?.barcodeSearch(
            headerMap,
            country_id,
            city_id,
            user_id,
            filter,
            page_number,
            page_size
        ) as Call<Any?>?
        altCall = altApiService?.barcodeSearch(
            headerMap,
            country_id,
            city_id,
            user_id,
            filter,
            page_number,
            page_size
        ) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun addToFastQCart(city_id: Int, user_id: String, filter: String) {
        Log.i(TAG, "Log addToFastQCart")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log barcode $filter")
        Log.i(TAG, "Log city_id $city_id")
        Log.i(TAG, "Log user_id $user_id")
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = user_id
        params["barcode"] = filter
        params["store_id"] = city_id
        val call = mainApiService?.addToFastQCart(headerMap, params) as Call<Any?>?
        altCall = altApiService?.addToFastQCart(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun updateCartFastQ(id: Int, qty: Int, city_id: Int, user_id: String) {
        Log.i(TAG, "Log updateCartFastQ")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log id $id")
        Log.i(TAG, "Log qty $qty")
        Log.i(TAG, "Log user_id $user_id")
        Log.i(TAG, "Log store_id $city_id")
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = user_id
        params["id"] = id
        params["qty"] = qty
        params["store_id"] = city_id
        val call = mainApiService?.updateFastQCart(headerMap, params) as Call<Any?>?
        altCall = altApiService?.updateFastQCart(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getFastQCarts(city_id: Int, user_id: String) {
        Log.i(TAG, "Log getFastQCarts")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log store_id $city_id")
        Log.i(TAG, "Log user_id $user_id")
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = user_id
        params["store_id"] = city_id
        val call = mainApiService?.getFastQCarts(headerMap, params) as Call<Any?>?
        altCall = altApiService?.getFastQCarts(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun generateOrders(city_id: Int, user_id: String, Cashier: String) {
        Log.i(TAG, "Log generateOrders")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log store_id $city_id")
        Log.i(TAG, "Log user_id $user_id")
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = user_id
        params["store_id"] = city_id
        params["Cashier"] = Cashier
        val call = mainApiService?.generateOrders(headerMap, params) as Call<Any?>?
        altCall = altApiService?.generateOrders(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getFastQSetting(city_id: Int, user_id: String) {
        Log.i(TAG, "Log Get Fastq Setting")
        Log.i(TAG, "Log Get Fastq headerMap $headerMap")
        Log.i(TAG, "Log Get Fastq store_id $city_id")
        Log.i(TAG, "Log Get Fastq  user_id $user_id")
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = user_id
        params["store_id"] = city_id
        val call = mainApiService?.getFastQSetting(headerMap, params) as Call<Any?>?
        altCall = altApiService?.getFastQSetting(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getSettings() {
        Log.i(TAG, "Log getSetting")

        lang = UtilityApp.getLanguage()
        lang = if (lang != null) {
            UtilityApp.getLanguage()
        } else {
            Locale.getDefault().language
        }

        Log.i(TAG, "Log lang $lang")

        val call = mainApiService?.getSetting(headerMap, lang) as Call<Any?>?
        altCall = altApiService?.getSetting(headerMap, lang) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun searchTxt(
        country_id: Int,
        city_id: Int,
        user_id: String,
        filter: String,
        page_number: Int,
        page_size: Int
    ) {
        Log.i(TAG, "Log searchTxt")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log country_id $country_id")
        Log.i(TAG, "Log city_id $city_id")
        Log.i(TAG, "Log user_id $user_id")
        Log.i(TAG, "Log filter $filter")
        Log.i(TAG, "Log page_number $page_number")
        Log.i(TAG, "Log page_size $page_size")
        val call = mainApiService?.searchProduct(
            headerMap,
            country_id,
            city_id,
            user_id,
            filter,
            page_number,
            page_size
        ) as Call<Any?>?
        altCall = altApiService?.searchProduct(
            headerMap,
            country_id,
            city_id,
            user_id,
            filter,
            page_number,
            page_size
        ) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun autocomplete(
        country_id: Int,
        city_id: Int,
        user_id: String,
        text: String,
        page_number: Int,
        page_size: Int
    ): Call<*>? {
        Log.i(TAG, "Log autocomplete")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log country_id $country_id")
        Log.i(TAG, "Log city_id $city_id")
        Log.i(TAG, "Log user_id $user_id")
        Log.i(TAG, "Log text $text")
        Log.i(TAG, "Log page_number $page_number")
        Log.i(TAG, "Log page_size $page_size")
        val call = mainApiService?.autocomplete(headerMap, country_id, city_id, user_id, text) as Call<Any?>?
        altCall = altApiService?.autocomplete(headerMap, country_id, city_id, user_id, text) as Call<Any?>?
        call?.enqueue(callbackApi)
        return call
    }

    fun getCatProductList(
        category_id: Int,
        country_id: Int,
        city_id: Int,
        user_id: String,
        filter: String,
        page_number: Int,
        page_size: Int
    ) {
        Log.i(TAG, "Log getCatProductList")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log category_id $category_id")
        Log.i(TAG, "Log country_id $country_id")
        Log.i(TAG, "Log city_id $city_id")
        Log.i(TAG, "Log user_id $user_id")
        Log.i(TAG, "Log filter $filter")
        Log.i(TAG, "Log page_number $page_number")
        Log.i(TAG, "Log page_size $page_size")
        val call = mainApiService?.getCatProductList(
            headerMap,
            category_id,
            country_id,
            city_id,
            user_id,
            filter,
            page_number,
            page_size
        ) as Call<Any?>?
        altCall = altApiService?.getCatProductList(
            headerMap,
            category_id,
            country_id,
            city_id,
            user_id,
            filter,
            page_number,
            page_size
        ) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getProductList(productRequest: ProductRequest?): Call<*>? {
        //int kind_id, String srots, int category_id, int country_id, int city_id, String user_id, String filter, int brand_id, int page_number, int page_size

//        Map<String, Object> params = new HashMap<>();

//        FilterModel filterModel = new FilterModel();
//        filterModel.setKey("brand");
//        filterModel.setValue("1,4,24");
//        List<FilterModel> filtersList = new ArrayList<>();
//        filtersList.add(filterModel);


//        SortModel sortModel = new SortModel();
//        sortModel.setKey("price");
//        sortModel.setDescending(true);

//        List<SortModel> sortList = new ArrayList<>();
//        sortList.add(sortModel);


//        String sortJson = new Gson().toJson(sortList);

//        try {
//            JSONArray sortJsonArr = new JSONArray(sortJson);
//            params.put("srots", sortJsonArr);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.i(TAG, "Log sortJson " + sortJson);
//        Log.i(TAG, "Log filtersJson " + filtersJson);

//        params.put("category_id", productRequest.getCategoryId());
//        params.put("country_id", productRequest.getCountryId());
//        params.put("city_id", productRequest.getCityId());
//        params.put("filter", productRequest.getFilter());
//        params.put("brand_id", productRequest.getBrandId());
//        params.put("page_number", productRequest.getPageNumber());
//        params.put("page_size", productRequest.getPageSize());
//        params.put("kind_id", productRequest.getKindId());
//
//        if (productRequest.getFilters() != null && !productRequest.getFilters().isEmpty()) {
//            String filtersJson = new Gson().toJson(productRequest.getFilters());
//            System.out.println("Log filtersJson " + filtersJson);
//            try {
//                JSONArray filtersJsonArr = new JSONArray(filtersJson);
////                params.put("filters", filtersJsonArr);
//                params.put("filters", productRequest.getFilters());
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (productRequest.getSrots() != null && !productRequest.getSrots().isEmpty()) {
//            String sortJson = new Gson().toJson(productRequest.getSrots());
//            System.out.println("Log sortJson " + sortJson);
//            try {
//                JSONArray sortJsonArr = new JSONArray(sortJson);
////                params.put("srots", sortJsonArr);
//                params.put("srots", productRequest.getSrots());
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        val paramsJson = Gson().toJson(productRequest)
        Log.i(TAG, "Log getProductList")
        Log.i(TAG, "Log getProductList headerMap $headerMap")
        //        Log.i(TAG, "Log params " + params);
        println("Log paramsJson $paramsJson")
        val call = mainApiService?.getProductsList(headerMap, productRequest) as Call<Any?>?
        altCall = altApiService?.getProductsList(headerMap, productRequest) as Call<Any?>?
        call?.enqueue(callbackApi)
        return call
    }

    fun getPastOrders(user_id: Int) {
        Log.i(TAG, "Log getPastOrders")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id $user_id")
        val call = mainApiService?.getPastOrders(headerMap, user_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getUpcomingOrders(user_id: Int) {
        Log.i(TAG, "Log getUpcomingOrders")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id $user_id")
        val call = mainApiService?.getUpcomingOrders(headerMap, user_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getPaymentMethod(sotre_id: Int) {
        Log.i(TAG, "Log getPaymentMethod")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log sotre_id $sotre_id")
        val call = mainApiService?.getPaymentMethod(headerMap, sotre_id) as Call<Any?>?
        altCall = altApiService?.getPaymentMethod(headerMap, sotre_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun makeOrder(orderCalls: OrderCall) {
        Log.i(TAG, "Log makeOrder")
        Log.i(TAG, "Log headerMap $headerMap")
        //        Log.i(TAG, "Log user_id " + orderCalls.user_id);
        Log.i(TAG, "Log store_ID " + orderCalls.store_ID)
        Log.i(TAG, "Log addressId " + orderCalls.address_id)
        Log.i(TAG, "Log payment_method " + orderCalls.payment_method)
        Log.i(TAG, "Log coupon_code_id " + orderCalls.coupon_code_id)
        Log.i(TAG, "Log delivery_date_id " + orderCalls.delivery_date_id)
        Log.i(TAG, "Log expressDelivery " + orderCalls.expressDelivery)
        Log.i(TAG, "Log itemNotFoundAction " + orderCalls.itemNotFoundAction)
        Log.i(TAG, "Log payToken " + orderCalls.pay_token)
        Log.i(TAG, "Log delivery_type " + orderCalls.delivery_type)
        Log.i(TAG, "Log delivery_type " + orderCalls.delivery_type)
        Log.i(TAG, "Log pay_token " + orderCalls.pay_token)
        val call = mainApiService?.makeOrder(headerMap, orderCalls) as Call<Any?>?
        altCall = altApiService?.makeOrder(headerMap, orderCalls) as Call<Any?>?
        call?.enqueue(callbackApi)
    }


    fun getQuickDelivery(quickCall: QuickCall) {
//        val quickCall1 = QuickCall()
//        quickCall1.store_id = quickCall.store_id
//        quickCall1.country_id = quickCall.country_id
        Log.i(TAG, "Log getQuickDelivery")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log store_id " + quickCall.store_id)
        Log.i(TAG, "Log country_id " + quickCall.country_id)
        val call = mainApiService?.getQuickDelivery(headerMap, quickCall) as Call<Any?>?
        altCall = altApiService?.getQuickDelivery(headerMap, quickCall) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getProductList(
        category_id: Int,
        country_id: Int,
        city_id: Int,
        user_id: String,
        filter: String,
        page_number: Int,
        page_size: Int
    ) {
        Log.i(TAG, "Log getProductList")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log category_id $category_id")
        Log.i(TAG, "Log country_id $country_id")
        Log.i(TAG, "Log city_id $city_id")
        Log.i(TAG, "Log user_id $user_id")
        Log.i(TAG, "Log filter $filter")
        Log.i(TAG, "Log page_number $page_number")
        Log.i(TAG, "Log page_size $page_size")
        val call = mainApiService?.getProductList(
            headerMap,
            category_id,
            country_id,
            city_id,
            user_id,
            filter,
            page_number,
            page_size
        ) as Call<Any?>?
        altCall = altApiService?.getProductList(
            headerMap,
            category_id,
            country_id,
            city_id,
            user_id,
            filter,
            page_number,
            page_size
        ) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getRate(productId: Int, store_id: Int) {
        val params: MutableMap<String, Any?> = HashMap()
        params["store_id"] = store_id
        params["product_id"] = productId
        Log.i(TAG, "Log getRate")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log store_ID $store_id")
        Log.i(TAG, "Log product_id $productId")
        val call = mainApiService?.getRates(headerMap, params) as Call<Any?>?
        altCall = altApiService?.getRates(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getProductRecipeList(
        recipe_id: Int,
        country_id: Int,
        city_id: Int,
        user_id: String,
        page_number: Int,
        page_size: Int
    ) {
        Log.i(TAG, "Log getProductRecipeList")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log recipe_id $recipe_id")
        Log.i(TAG, "Log country_id $country_id")
        Log.i(TAG, "Log city_id $city_id")
        Log.i(TAG, "Log user_id $user_id")
        Log.i(TAG, "Log page_number $page_number")
        Log.i(TAG, "Log page_size $page_size")
        val call = mainApiService?.getProductRecipeLis(
            headerMap,
            recipe_id,
            country_id,
            city_id,
            user_id,
            page_number,
            page_size
        ) as Call<Any?>?
        altCall = altApiService?.getProductRecipeLis(
            headerMap,
            recipe_id,
            country_id,
            city_id,
            user_id,
            page_number,
            page_size
        ) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getBrochuresList(store_id: Int, booklet_id: Int) {
        Log.i(TAG, "Log getBrochuresList")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log store_ID $store_id")
        Log.i(TAG, "Log booklet_id $booklet_id")
        val call = mainApiService?.getBrochuresList(headerMap, store_id, booklet_id) as Call<Any?>?
        altCall = altApiService?.getBrochuresList(headerMap, store_id, booklet_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getBookletsList(store_id: Int) {
        Log.i(TAG, "Log getBookletsList")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log store_ID $store_id")
        val call = mainApiService?.getBookletsList(headerMap, store_id) as Call<Any?>?
        altCall = altApiService?.getBookletsList(headerMap, store_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getDinnersList(lang: String) {
        Log.i(TAG, "Log getDinnersList")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log lang $lang")
        val call = mainApiService?.getDinnersList(headerMap, lang) as Call<Any?>?
        altCall = altApiService?.getDinnersList(headerMap, lang) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getSingleDinner(dinner_id: Int, lan: String) {
        Log.i(TAG, "Log getSingleDinner")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log lang $lan")
        Log.i(TAG, "Log dinner_id $dinner_id")
        val call = mainApiService?.getSingleDinner(headerMap, dinner_id, lan) as Call<Any?>?
        altCall = altApiService?.getSingleDinner(headerMap, dinner_id, lan) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    val appRate: Unit
        get() {
            val params: MutableMap<String, Any?> = HashMap()
            Log.i(TAG, "Log getAppRate")
            Log.i(TAG, "Log headerMap $headerMap")
            val call = mainApiService?.getAppRate(headerMap, params) as Call<Any?>?
            call?.enqueue(callbackApi)
        }

    fun setRate(reviewModel: ReviewModel) {
        val params: MutableMap<String, Any?> = HashMap()
        params["store_id"] = reviewModel.storeId
        params["user_id"] = reviewModel.user_id
        params["product_id"] = reviewModel.productId
        params["rate"] = reviewModel.rate
        params["comment"] = reviewModel.comment
        Log.i(TAG, "Log setRate")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log store_ID " + reviewModel.storeId)
        Log.i(TAG, "Log product_id " + reviewModel.productId)
        Log.i(TAG, "Log rate " + reviewModel.rate)
        Log.i(TAG, "Log comment " + reviewModel.comment)
        val call = mainApiService?.setRate(headerMap, params) as Call<Any?>?
        altCall = altApiService?.setRate(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }


    fun saveTransaction(transaction: Transaction?, orderId: Int, trackid: String) {
        val params: MutableMap<String, Any?> = HashMap()
        params["terminalId"] = transaction?.terminalId
        params["cardNumber"] = transaction?.cardNumber
        params["transactionMessage"] = transaction?.transactionMessage
        params["referenceNumber"] = transaction?.referenceNumber
        params["amount"] = transaction?.amount
        params["order_id"] = orderId
        params["trackid"] = trackid
        params["app_id"] = Constants.appId
        Log.i(TAG, "Log saveTransactionData")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log params saveTransaction $params")
        val call = mainApiService?.payStatus(headerMap, params) as Call<Any?>?
        altCall = altApiService?.payStatus(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }


    fun setAppRate(reviewModel: ReviewModel) {
        val params: MutableMap<String, Any?> = HashMap()
        params["user_id"] = reviewModel.user_id
        params["rate"] = reviewModel.rate
        params["comment"] = reviewModel.comment
        Log.i(TAG, "Log setAppRate")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log user_id " + reviewModel.user_id)
        Log.i(TAG, "Log rate " + reviewModel.rate)
        Log.i(TAG, "Log comment " + reviewModel.comment)
        val call = mainApiService?.setAppRate(headerMap, params) as Call<Any?>?
        altCall = altApiService?.setAppRate(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    val setting: Unit
        get() {
            Log.i(TAG, "Log getSetting")
            lang = UtilityApp.getLanguage()
            lang = if (lang != null) {
                UtilityApp.getLanguage()
            } else {
                Locale.getDefault().language
            }
            Log.i(TAG, "Log lang $lang")
            val call = mainApiService?.getSetting(headerMap, lang) as Call<Any?>?
            altCall = altApiService?.getSetting(headerMap, lang) as Call<Any?>?
            call?.enqueue(callbackApi)
        }

    fun getValidate(device_type: String?, app_version: String, app_build: Int) {
        Log.i(TAG, "Log getValidate")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log app_version $app_version")
        Log.i(TAG, "Log app_build $app_build")
        val call = mainApiService?.getValidate(headerMap, device_type, app_version, app_build) as Call<Any?>?
        altCall = altApiService?.getValidate(headerMap, device_type, app_version, app_build) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getLinks(store_id: Int) {
        Log.i(TAG, "Log getLinks")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log store_id $store_id")
        val call = mainApiService?.getSocialLink(headerMap, store_id) as Call<Any?>?
        altCall = altApiService?.getSocialLink(headerMap, store_id) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getOrders(user_id: Int, type: String, filter: String) {
        Log.i(TAG, "Log getOrders")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log type $type")
        Log.i(TAG, "Log filter $filter")
        Log.i(TAG, "Log user_id $user_id")
        val orderListCall = orderListCall()
        orderListCall.userId = user_id
        orderListCall.type = type
        orderListCall.filter = filter
        val call = mainApiService?.getOrdersList(headerMap, orderListCall) as Call<Any?>?
        altCall = altApiService?.getOrdersList(headerMap, orderListCall) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun getOrderDetails(order_id: Int, user_id: Int, store_id: Int, type: String) {
        Log.i(TAG, "Log getOrderDetails")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log type $type")
        Log.i(TAG, "Log store_id $store_id")
        Log.i(TAG, "Log store_id $order_id")
        Log.i(TAG, "Log user_id $user_id")
        val orderListCall = orderListCall()
        orderListCall.orderId = order_id
        orderListCall.userId = user_id
        orderListCall.storeId = store_id
        orderListCall.type = type
        val call = mainApiService?.getOrderDetails(headerMap, orderListCall) as Call<Any?>?
        altCall = altApiService?.getOrderDetails(headerMap, orderListCall) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

    fun GetDeliveryInfo(store_ID: Int, address_id: Int?) {
        val params: MutableMap<String, Any?> = HashMap()
        params["address_id"] = address_id
        params["store_ID"] = store_ID
        Log.i(TAG, "Log GetDeliveryInfo")
        Log.i(TAG, "Log headerMap $headerMap")
        Log.i(TAG, "Log address_id $address_id")
        Log.i(TAG, "Log store_ID $store_ID")
        val call = mainApiService?.getDeliveryInfo(headerMap, params) as Call<Any?>?
        altCall = altApiService?.getDeliveryInfo(headerMap, params) as Call<Any?>?
        call?.enqueue(callbackApi)
    }

}