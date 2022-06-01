package com.amour.shop.ApiHandler

import com.amour.shop.Models.*
import com.amour.shop.Models.request.BranchesByCountryRequest
import com.amour.shop.Models.request.ProductRequest
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {
    @POST("v9/Account/userRegister")
    fun registerUserHandle(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<RegisterResultModel?>?

    @POST("v9/Account/driverRegister")
    fun registerDriverHandle(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<LoginResultModel?>?

    @POST("v9/Account/login")
    fun loginUserHandle(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<LoginResultModel?>?

    @POST("v9/Account/getotp")
    fun getOptHandle(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("mobile_number") mobile_number: String?
    ): Call<OtpModel?>?

    @POST("v9/Account/refresh-token")
    fun refreshToken(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<ResultAPIModel<RefreshTokenModel?>?>?

    @POST("v9/Account/getUserDetail")
    fun getUserDetail(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("user_id") user_id: Int,
        @Query("store_id") store_id: Int
    ): Call<ResultAPIModel<ProfileData?>?>?

    @POST("v9/Account/forgotPassword")
    fun ForgetPasswordHandle(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<OtpModel?>?

    @POST("v9/Account/changePassword")
    fun changePasswordHandle(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<ResultAPIModel<RefreshTokenModel?>?>?

    @POST("v9/Account/updatePassword")
    fun updatePasswordHandle(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<GeneralModel?>?

    @POST("v9/Account/updateDeviceToken2")
    fun updateTokenHandle(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<ResultAPIModel<String?>?>?

    @POST("v9/BenefitPay/PaySyccess")
    fun PaySuccess(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<ResultAPIModel<String?>?>?

    @POST("v9/BenefitPay/PayError")
    fun PayError(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<ResultAPIModel<String?>?>?

    @POST("v9/BenefitPay2/PayStatus")
    fun payStatus(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<ResultAPIModel<GeneralModel?>?>?

    @POST("v9/Account/otpVerify")
    fun otpVerifyUserHandle(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body param: OtpModel?
    ): Call<GeneralModel?>?

    @POST("v9/Loayl/GetSettings")
    fun getSettings(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("country_id") country_id: Int
    ): Call<ResultAPIModel<SettingCouponsModel?>?>?

    @POST("v9/Loayl/GetTotalPoint")
    fun getTotalPoint(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("userid") userId: Int
    ): Call<ResultAPIModel<TotalPointModel?>?>?

    @POST("v9/Loayl/GetCoupons")
    fun getCoupons(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("userid") userId: Int
    ): Call<ResultAPIModel<List<CouponsModel?>?>?>?

    @POST("v9/Loayl/GetTrans")
    fun getTrans(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("userid") userId: Int
    ): Call<ResultAPIModel<List<TransactionModel?>?>?>?

    @POST("v9/Loayl/GenerateCoupon")
    fun generateCoupon(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @QueryMap queryparams: MutableMap<String, Any?>
    ): Call<GeneralModel?>?

    @POST("v9/Locations/countryList")
    fun GetCountry(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<CountryModelResult?>?

    @POST("v9/Locations/storedetails")
    fun getQuickDelivery(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body param: QuickCall?
    ): Call<ResultAPIModel<QuickDeliveryRespond?>?>?

    @POST
    fun   getCity(
        @Url url: String?,
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: BranchesByCountryRequest?
    ): Call<CityModelResult?>?

    @POST("v9/Products/GetRates")
    fun getRates(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<ResultAPIModel<ArrayList<ReviewModel?>?>?>?

    @POST("v9/Dinners/DinnersList")
    fun getDinnersList(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("lan") lan: String?
    ): Call<ResultAPIModel<ArrayList<DinnerModel?>?>?>?

    @POST("v9/Dinners/Dinner")
    fun getSingleDinner(
        @HeaderMap headerParams: MutableMap<String, Any>, @Query("dinner_id") dinner_id: Int,
        @Query("lan") lan: String?
    ): Call<ResultAPIModel<SingleDinnerModel?>?>?

    @POST("v9/Booklets/BookletsList")
    fun getBookletsList(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("store_id") store_id: Int
    ): Call<ResultAPIModel<ArrayList<BookletsModel?>?>?>?

    @POST("v9/Booklets/BrochuresList")
    fun getBrochuresList(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("store_id") sotre_id: Int,
        @Query("booklet_id") booklet_id: Int
    ): Call<ResultAPIModel<ArrayList<BrochuresModel?>?>?>?

    @POST("v9/Company/setrate")
    fun setAppRate(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<ResultAPIModel<ReviewModel?>?>?

    @POST("v9/Company/GetRates")
    fun getAppRate(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<ResultAPIModel<ArrayList<ReviewModel?>?>?>?

    @POST("v9/Address/createNewAddress")
    fun createNewAddress(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<AddressResultModel?>?

    @POST("v9/Favourite/addFavouriteProduct")
    fun addFavouriteProduct(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<GeneralModel?>?

    @POST("v9/Favourite/deleteFavouriteProduct")
    fun deleteFavouriteProduct(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<GeneralModel?>?

    @POST("v9/Carts/addToCart")
    fun addToCart(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<CartProcessModel?>?

    @POST("v9/Carts/AddExtrat")
    fun AddExtrat(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("qty") qty: Int,
        @Query("barcode") barcode: String?,
        @Query("description") description: String?,
        @Query("user_id") user_id: Int,
        @Query("store_id") store_id: Int,
        @Body params: RequestBody?
    ): Call<AddExtraResponse?>?

    @POST("v9/Carts/deleteCartItems")
    fun deleteCartItems(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<CartProcessModel?>?

    @POST("v9/Carts/updateRemark")
    fun updateRemark(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<CartProcessModel?>?

    @POST("v9/Carts/updateCart")
    fun updateCart(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<CartProcessModel?>?

    @POST("v9/Account/updateProfile")
    fun updateProfile(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<LoginResultModel?>?

    @POST("v9/Account/UploadPhoto")
    fun uploadPhoto(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: RequestBody?,
        @Query("user_id") user_id: Int
    ): Call<ResultAPIModel<GeneralModel?>?>?

    @POST("v9/Products/setrate")
    fun setRate(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body params: MutableMap<String, Any?>
    ): Call<ResultAPIModel<ReviewModel?>?>?

    @POST("v9/Orders/CreateOrder")
    fun makeOrder(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body param: OrderCall?
    ): Call<OrdersResultModel?>?

    @POST("v9/Orders/GetOrdersList")
    fun getOrdersList(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body param: orderListCall?
    ): Call<OrderResultModel?>?

    @POST("v9/Orders/GetOrderDetails")
    fun getOrderDetails(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body param: orderListCall?
    ): Call<ResultAPIModel<ItemDetailsModel?>?>?

    @POST("v9/ScanAndGo/GetItem")
    fun updateFastQCart(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @QueryMap queryparams: MutableMap<String, Any?>
    ): Call<ResultAPIModel<ScanModel?>?>?

    @POST("v9/ScanAndGo/GetCarts")
    fun getFastQCarts(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @QueryMap queryparams: MutableMap<String, Any?>
    ): Call<ResultAPIModel<ArrayList<CartFastQModel?>?>?>?

    @POST("v9/ScanAndGo/GenerateOrders")
    fun generateOrders(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @QueryMap queryparams: MutableMap<String, Any?>
    ): Call<ResultAPIModel<String?>?>?

    /* -------------------------Get Handle ------------------------- */
    @GET("v9/Locations/getCountryDetail")
    fun getCountryDetail(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @QueryMap params: MutableMap<String, Any?>
    ): Call<ResultAPIModel<CountryDetailsModel?>?>?

    @GET("v9/Locations/getSocialLink")
    fun getSocialLink(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("store_id") store_id: Int
    ): Call<ResultAPIModel<SoicalLink?>?>?

    @GET("v9/Locations/getValidate")
    fun getValidate(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("device_type") device_type: String?,
        @Query("app_version") app_version: String?,
        @Query("app_build") app_build: Int
    ): Call<GeneralModel?>?

    @GET("v9/Locations/getAreas")
    fun GetAreas(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("country_id") country_id: Int
    ): Call<AreasResultModel?>?

    @GET("v9/Address/getUserAddress")
    fun getUserAddress(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("user_id") user_id: Int
    ): Call<AddressResultModel?>?

    @GET("v9/Address/setDefaultAddress")
    fun setDefaultAddress(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("user_id") user_id: Int,
        @Query("address_id") address_id: Int
    ): Call<GeneralModel?>?

    @GET("v9/Address/getAddressById")
    fun getAddressById(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("address_id") address_id: Int
    ): Call<AddressResultModel?>?

    @GET("v9/Address/deleteAddress")
    fun deleteAddress(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("address_id") address_id: Int
    ): Call<AddressResultModel?>?

    @GET("v9/Account/logout")
    fun logout(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("user_id") user_id: Int,
        @Query("user_type") user_type: String?
    ): Call<ResultAPIModel<MemberModel?>?>?

    @GET("v9/Products/singleproductList")
    fun getSignalProducts(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @QueryMap queryParams: MutableMap<String, Any?>,
//        @Query("country_id") country_id: Int,
//        @Query("city_id") city_id: Int,
//        @Query("product_id") product_id: Int,
//        @Query("user_id") user_id: String?
    ): Call<ProductDetailsModel?>?

    @GET("v9/Products/categoryList")
    fun getMainPage(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("category_id") category_id: Int,
        @Query("country_id") country_id: Int,
        @Query("city_id") city_id: Int,
        @Query("user_id") user_id: String?
    ): Call<MainModel?>?

    @GET("v9/Sliders/getSliders")
    fun getSliders(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("sotre_id") sotre_id: Int,
    ): Call<ResultAPIModel<ArrayList<Slider?>?>?>?


    @GET("v9/Products/AllKinds")
    fun getAllKindsList(@HeaderMap headerParams: MutableMap<String, Any>): Call<ResultAPIModel<ArrayList<KindCategoryModel?>?>?>?

    @GET("v9/Products/AllCategories")
    fun getAllCategories(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("sotre_id") sotre_id: Int
    ): Call<CategoryResultModel?>?

    @GET("v9/Products/productRecipeList")
    fun getProductRecipeLis(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("recipe_id") recipe_id: Int,
        @Query("country_id") country_id: Int,
        @Query("city_id") city_id: Int,
        @Query("user_id") user_id: String?,
        @Query("page_number") page_number: Int,
        @Query("page_size") page_size: Int
    ): Call<ResultAPIModel<ArrayList<ProductModel?>?>?>?

    @GET("v9/Products/allbrands")
    fun getAllBrands(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("sotre_id") sotre_id: Int
    ): Call<ResultAPIModel<ArrayList<BrandModel?>?>?>?

    @GET("v9/Company/AboutAs")
    fun getSetting(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("lng") lng: String?
    ): Call<ResultAPIModel<SettingModel?>?>?

    // change version
    @GET("v9/Carts/checkOut")
    fun getACarts(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("user_id") user_id: Int,
        @Query("store_ID") store_ID: Int
    ): Call<CartResultModel?>?

    @GET("v9/Orders/checkOut")
    fun checkCart(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("user_id") user_id: Int?, @Query("store_ID") sotre_id: Int?
    ): Call<CheckOrderResponse?>?

    @POST("v9/Products/productList")
    fun getProductsList(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Body bodyParams: ProductRequest?
    ): Call<FavouriteResultModel?>?

    @GET("v9/Products/search")
    fun searchProduct(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("country_id") country_id: Int,
        @Query("city_id") city_id: Int,
        @Query("user_id") user_id: String?,
        @Query("text") text: String?,
        @Query("page_number") page_number: Int,
        @Query("page_size") page_size: Int
    ): Call<FavouriteResultModel?>?

    @GET("v9/Products/barcodeSearch")
    fun barcodeSearch(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("country_id") country_id: Int,
        @Query("city_id") city_id: Int,
        @Query("user_id") user_id: String?,
        @Query("barcode") barcode: String?,
        @Query("page_number") page_number: Int,
        @Query("page_size") page_size: Int
    ): Call<FavouriteResultModel?>?

    @GET("v9/Products/autocomplete")
    fun autocomplete(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("country_id") country_id: Int,
        @Query("city_id") city_id: Int,
        @Query("user_id") user_id: String?,
        @Query("text") text: String?
    ): Call<AutoCompeteResult?>?

    @GET("v9/Products/productList")
    fun getCatProductList(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("category_id") category_id: Int,
        @Query("country_id") country_id: Int,
        @Query("city_id") city_id: Int,
        @Query("user_id") user_id: String?,
        @Query("filter") filter: String?,
        @Query("page_number") page_number: Int,
        @Query("page_size") page_size: Int
    ): Call<FavouriteResultModel?>?

    @GET("v9/Orders/getPastOrders")
    fun getPastOrders(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("user_id") user_id: Int
    ): Call<OrdersResultModel?>?

    @GET("v9/Orders/GetDeliveryInfo")
    fun getDeliveryInfo(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @QueryMap queryparams: MutableMap<String, Any?>
    ): Call<DeliveryInfo?>?

    //    Call<ResultAPIModel<DeliveryInfo>> GetDeliveryInfo(@HeaderMap() MutableMap<String, Object> headerParams, @QueryMap MutableMap<String, Object> queryParams);
    @GET("v9/Orders/getUpcomingOrders")
    fun getUpcomingOrders(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("address_id") user_id: Int
    ): Call<OrdersResultModel?>?

    @GET("v9/Orders/GetOrderDelivery")
    fun getOrderDelivery(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("user_id") user_id: Int
    ): Call<OrdersResultModel?>?

    @GET("v9/Orders/getPaymentMethod")
    fun getPaymentMethod(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("sotre_id") sotre_id: Int
    ): Call<PaymentResultModel?>?

    @GET("v9/Products/productList")
    fun getProductList(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @Query("category_id") category_id: Int,
        @Query("country_id") country_id: Int,
        @Query("city_id") city_id: Int,
        @Query("user_id") user_id: String?,
        @Query("filter") filter: String?,
        @Query("page_number") page_number: Int,
        @Query("page_size") page_size: Int
    ): Call<FavouriteResultModel?>?

    @GET("v9/ScanAndGo/GetItem")
    fun addToFastQCart(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @QueryMap queryparams: MutableMap<String, Any?>
    ): Call<ScanResult?>?

    @GET("v9/ScanAndGo/GetSetting")
    fun getFastQSetting(
        @HeaderMap headerParams: MutableMap<String, Any>,
        @QueryMap queryParams: MutableMap<String, Any?>
    ): Call<ResultAPIModel<FastValidModel?>?>?
}



