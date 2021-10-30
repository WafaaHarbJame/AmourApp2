package com.ramez.shopp.ApiHandler;


import com.ramez.shopp.Classes.AddExtraResponse;
import com.ramez.shopp.Classes.CityModelResult;
import com.ramez.shopp.Classes.DeliveryInfo;
import com.ramez.shopp.Classes.FastValidModel;
import com.ramez.shopp.Classes.OtpModel;
import com.ramez.shopp.Classes.SettingModel;
import com.ramez.shopp.Classes.SoicalLink;
import com.ramez.shopp.Classes.orderListCall;
import com.ramez.shopp.Models.AddressResultModel;
import com.ramez.shopp.Models.AreasResultModel;
import com.ramez.shopp.Models.AutoCompeteResult;
import com.ramez.shopp.Models.BookletsModel;
import com.ramez.shopp.Models.BrandModel;
import com.ramez.shopp.Models.BrochuresModel;
import com.ramez.shopp.Models.CartFastQModel;
import com.ramez.shopp.Models.CartProcessModel;
import com.ramez.shopp.Models.CartResultModel;
import com.ramez.shopp.Models.CategoryResultModel;
import com.ramez.shopp.Models.CheckOrderModel;
import com.ramez.shopp.Models.CheckOrderResponse;
import com.ramez.shopp.Models.CountryDetailsModel;
import com.ramez.shopp.Models.CountryModelResult;
import com.ramez.shopp.Models.CouponsModel;
import com.ramez.shopp.Models.DeliveryResultModel;
import com.ramez.shopp.Models.DinnerModel;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.GeneralModel;
import com.ramez.shopp.Models.ItemDetailsModel;
import com.ramez.shopp.Models.LoginResultModel;
import com.ramez.shopp.Models.MainModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.OrderCall;
import com.ramez.shopp.Models.OrderNewModel;
import com.ramez.shopp.Models.OrderResultModel;
import com.ramez.shopp.Models.OrdersResultModel;
import com.ramez.shopp.Models.PaymentResultModel;
import com.ramez.shopp.Models.ProductDetailsModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.Models.ProfileData;
import com.ramez.shopp.Models.QuickCall;
import com.ramez.shopp.Models.QuickDeliveryRespond;
import com.ramez.shopp.Models.RegisterResultModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.ReviewModel;
import com.ramez.shopp.Models.ScanModel;
import com.ramez.shopp.Models.ScanResult;
import com.ramez.shopp.Models.SettingCouponsModel;
import com.ramez.shopp.Models.SingleDinnerModel;
import com.ramez.shopp.Models.TotalPointModel;
import com.ramez.shopp.Models.TransactionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;


public interface

ApiInterface {

    @POST("v8/Account/userRegister")
    Call<RegisterResultModel> registerUserHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v8/Account/driverRegister")
    Call<LoginResultModel> registerDriverHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v8/Account/login")
    Call<LoginResultModel> loginUserHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v8/Account/getotp")
    Call<OtpModel> GetOptHandle(@HeaderMap() Map<String, Object> headerParams, @Query("mobile_number") String mobile_number);

    @POST("v8/Account/getUserDetail")
    Call<ResultAPIModel<ProfileData>> getUserDetail(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id, @Query("store_id") int store_id);

    @POST("v8/Account/forgotPassword")
    Call<OtpModel> ForgetPasswordHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v8/Account/changePassword")
    Call<OtpModel> changePasswordHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @POST("v8/Account/updatePassword")
    Call<GeneralModel> updatePasswordHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @POST("v8/Account/updateDeviceToken2")
    Call<ResultAPIModel<String>> UpdateTokenHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @POST("v8/Account/otpVerify")
    Call<GeneralModel> otpVerifyUserHandle(@HeaderMap() Map<String, Object> headerParams, @Body OtpModel param);

    /* ------------------------- Address Handle ------------------------- */

    /* -------------------------Start Loayl Handle ------------------------- */

    @POST("v8/Loayl/GetSettings")
    Call<ResultAPIModel<SettingCouponsModel>> getSettings(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id);

    @POST("v8/Loayl/GetTotalPoint")
    Call<ResultAPIModel<TotalPointModel>> getTotalPoint(@HeaderMap() Map<String, Object> headerParams, @Query("userid") int userId);

    @POST("v8/Loayl/GetCoupons")
    Call<ResultAPIModel<List<CouponsModel>>> getCoupons(@HeaderMap() Map<String, Object> headerParams, @Query("userid") int userId);

    @POST("v8/Loayl/GetTrans")
    Call<ResultAPIModel<List<TransactionModel>>> getTrans(@HeaderMap() Map<String, Object> headerParams, @Query("userid") int userId);

    @POST("v8/Loayl/GenerateCoupon")
    Call<GeneralModel> generateCoupon(@HeaderMap() Map<String, Object> headerParams, @QueryMap Map<String, Object> queryParams);

    /* -------------------------end Loayl Handle ------------------------- */

    @POST("v8/Locations/countryList")
    Call<CountryModelResult> GetCountry(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @GET("v8/Locations/getCountryDetail")
    Call<ResultAPIModel<CountryDetailsModel>> getCountryDetail(@HeaderMap() Map<String, Object> headerParams,
                                                               @QueryMap Map<String, Object> params);


    @GET("v8/Locations/getSocialLink")
    Call<ResultAPIModel<SoicalLink>> getSocialLink(@HeaderMap() Map<String, Object> headerParams,
                                                   @Query("store_id") int store_id);

    @GET("v8/Locations/getValidate")
    Call<GeneralModel> getValidate(@HeaderMap() Map<String, Object> headerParams, @Query("device_type") String device_type, @Query("app_version") String app_version, @Query("app_build") int app_build);

    @POST("v8/Locations/storedetails")
    Call<ResultAPIModel<QuickDeliveryRespond>> getQuickDelivery(@HeaderMap() Map<String, Object> headerParams, @Body QuickCall param);

    @POST
    Call<CityModelResult> GetCity(@Url String url, @HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @GET("v8/Locations/getAreas")
    Call<AreasResultModel> GetAreas(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id);

    @GET("v8/Address/getUserAddress")
    Call<AddressResultModel> GetUserAddress(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id);

    @GET("v8/Address/setDefaultAddress")
    Call<GeneralModel> setDefaultAddress(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id, @Query("address_id") int address_id);

    @GET("v8/Address/getAddressById")
    Call<AddressResultModel> GetAddressById(@HeaderMap() Map<String, Object> headerParams, @Query("address_id") int address_id);

    @POST("v8/Address/createNewAddress")
    Call<AddressResultModel> CreateNewAddress(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @GET("v8/Address/deleteAddress")
    Call<AddressResultModel> deleteAddress(@HeaderMap() Map<String, Object> headerParams, @Query("address_id") int address_id);


    @GET("v8/Account/logout")
    Call<ResultAPIModel<MemberModel>> logout(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id, @Query("user_type") String user_type);


    @GET("v8/Products/singleproductList")
    Call<ProductDetailsModel> GetSignalProducts(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("product_id") int product_id, @Query("user_id") String user_id);

    @GET("v8/Products/categoryList")
    Call<MainModel> GetMainPage(@HeaderMap() Map<String, Object> headerParams, @Query("category_id") int category_id, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id);

    @GET("v8/Products/AllCategories")
    Call<CategoryResultModel> GetAllCategories(@HeaderMap() Map<String, Object> headerParams, @Query("sotre_id") int sotre_id);

    @POST("v8/Products/setrate")
    Call<ResultAPIModel<ReviewModel>> setRate(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v8/Products/GetRates")
    Call<ResultAPIModel<ArrayList<ReviewModel>>> GetRates(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v8/Dinners/DinnersList")
    Call<ResultAPIModel<ArrayList<DinnerModel>>> getDinnersList(@HeaderMap() Map<String, Object> headerParams, @Query("lan") String lan);

    @POST("v8/Dinners/Dinner")
    Call<ResultAPIModel<SingleDinnerModel>> getSingleDinner(@HeaderMap() Map<String, Object> headerParams, @Query("dinner_id") int dinner_id,
                                                            @Query("lan") String lan);

    @GET("v8/Products/productRecipeList")
    Call<ResultAPIModel<ArrayList<ProductModel>>> getProductRecipeLis(@HeaderMap() Map<String, Object> headerParams, @Query("recipe_id") int recipe_id,
                                                                      @Query("country_id") int country_id, @Query("city_id")
                                                                              int city_id, @Query("user_id") String user_id,
                                                                      @Query("page_number") int page_number, @Query("page_size") int page_size);

    @GET("v8/Products/allbrands")
    Call<ResultAPIModel<ArrayList<BrandModel>>> GetAllBrands(@HeaderMap() Map<String, Object> headerParams, @Query("sotre_id") int sotre_id);


    @POST("v8/Booklets/BookletsList")
    Call<ResultAPIModel<ArrayList<BookletsModel>>> getBookletsList(@HeaderMap() Map<String, Object> headerParams, @Query("store_id") int store_id);


    @POST("v8/Booklets/BrochuresList")
    Call<ResultAPIModel<ArrayList<BrochuresModel>>> getBrochuresList(@HeaderMap() Map<String, Object> headerParams, @Query("store_id") int sotre_id, @Query("booklet_id") int booklet_id);


    @POST("v8/Company/setrate")
    Call<ResultAPIModel<ReviewModel>> setAppRate(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @POST("v8/Company/GetRates")
    Call<ResultAPIModel<ArrayList<ReviewModel>>> getAppRate(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @GET("v8/Company/AboutAs")
    Call<ResultAPIModel<SettingModel>> getSetting(@HeaderMap() Map<String, Object> headerParams, @Query("lng") String lng);

    @POST("v8/Favourite/addFavouriteProduct")
    Call<GeneralModel> addFavouriteProduct(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v8/Favourite/deleteFavouriteProduct")
    Call<GeneralModel> deleteFavouriteProduct(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v8/Carts/addToCart")
    Call<CartProcessModel> addToCart(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v8/Carts/AddExtrat")
    Call<AddExtraResponse> AddExtrat(@HeaderMap() Map<String, Object> headerParams, @Query("qty") int qty, @Query("barcode") String barcode, @Query("description") String description, @Query("user_id") int user_id, @Query("store_id") int store_id, @Body RequestBody params);


    @POST("v8/Carts/deleteCartItems")
    Call<CartProcessModel> deleteCartItems(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v8/Carts/updateRemark")
    Call<CartProcessModel> updateRemark(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @POST("v8/Carts/updateCart")
    Call<CartProcessModel> updateCart(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    // change version
    @GET("v8/Carts/checkOut")
    Call<CartResultModel> GetACarts(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id, @Query("store_ID") int store_ID);


    @GET("v8/Orders/checkOut")
    Call<CheckOrderResponse> checkCart(@HeaderMap() Map<String, Object> headerParams,
                                       @Query("user_id") int user_id, @Query("store_ID") int sotre_id);


    @GET("v8/Products/productList")
    Call<FavouriteResultModel> GetFavoriteProducts(@HeaderMap Map<String, Object> headerParams, @QueryMap Map<String, Object> queryParams);

    @POST("v8/Account/updateProfile")
    Call<LoginResultModel> updateProfile(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v8/Account/UploadPhoto")
    Call<ResultAPIModel<GeneralModel>> uploadPhoto(@HeaderMap() Map<String, Object> headerParams, @Body RequestBody params, @Query("user_id") int user_id);

    @GET("v8/Products/search")
    Call<FavouriteResultModel> searchProduct(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id, @Query("text") String text, @Query("page_number") int page_number, @Query("page_size") int page_size);

    @GET("v8/Products/barcodeSearch")
    Call<FavouriteResultModel> barcodeSearch(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id, @Query("barcode") String barcode, @Query("page_number") int page_number, @Query("page_size") int page_size);

    @GET("v8/Products/autocomplete")
    Call<AutoCompeteResult> autocomplete(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id, @Query("text") String text);

    @GET("v8/Products/productList")
    Call<FavouriteResultModel> getCatProductList(@HeaderMap() Map<String, Object> headerParams, @Query("category_id") int category_id, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id, @Query("filter") String filter, @Query("page_number") int page_number, @Query("page_size") int page_size);


    @GET("v8/Orders/getPastOrders")
    Call<OrdersResultModel> getPastOrders(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id);

    @GET("v8/Orders/GetDeliveryInfo")
    Call<DeliveryInfo> GetDeliveryInfo(@HeaderMap() Map<String, Object> headerParams, @QueryMap Map<String, Object> queryParams);


    @GET("v8/Orders/getUpcomingOrders")
    Call<OrdersResultModel> getUpcomingOrders(@HeaderMap() Map<String, Object> headerParams, @Query("address_id") int user_id);

    @POST("v8/Orders/GetOrdersList")
    Call<OrderResultModel> GetOrdersList(@HeaderMap() Map<String, Object> headerParams, @Body orderListCall param);


    @POST("v8/Orders/GetOrderDetails")
    Call<ResultAPIModel<ItemDetailsModel>> GetOrderDetails(@HeaderMap() Map<String, Object> headerParams, @Body orderListCall param);

    @GET("v8/Orders/GetOrderDelivery")
    Call<OrdersResultModel> getOrderDelivery(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id);

    @GET("v8/Orders/getPaymentMethod")
    Call<PaymentResultModel> getPaymentMethod(@HeaderMap() Map<String, Object> headerParams, @Query("sotre_id") int sotre_id);


    @POST("v8/Orders/CreateOrder")
    Call<OrdersResultModel> makeOrder(@HeaderMap() Map<String, Object> headerParams, @Body OrderCall param);

    @GET("v8/Products/productList")
    Call<FavouriteResultModel> getProductList(@HeaderMap() Map<String, Object> headerParams, @Query("category_id") int category_id, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id, @Query("filter") String filter, @Query("page_number") int page_number, @Query("page_size") int page_size);


    @GET("v8/ScanAndGo/GetItem")
    Call<ScanResult> addToFastQCart(@HeaderMap() Map<String, Object> headerParams, @QueryMap Map<String, Object> queryParams);


    @POST("v8/ScanAndGo/GetItem")
    Call<ResultAPIModel<ScanModel>> updateFastQCart(@HeaderMap() Map<String, Object> headerParams, @QueryMap Map<String, Object> queryParams);



    @POST("v8/ScanAndGo/GetCarts")
    Call<ResultAPIModel<ArrayList<CartFastQModel>>> getFastQCarts
            (@HeaderMap() Map<String, Object> headerParams,
             @QueryMap Map<String, Object> queryParams);


    @POST("v8/ScanAndGo/GenerateOrders")
    Call<ResultAPIModel<String>> generateOrders
            (@HeaderMap() Map<String, Object> headerParams,
             @QueryMap Map<String, Object> queryParams);

    @GET("v8/ScanAndGo/GetSetting")
    Call<ResultAPIModel<FastValidModel>> GetSetting
            (@HeaderMap() Map<String, Object> headerParams,
             @QueryMap Map<String, Object> queryParams);

    

}



