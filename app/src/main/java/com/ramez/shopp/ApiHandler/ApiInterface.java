package com.ramez.shopp.ApiHandler;


import com.ramez.shopp.Classes.AddExtraResponse;
import com.ramez.shopp.Classes.CityModelResult;
import com.ramez.shopp.Classes.DeliveryInfo;
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


    @POST("v7/Account/userRegister")
    Call<RegisterResultModel> registerUserHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v7/Account/driverRegister")
    Call<LoginResultModel> registerDriverHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v7/Account/login")
    Call<LoginResultModel> loginUserHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v7/Account/getotp")
    Call<OtpModel> GetOptHandle(@HeaderMap() Map<String, Object> headerParams, @Query("mobile_number") String mobile_number);

    @POST("v7/Account/getUserDetail")
    Call<ResultAPIModel<ProfileData>> getUserDetail(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id, @Query("store_id") int store_id);

    @POST("v7/Account/forgotPassword")
    Call<OtpModel> ForgetPasswordHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v7/Account/changePassword")
    Call<OtpModel> changePasswordHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @POST("v7/Account/updatePassword")
    Call<GeneralModel> updatePasswordHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @POST("v7/Account/updateDeviceToken2")
    Call<ResultAPIModel<String>> UpdateTokenHandle(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @POST("v7/Account/otpVerify")
    Call<GeneralModel> otpVerifyUserHandle(@HeaderMap() Map<String, Object> headerParams, @Body OtpModel param);

    /* ------------------------- Address Handle ------------------------- */

    /* -------------------------Start Loayl Handle ------------------------- */

    @POST("v7/Loayl/GetSettings")
    Call<ResultAPIModel<SettingCouponsModel>> getSettings(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id);

    @POST("v7/Loayl/GetTotalPoint")
    Call<ResultAPIModel<TotalPointModel>> getTotalPoint(@HeaderMap() Map<String, Object> headerParams, @Query("userid") int userId);

    @POST("v7/Loayl/GetCoupons")
    Call<ResultAPIModel<List<CouponsModel>>> getCoupons(@HeaderMap() Map<String, Object> headerParams, @Query("userid") int userId);

    @POST("v7/Loayl/GetTrans")
    Call<ResultAPIModel<List<TransactionModel>>> getTrans(@HeaderMap() Map<String, Object> headerParams, @Query("userid") int userId);

    @POST("v7/Loayl/GenerateCoupon")
    Call<GeneralModel> generateCoupon(@HeaderMap() Map<String, Object> headerParams, @QueryMap Map<String, Object> queryParams);

    /* -------------------------end Loayl Handle ------------------------- */

    @POST("v7/Locations/countryList")
    Call<CountryModelResult> GetCountry(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @GET("v7/Locations/getCountryDetail")
    Call<ResultAPIModel<CountryDetailsModel>> getCountryDetail(@HeaderMap() Map<String, Object> headerParams,
                                                               @QueryMap Map<String, Object> params);


    @GET("v7/Locations/getSocialLink")
    Call<ResultAPIModel<SoicalLink>> getSocialLink(@HeaderMap() Map<String, Object> headerParams,
                                                   @Query("store_id") int store_id);

    @GET("v7/Locations/getValidate")
    Call<GeneralModel> getValidate(@HeaderMap() Map<String, Object> headerParams, @Query("device_type") String device_type, @Query("app_version") String app_version, @Query("app_build") int app_build);

    @POST("v7/Locations/storedetails")
    Call<ResultAPIModel<QuickDeliveryRespond>> getQuickDelivery(@HeaderMap() Map<String, Object> headerParams, @Body QuickCall param);

    @POST
    Call<CityModelResult> GetCity(@Url String url, @HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @GET("v7/Locations/getAreas")
    Call<AreasResultModel> GetAreas(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id);

    @GET("v7/Address/getUserAddress")
    Call<AddressResultModel> GetUserAddress(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id);

    @GET("v7/Address/setDefaultAddress")
    Call<GeneralModel> setDefaultAddress(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id, @Query("address_id") int address_id);

    @GET("v7/Address/getAddressById")
    Call<AddressResultModel> GetAddressById(@HeaderMap() Map<String, Object> headerParams, @Query("address_id") int address_id);

    @POST("v7/Address/createNewAddress")
    Call<AddressResultModel> CreateNewAddress(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @GET("v7/Address/deleteAddress")
    Call<AddressResultModel> deleteAddress(@HeaderMap() Map<String, Object> headerParams, @Query("address_id") int address_id);


    @GET("v7/Account/logout")
    Call<ResultAPIModel<MemberModel>> logout(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id, @Query("user_type") String user_type);


    @GET("v7/Products/singleproductList")
    Call<ProductDetailsModel> GetSignalProducts(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("product_id") int product_id, @Query("user_id") String user_id);

    @GET("v7/Products/categoryList")
    Call<MainModel> GetMainPage(@HeaderMap() Map<String, Object> headerParams, @Query("category_id") int category_id, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id);

    @GET("v7/Products/AllCategories")
    Call<CategoryResultModel> GetAllCategories(@HeaderMap() Map<String, Object> headerParams, @Query("sotre_id") int sotre_id);

    @POST("v7/Products/setrate")
    Call<ResultAPIModel<ReviewModel>> setRate(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v7/Products/GetRates")
    Call<ResultAPIModel<ArrayList<ReviewModel>>> GetRates(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v7/Dinners/DinnersList")
    Call<ResultAPIModel<ArrayList<DinnerModel>>> getDinnersList(@HeaderMap() Map<String, Object> headerParams, @Query("lan") String lan);

    @POST("v7/Dinners/Dinner")
    Call<ResultAPIModel<SingleDinnerModel>> getSingleDinner(@HeaderMap() Map<String, Object> headerParams, @Query("dinner_id") int dinner_id,
                                                            @Query("lan") String lan);

    @GET("v7/Products/productRecipeList")
    Call<ResultAPIModel<ArrayList<ProductModel>>> getProductRecipeLis(@HeaderMap() Map<String, Object> headerParams, @Query("recipe_id") int recipe_id,
                                                                      @Query("country_id") int country_id, @Query("city_id")
                                                                              int city_id, @Query("user_id") String user_id,
                                                                      @Query("page_number") int page_number, @Query("page_size") int page_size);

    @GET("v7/Products/allbrands")
    Call<ResultAPIModel<ArrayList<BrandModel>>> GetAllBrands(@HeaderMap() Map<String, Object> headerParams, @Query("sotre_id") int sotre_id);


    @POST("v7/Booklets/BookletsList")
    Call<ResultAPIModel<ArrayList<BookletsModel>>> getBookletsList(@HeaderMap() Map<String, Object> headerParams, @Query("store_id") int store_id);


    @POST("v7/Booklets/BrochuresList")
    Call<ResultAPIModel<ArrayList<BrochuresModel>>> getBrochuresList(@HeaderMap() Map<String, Object> headerParams, @Query("store_id") int sotre_id, @Query("booklet_id") int booklet_id);


    @POST("v7/Company/setrate")
    Call<ResultAPIModel<ReviewModel>> setAppRate(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @POST("v7/Company/GetRates")
    Call<ResultAPIModel<ArrayList<ReviewModel>>> getAppRate(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @GET("v7/Company/AboutAs")
    Call<ResultAPIModel<SettingModel>> getSetting(@HeaderMap() Map<String, Object> headerParams, @Query("lng") String lng);

    @POST("v7/Favourite/addFavouriteProduct")
    Call<GeneralModel> addFavouriteProduct(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v7/Favourite/deleteFavouriteProduct")
    Call<GeneralModel> deleteFavouriteProduct(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v7/Carts/addToCart")
    Call<CartProcessModel> addToCart(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v7/Carts/AddExtrat")
    Call<AddExtraResponse> AddExtrat(@HeaderMap() Map<String, Object> headerParams, @Query("qty") int qty, @Query("barcode") String barcode, @Query("description") String description, @Query("user_id") int user_id, @Query("store_id") int store_id, @Body RequestBody params);


    @POST("v7/Carts/deleteCartItems")
    Call<CartProcessModel> deleteCartItems(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v7/Carts/updateRemark")
    Call<CartProcessModel> updateRemark(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);


    @POST("v7/Carts/updateCart")
    Call<CartProcessModel> updateCart(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    // change version
    @GET("v7/Carts/checkOut")
    Call<CartResultModel> GetACarts(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id, @Query("store_ID") int store_ID);


    @GET("v7/Orders/checkOut")
    Call<CheckOrderResponse> checkCart(@HeaderMap() Map<String, Object> headerParams,
                                       @Query("user_id") int user_id, @Query("store_ID") int sotre_id);


    @GET("v7/Products/productList")
    Call<FavouriteResultModel> GetFavoriteProducts(@HeaderMap Map<String, Object> headerParams, @QueryMap Map<String, Object> queryParams);

    @POST("v7/Account/updateProfile")
    Call<LoginResultModel> updateProfile(@HeaderMap() Map<String, Object> headerParams, @Body Map<String, Object> params);

    @POST("v7/Account/UploadPhoto")
    Call<ResultAPIModel<GeneralModel>> uploadPhoto(@HeaderMap() Map<String, Object> headerParams, @Body RequestBody params, @Query("user_id") int user_id);

    @GET("v7/Products/search")
    Call<FavouriteResultModel> searchProduct(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id, @Query("text") String text, @Query("page_number") int page_number, @Query("page_size") int page_size);

    @GET("v7/Products/barcodeSearch")
    Call<FavouriteResultModel> barcodeSearch(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id, @Query("barcode") String barcode, @Query("page_number") int page_number, @Query("page_size") int page_size);

    @GET("v7/Products/autocomplete")
    Call<AutoCompeteResult> autocomplete(@HeaderMap() Map<String, Object> headerParams, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id, @Query("text") String text);

    @GET("v7/Products/productList")
    Call<FavouriteResultModel> getCatProductList(@HeaderMap() Map<String, Object> headerParams, @Query("category_id") int category_id, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id, @Query("filter") String filter, @Query("page_number") int page_number, @Query("page_size") int page_size);


    @GET("v7/Orders/getPastOrders")
    Call<OrdersResultModel> getPastOrders(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id);

    @GET("v7/Orders/GetDeliveryInfo")
    Call<DeliveryInfo> GetDeliveryInfo(@HeaderMap() Map<String, Object> headerParams, @QueryMap Map<String, Object> queryParams);


    @GET("v7/Orders/getUpcomingOrders")
    Call<OrdersResultModel> getUpcomingOrders(@HeaderMap() Map<String, Object> headerParams, @Query("address_id") int user_id);

    @POST("v7/Orders/GetOrdersList")
    Call<OrderResultModel> GetOrdersList(@HeaderMap() Map<String, Object> headerParams, @Body orderListCall param);


    @POST("v7/Orders/GetOrderDetails")
    Call<ResultAPIModel<ItemDetailsModel>> GetOrderDetails(@HeaderMap() Map<String, Object> headerParams, @Body orderListCall param);

    @GET("v7/Orders/GetOrderDelivery")
    Call<OrdersResultModel> getOrderDelivery(@HeaderMap() Map<String, Object> headerParams, @Query("user_id") int user_id);

    @GET("v7/Orders/getPaymentMethod")
    Call<PaymentResultModel> getPaymentMethod(@HeaderMap() Map<String, Object> headerParams, @Query("sotre_id") int sotre_id);


    @POST("v7/Orders/CreateOrder")
    Call<OrdersResultModel> makeOrder(@HeaderMap() Map<String, Object> headerParams, @Body OrderCall param);

    @GET("v7/Products/productList")
    Call<FavouriteResultModel> getProductList(@HeaderMap() Map<String, Object> headerParams, @Query("category_id") int category_id, @Query("country_id") int country_id, @Query("city_id") int city_id, @Query("user_id") String user_id, @Query("filter") String filter, @Query("page_number") int page_number, @Query("page_size") int page_size);


}

