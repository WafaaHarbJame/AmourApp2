package com.ramez.shopp.ApiHandler;


import android.accounts.Account;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.OtpModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Classes.orderListCall;
import com.ramez.shopp.Models.AddExtraCall;
import com.ramez.shopp.Models.AddressModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.LoginResultModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.OrderCall;
import com.ramez.shopp.Models.QuickCall;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.ReviewModel;

import java.io.File;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;


public class DataFeacher {
    final String TAG = "Log";
    final String LOGIN_URL = "/" + GlobalData.COUNTRY + "/GroceryStoreApi/api/v4/Account/login";
    DataFetcherCallBack dataFetcherCallBack;
    ApiInterface apiService;
    //    int city;
    String accessToken;
    String lang;
    Map<String, Object> headerMap = new HashMap<>();
    private Callback callbackApi;

    public DataFeacher() {
        apiService = ApiClient.getClient().create(ApiInterface.class);
        this.dataFetcherCallBack = (obj, func, IsSuccess) -> {

        };

        headerMap.put("ApiKey", Constants.api_key);
        headerMap.put("device_type", Constants.deviceType);
        headerMap.put("app_version", UtilityApp.getAppVersionStr());
        headerMap.put("token", UtilityApp.getToken());
        headerMap.put("Accept", "application/json");
        headerMap.put("Content-Type", "application/json");

        callbackApi = new Callback() {
            @Override
            public void onResponse(Call call, Response response) {


                if (response.isSuccessful()) {

//                    ResultAPIModel result = (ResultAPIModel) response.body();

                    if (dataFetcherCallBack != null)
                        dataFetcherCallBack.Result(response.body(), Constants.success, true);
//                    if (dataFetcherCallBack != null) {
//
//                        if (result != null && result.status == 0) {
//                            dataFetcherCallBack.Result(response.body(), Constants.ERROR, false);
//
//                        } else {
//                            dataFetcherCallBack.Result(response.body(), Constants.success, true);
//
//                        }
//                    }

                } else {
                    ResultAPIModel errorModel = null;
                    try {
                        String error = response.errorBody().string();
                        Log.e("Log", "Log error " + error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ResultAPIModel>() {
                        }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dataFetcherCallBack.Result(errorModel, Constants.ERROR, false);

                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
                if ((t instanceof UnknownHostException || t instanceof NoRouteToHostException) && dataFetcherCallBack != null) {
                    dataFetcherCallBack.Result(null, Constants.NO_CONNECTION, false);
                } else {
                    if (dataFetcherCallBack != null)
                        dataFetcherCallBack.Result(null, Constants.FAIL, false);
                }
            }
        };

    }

    public DataFeacher(boolean isLong, DataFetcherCallBack dataFetcherCallBack) {
        this.dataFetcherCallBack = dataFetcherCallBack;
        apiService = isLong ? ApiClient.getLongClient().create(ApiInterface.class) : ApiClient.getClient().create(ApiInterface.class);

        headerMap.put("ApiKey", Constants.api_key);
        headerMap.put("device_type", Constants.deviceType);
        headerMap.put("app_version", UtilityApp.getAppVersionStr());
        headerMap.put("token", UtilityApp.getToken());
        headerMap.put("Accept", "application/json");
        headerMap.put("Content-Type", "application/json");

        callbackApi = new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()) {
                    System.out.println("Log url " + call.request().url().url().getPath());

                    String url = call.request().url().url().getPath();
                    if (url.equals(LOGIN_URL)) {
                        LoginResultModel result = (LoginResultModel) response.body();

                        if (dataFetcherCallBack != null) {

                            if (result != null && result.getStatus() == 0) {
                                dataFetcherCallBack.Result(response.body(), Constants.ERROR, false);

                            } else {
                                dataFetcherCallBack.Result(response.body(), Constants.success, true);
                            }
                        }

                    } else {
                        if (dataFetcherCallBack != null)
                            dataFetcherCallBack.Result(response.body(), Constants.success, true);
                    }

                } else {
                    ResultAPIModel errorModel = null;
                    try {
                        String error = response.errorBody().string();
                        Log.e("Log", "Log error " + error);
                        errorModel = new Gson().fromJson(error, new TypeToken<ResultAPIModel>() {
                        }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    dataFetcherCallBack.Result(errorModel, Constants.ERROR, false);

                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
                if ((t instanceof UnknownHostException || t instanceof NoRouteToHostException) && dataFetcherCallBack != null) {
                    dataFetcherCallBack.Result(null, Constants.NO_CONNECTION, false);
                } else {
                    if (dataFetcherCallBack != null)
                        dataFetcherCallBack.Result(null, Constants.FAIL, false);
                }
            }
        };


    }

    public void loginHandle(MemberModel memberModel) {

        Map<String, Object> params = new HashMap<>();
        params.put("mobile_number", memberModel.getMobileNumber());
        params.put("password", memberModel.getPassword());
        params.put("user_type", memberModel.getUserType());
        params.put("device_type", Constants.deviceType);
        params.put("device_token", memberModel.getDeviceToken());
        params.put("device_id", memberModel.getDeviceToken());
       params.put("city_id", memberModel.getCity());

        Log.i(TAG, "Log loginHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log getMobileNumber " + memberModel.getMobileNumber());
        Log.i(TAG, "Log password " + memberModel.getPassword());
        Log.i(TAG, "Log device_type " + "android");
        Log.i(TAG, "Log device_token " + memberModel.getDeviceToken());
        Log.i(TAG, "Log getDeviceId " + memberModel.getDeviceId());
        Log.i(TAG, "Log user_type " + memberModel.getUserType());
        Log.i(TAG, "Log StoreId " + memberModel.getCity());

        Call call = apiService.loginUserHandle(headerMap, params);
        call.enqueue(callbackApi);

    }

    public void logOut(MemberModel memberModel) {

        Log.i(TAG, "Log logOut");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + memberModel.getId());

        Call call = apiService.logout(headerMap, memberModel.getId(), Constants.user_type);
        call.enqueue(callbackApi);

    }

    public void RegisterHandle(MemberModel memberModel) {

        Map<String, Object> params = new HashMap<>();
        params.put("mobile_number", memberModel.getMobileNumber());
        params.put("password", memberModel.getPassword());
        params.put("user_type", Constants.user_type);
        params.put("device_type", memberModel.getDeviceType());
        params.put("device_token", memberModel.getDeviceToken());
        params.put("name", memberModel.getName());
        params.put("country", memberModel.getCountry());
        params.put("city", memberModel.getCity());
        params.put("email", memberModel.getEmail());
        params.put("device_id", memberModel.getDeviceId());
        params.put("prefix", memberModel.getPrefix());

        Log.i(TAG, "Log RegisterHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log username " + memberModel.getName());
        Log.i(TAG, "Log password " + memberModel.getPassword());
        Log.i(TAG, "Log device_type " + "android");
        Log.i(TAG, "Log device_token " + memberModel.getDeviceToken());
        Log.i(TAG, "Log city ID  " + memberModel.getCity());
        Log.i(TAG, "Log country Name " + memberModel.getCountry());
        Log.i(TAG, "Log mobile_number " + memberModel.getMobileNumber());
        Log.i(TAG, "Log device_id " + memberModel.getDeviceId());
        Log.i(TAG, "Log prefix " + memberModel.getPrefix());
        Log.i(TAG, "Log device_token " + memberModel.getDeviceToken());
        Log.i(TAG, "Log EMAIL " + memberModel.getEmail());

        Call call = apiService.registerUserHandle(headerMap, params);
        call.enqueue(callbackApi);
    }

    public void ForgetPasswordHandle(MemberModel memberModel) {

        Map<String, Object> params = new HashMap<>();
        params.put("mobile_number", memberModel.getMobileNumber());
        params.put("user_type", memberModel.getUserType());

        Log.i(TAG, "Log ForgetPasswordHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log mobile_number " + memberModel.getMobileNumber());
        Log.i(TAG, "Log user_type " + memberModel.getUserType());

        Call call = apiService.ForgetPasswordHandle(headerMap, params);
        call.enqueue(callbackApi);
    }


    public void ChangePasswordHandle(MemberModel memberModel) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", memberModel.getId());
        params.put("password", memberModel.getPassword());
        params.put("new_password", memberModel.getNew_password());

        Log.i(TAG, "Log ChangePasswordHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + memberModel.getId());
        Log.i(TAG, "Log password " + memberModel.getPassword());
        Log.i(TAG, "Log new_password " + memberModel.getNew_password());


        Call call = apiService.changePasswordHandle(headerMap, params);
        call.enqueue(callbackApi);

    }


    public void UpdatePasswordHandle(MemberModel memberModel) {

        Map<String, Object> params = new HashMap<>();
        params.put("mobile_number", memberModel.getMobileNumber());
        params.put("password", memberModel.getPassword());
        params.put("re_password", memberModel.getNew_password());

        Log.i(TAG, "Log UpdatePasswordHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log mobile_number " + memberModel.getMobileNumber());
        Log.i(TAG, "Log password " + memberModel.getPassword());
        Log.i(TAG, "Log re_password " + memberModel.getNew_password());


        Call call = apiService.updatePasswordHandle(headerMap, params);
        call.enqueue(callbackApi);

    }


    public void UpdateTokenHandle(MemberModel memberModel) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", memberModel.getId());
        params.put("device_token", memberModel.getDeviceToken());
        params.put("user_type", Constants.user_type);

        Log.i(TAG, "Log UpdateTokenHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + memberModel.getId());
        Log.i(TAG, "Log device_token " + memberModel.getDeviceToken());

        Call call = apiService.UpdateTokenHandle(headerMap, params);
        call.enqueue(callbackApi);
    }

    public void CountryHandle() {

        Map<String, Object> params = new HashMap<>();
        lang = UtilityApp.getLanguage();
        if (lang != null) {
            params.put("lan", lang);
        } else {
            params.put("lan", Locale.getDefault().getLanguage());

        }

        Log.i(TAG, "Log GetCountryHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log lan " + lang);

        Call call = apiService.GetCountry(headerMap, params);
        call.enqueue(callbackApi);
    }

    public void CityHandle(int country_id) {

        Log.i(TAG, "Log CityHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_id " + country_id);

        String countryCode = "";
        if (UtilityApp.getLocalData().getShortname() != null)
            countryCode = UtilityApp.getLocalData().getShortname();
        else countryCode = GlobalData.COUNTRY;

        String url = " https://risteh.com/" + countryCode + "/GroceryStoreApi/api/v4/Locations/citiesByCountry";

        if (UtilityApp.getLanguage() != null) {
            lang = UtilityApp.getLanguage();
        } else {
            lang = Locale.getDefault().getLanguage();
        }

        Log.i(TAG, "Log lang " + lang);

        Map<String, Object> params = new HashMap<>();
        params.put("country_id", country_id);


        Call call = apiService.GetCity(url, headerMap, params);
        call.enqueue(callbackApi);
    }

    public void sendOpt(String mobile_number) {

        Log.i(TAG, "Log sendOpt");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log mobile_number " + mobile_number);

        Call call = apiService.GetOptHandle(headerMap, mobile_number);
        call.enqueue(callbackApi);

    }

    public void VerifyOtpHandle(String mobile_number, String otp) {

        Log.i(TAG, "Log VerifyOtpHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log mobile_number " + mobile_number);
        Log.i(TAG, "Log otp " + otp);

        OtpModel otpModel = new OtpModel();
        otpModel.setMobileNumber(mobile_number);
        otpModel.setOtp(otp);

        Call call = apiService.otpVerifyUserHandle(headerMap, otpModel);
        call.enqueue(callbackApi);
    }

    public void GetAddressHandle(int user_id) {

        Log.i(TAG, "Log GetAddressHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + user_id);

        Call call = apiService.GetUserAddress(headerMap, user_id);
        call.enqueue(callbackApi);
    }


    public void setDefaultAddress(int user_id, int address_id) {

        Log.i(TAG, "Log setDefaultAddress");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log addressId " + address_id);


        Call call = apiService.setDefaultAddress(headerMap, user_id, address_id);
        call.enqueue(callbackApi);
    }


    public void GetAddressByIdHandle(int address_id) {

        Log.i(TAG, "Log GetAddressByIdHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log address_id " + address_id);

        Call call = apiService.GetAddressById(headerMap, address_id);
        call.enqueue(callbackApi);

    }

    public void CreateAddressHandle(AddressModel addressModel) {


        Log.i(TAG, "Log CreateAddressHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log mobile_number " + addressModel.getMobileNumber());
        Log.i(TAG, "Log user id  " + addressModel.getUserId());
        Log.i(TAG, "Log name " + addressModel.getName());
        Log.i(TAG, "Log area id  " + addressModel.getAreaId());
        Log.i(TAG, "Log state id  " + addressModel.getState());
        Log.i(TAG, "Log getBlock   " + addressModel.getBlock());
        Log.i(TAG, "Log getStreetDetails  " + addressModel.getStreetDetails());
        Log.i(TAG, "Log getHouseNo  " + addressModel.getHouseNo());
        Log.i(TAG, "Log apartment_no  " + addressModel.getApartmentNo());
        Log.i(TAG, "Log phone_prefix  " + addressModel.getPhonePrefix());
        Log.i(TAG, "Log mobile_number  " + addressModel.getMobileNumber());
        Log.i(TAG, "Log longitude  " + addressModel.getLongitude());
        Log.i(TAG, "Log getLatitude  " + addressModel.getLatitude());
        Log.i(TAG, "Log google_address  " + addressModel.getGoogleAddress());

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", addressModel.getUserId());
        params.put("name", addressModel.getName());
        params.put("area_id", addressModel.getAreaId());
        params.put("state_id", addressModel.getState());
        params.put("block", addressModel.getBlock());
        params.put("street_details", addressModel.getStreetDetails());
        params.put("house_no", addressModel.getHouseNo());
        params.put("apartment_no", addressModel.getApartmentNo());
        params.put("phone_prefix", addressModel.getPhonePrefix());
        params.put("mobile_number", addressModel.getMobileNumber());
        params.put("longitude", addressModel.getLongitude());
        params.put("latitude", addressModel.getLatitude());
        params.put("google_address", addressModel.getGoogleAddress());


        Call call = apiService.CreateNewAddress(headerMap, params);
        call.enqueue(callbackApi);
    }

    public void GetAreasHandle(int country_id) {

        Log.i(TAG, "Log GetAreasHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_id " + country_id);

        Call call = apiService.GetAreas(headerMap, country_id);
        call.enqueue(callbackApi);
    }

    public void deleteAddressHandle(int address_id) {

        Log.i(TAG, "Log deleteAddressHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log address_id " + address_id);

        Call call = apiService.deleteAddress(headerMap, address_id);
        call.enqueue(callbackApi);

    }


    public void getSettings(int countryId) {

        Log.i(TAG, "Log settingCouponsModel");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_id " + countryId);

        Call call = apiService.getSettings(headerMap, countryId);
        call.enqueue(callbackApi);
    }

    public void getTotalPoint(int userId) {

        Log.i(TAG, "Log getTotalPoint");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log userid " + userId);

        Call call = apiService.getTotalPoint(headerMap, userId);
        call.enqueue(callbackApi);
    }

    public void getCoupons(int userId) {

        Log.i(TAG, "Log getCoupons");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log userid " + userId);

        Call call = apiService.getCoupons(headerMap, userId);
        call.enqueue(callbackApi);
    }

    public void getTrans(int userId) {

        Log.i(TAG, "Log getTrans");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log userid " + userId);

        Call call = apiService.getTrans(headerMap, userId);
        call.enqueue(callbackApi);
    }

    public void generateCoupon(int userId, int points) {

        Map<String, Object> params = new HashMap<>();
        params.put("userid", userId);
        params.put("points", points);

        Log.i(TAG, "Log generateCoupon");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log userid " + userId);
        Log.i(TAG, "Log points " + points);

        Call call = apiService.generateCoupon(headerMap, params);
        call.enqueue(callbackApi);

    }


    public void GetMainPage(int category_id, int country_id, int city_id, String user_id) {

        Log.i(TAG, "Log GetMainPage");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_id " + country_id);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log city_id " + city_id);

        Call call = apiService.GetMainPage(headerMap, category_id, country_id, city_id, user_id);
        call.enqueue(callbackApi);
    }

    public void GetSingleProduct(int country_id, int city_id, int product_id, String user_id) {

        Log.i(TAG, "Log GetSingleProduct");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_id " + country_id);
        Log.i(TAG, "Log product_id " + product_id);
        Log.i(TAG, "Log user_id " + user_id);

        Call call = apiService.GetSignalProducts(headerMap, country_id, city_id, product_id, user_id);
        call.enqueue(callbackApi);
    }


    public void GetAllCategories(int sotre_id) {

        Log.i(TAG, "Log GetAllCategories");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_id " + sotre_id);

        Call call = apiService.GetAllCategories(headerMap, sotre_id);
        call.enqueue(callbackApi);
    }


    public void GetAllBrands(int sotre_id) {

        Log.i(TAG, "Log GetAllBrands");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_id " + sotre_id);

        Call call = apiService.GetAllBrands(headerMap, sotre_id);
        call.enqueue(callbackApi);
    }


    public void addToFavoriteHandle(int user_id, int store_ID, int product_id) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("store_ID", store_ID);
        params.put("product_id", product_id);


        Log.i(TAG, "Log addToFavoriteHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log store_ID " + store_ID);
        Log.i(TAG, "Log product_id " + product_id);

        Call call = apiService.addFavouriteProduct(headerMap, params);
        call.enqueue(callbackApi);
    }

    public void deleteFromFavoriteHandle(int user_id, int store_ID, int product_id) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("store_ID", store_ID);
        params.put("product_id", product_id);

        Log.i(TAG, "Log deleteFromFavoriteHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log store_ID " + store_ID);
        Log.i(TAG, "Log product_id " + product_id);

        Call call = apiService.deleteFavouriteProduct(headerMap, params);
        call.enqueue(callbackApi);
    }

    public void addCartHandle(int productId, int product_barcode_id, int quantity, int userId, int storeId) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("store_ID", storeId);
        params.put("product_id", productId);
        params.put("quantity", quantity);
        params.put("product_barcode_id", product_barcode_id);


        Log.i(TAG, "Log addCartHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + userId);
        Log.i(TAG, "Log store_ID " + storeId);
        Log.i(TAG, "Log product_id " + productId);
        Log.i(TAG, "Log product_barcode_id " + product_barcode_id);
        Log.i(TAG, "Log quantity " + quantity);


        Call call = apiService.addToCart(headerMap, params);
        call.enqueue(callbackApi);
    }

    public void updateCartHandle(int productId, int product_barcode_id, int quantity, int userId, int storeId, int cart_id, String update_quantity) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("store_ID", storeId);
        params.put("product_id", productId);
        params.put("quantity", quantity);
        params.put("cart_id", cart_id);
        params.put("update_type", update_quantity);
        params.put("product_barcode_id", product_barcode_id);


        Log.i(TAG, "Log updateCartHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + userId);
        Log.i(TAG, "Log cart_id " + cart_id);
        Log.i(TAG, "Log store_ID " + storeId);
        Log.i(TAG, "Log product_id " + productId);
        Log.i(TAG, "Log product_barcode_id " + product_barcode_id);
        Log.i(TAG, "Log quantity " + quantity);


        Call call = apiService.updateCart(headerMap, params);
        call.enqueue(callbackApi);
    }

    public void deleteCartHandle(int productId, int product_barcode_id, int cart_id, int userId, int storeId) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("store_ID", storeId);
        params.put("product_id", productId);
        params.put("cart_id", cart_id);
        params.put("product_barcode_id", product_barcode_id);


        Log.i(TAG, "Log deleteCartHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + userId);
        Log.i(TAG, "Log store_ID " + storeId);
        Log.i(TAG, "Log product_id " + productId);
        Log.i(TAG, "Log product_barcode_id " + product_barcode_id);
        Log.i(TAG, "Log cart_id " + cart_id);


        Call call = apiService.deleteCartItems(headerMap, params);
        call.enqueue(callbackApi);
    }


    public void updateRemarkCartHandle(int cart_id, String remark) {

        Map<String, Object> params = new HashMap<>();
        params.put("cart_id", cart_id);
        params.put("remark", remark);


        Log.i(TAG, "Log updateRemarkCartHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log remark " + remark);
        Log.i(TAG, "Log cart_id " + cart_id);


        Call call = apiService.updateRemark(headerMap, params);
        call.enqueue(callbackApi);
    }


    public void GetCarts(int sotre_id, int user_id) {

        Log.i(TAG, "Log GetCarts");
        Log.i(TAG, "Log headerMap " + headerMap);

        Log.i(TAG, "Log sotre_id " + sotre_id);
        Log.i(TAG, "Log user_id " + user_id);

        Call call = apiService.GetACarts(headerMap, user_id, sotre_id);
        call.enqueue(callbackApi);


    }


    public void updateProfile(MemberModel memberModel) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", memberModel.getId());
        params.put("name", memberModel.getName());
        params.put("email", memberModel.getEmail());
        params.put("country", memberModel.getCountry());
        params.put("state", "1");
        params.put("city", memberModel.getCity());

        Log.i(TAG, "Log updateProfile");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log name " + memberModel.getName());
        Log.i(TAG, "Log email " + memberModel.getEmail());
        Log.i(TAG, "Log country " + memberModel.getCountry());
        Log.i(TAG, "Log city " + memberModel.getCity());


        Call call = apiService.updateProfile(headerMap, params);
        call.enqueue(callbackApi);

    }


    public void uploadPhoto(int userId, File photo) {

        Log.i(TAG, "Log uploadPhoto");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log userId " + userId);
        Log.i(TAG, "Log photo Name  " + photo.getName());

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        if (photo != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), photo);
            builder.addFormDataPart("file", photo.lastModified() + ".png", requestBody);
        }

        MultipartBody requestBody = builder.build();

        Call call = apiService.uploadPhoto(headerMap, requestBody, userId);
        call.enqueue(callbackApi);

    }


    public void getUserDetails(int user_id,int store_id) {

        Log.i(TAG, "Log getUserDetails");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log store_id " + store_id);

        Call call = apiService.getUserDetail(headerMap,user_id,store_id);
        call.enqueue(callbackApi);
    }

    public void barcodeSearch(int country_id, int city_id, String user_id, String filter, int page_number, int page_size) {

        Log.i(TAG, "Log barcodeSearch");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_id " + country_id);
        Log.i(TAG, "Log city_id " + city_id);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log filter " + filter);
        Log.i(TAG, "Log page_number " + page_number);
        Log.i(TAG, "Log page_size " + page_size);

        Call call = apiService.barcodeSearch(headerMap, country_id, city_id, user_id, filter, page_number, page_size);
        call.enqueue(callbackApi);
    }

    public void searchTxt(int country_id, int city_id, String user_id, String filter, int page_number, int page_size) {

        Log.i(TAG, "Log searchTxt");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_id " + country_id);
        Log.i(TAG, "Log city_id " + city_id);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log filter " + filter);
        Log.i(TAG, "Log page_number " + page_number);
        Log.i(TAG, "Log page_size " + page_size);

        Call call = apiService.searchProduct(headerMap, country_id, city_id, user_id, filter, page_number, page_size);
        call.enqueue(callbackApi);
    }

    public Call autocomplete(int country_id, int city_id, String user_id, String text, int page_number, int page_size) {

        Log.i(TAG, "Log autocomplete");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_id " + country_id);
        Log.i(TAG, "Log city_id " + city_id);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log text " + text);
        Log.i(TAG, "Log page_number " + page_number);
        Log.i(TAG, "Log page_size " + page_size);

        Call call = apiService.autocomplete(headerMap, country_id, city_id, user_id, text);
        call.enqueue(callbackApi);

        return call;
    }

    public void getCatProductList(int category_id, int country_id, int city_id, String user_id, String filter, int page_number, int page_size) {

        Log.i(TAG, "Log getCatProductList");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log category_id " + category_id);
        Log.i(TAG, "Log country_id " + country_id);
        Log.i(TAG, "Log city_id " + city_id);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log filter " + filter);
        Log.i(TAG, "Log page_number " + page_number);
        Log.i(TAG, "Log page_size " + page_size);

        Call call = apiService.getCatProductList(headerMap, category_id, country_id, city_id, user_id, filter, page_number, page_size);
        call.enqueue(callbackApi);
    }


    public Call getFavorite(int category_id, int country_id, int city_id, String user_id, String filter, int brand_id, int page_number, int page_size) {

        Log.i(TAG, "Log getFavorite");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log category_id " + category_id);
        Log.i(TAG, "Log brand_id " + brand_id);
        Log.i(TAG, "Log country_id " + country_id);
        Log.i(TAG, "Log city_id " + city_id);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log filter " + filter);
        Log.i(TAG, "Log page_number " + page_number);
        Log.i(TAG, "Log page_size " + page_size);

        Call call = apiService.GetFavoriteProducts(headerMap, category_id, country_id, city_id, user_id, filter, brand_id, page_number, page_size);
        call.enqueue(callbackApi);
        return call;
    }


    public void getPastOrders(int user_id) {

        Log.i(TAG, "Log getPastOrders");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + user_id);

        Call call = apiService.getPastOrders(headerMap, user_id);
        call.enqueue(callbackApi);
    }

    public void getUpcomingOrders(int user_id) {

        Log.i(TAG, "Log getUpcomingOrders");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + user_id);

        Call call = apiService.getUpcomingOrders(headerMap, user_id);
        call.enqueue(callbackApi);
    }

    public void getPaymentMethod(int sotre_id) {

        Log.i(TAG, "Log getPaymentMethod");

        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log sotre_id " + sotre_id);

        Call call = apiService.getPaymentMethod(headerMap, sotre_id);
        call.enqueue(callbackApi);
    }


    public void getDeliveryTimeList(int sotre_id) {

        Log.i(TAG, "Log getDeliveryTimeList");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log store Id getDeliveryTimeList " + sotre_id);

        Call call = apiService.getDeliveryTimeList(headerMap, sotre_id);
        call.enqueue(callbackApi);
    }

    public void makeOrder(OrderCall orderCalls) {
        OrderCall orderCall = new OrderCall();
        orderCall.user_id = orderCalls.user_id;
        orderCall.store_ID = orderCalls.store_ID;
        orderCall.address_id = orderCalls.address_id;
        orderCall.payment_method = orderCalls.payment_method;
        orderCall.coupon_code_id = orderCalls.coupon_code_id;
        orderCall.delivery_date_id = orderCalls.delivery_date_id;
        orderCall.expressDelivery = orderCalls.expressDelivery;

        Log.i(TAG, "Log makeOrder");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + orderCalls.user_id);
        Log.i(TAG, "Log store_ID " + orderCalls.store_ID);
        Log.i(TAG, "Log addressId " + orderCalls.address_id);
        Log.i(TAG, "Log payment_method " + orderCalls.payment_method);
        Log.i(TAG, "Log coupon_code_id " + orderCalls.coupon_code_id);
        Log.i(TAG, "Log delivery_date_id " + orderCalls.delivery_date_id);
        Log.i(TAG, "Log expressDelivery " + orderCalls.expressDelivery);

        Call call = apiService.makeOrder(headerMap, orderCall);
        call.enqueue(callbackApi);
    }

    public void AddExtrat(AddExtraCall addExtraCall, File photo) {
        Log.i(TAG, "Log AddExtrat");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + addExtraCall.userId);
        Log.i(TAG, "Log store_ID " + addExtraCall.storeId);
        Log.i(TAG, "Log qty " + addExtraCall.qty);
        Log.i(TAG, "Log barcode " + addExtraCall.barcode);
        Log.i(TAG, "Log description " + addExtraCall.description);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);

        if (photo != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), photo);
            builder.addFormDataPart("file", photo.lastModified() + ".png", requestBody);
        }

        MultipartBody requestBody = builder.build();


        Call call = apiService.AddExtrat(headerMap, addExtraCall.qty,
                addExtraCall.barcode, addExtraCall.description, addExtraCall.userId, addExtraCall.storeId, requestBody);
        call.enqueue(callbackApi);
    }


    public void getQuickDelivery(QuickCall quickCall) {
        QuickCall quickCall1 = new QuickCall();
        quickCall1.store_id = quickCall.store_id;
        quickCall1.country_id = quickCall.country_id;

        Log.i(TAG, "Log getQuickDelivery");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log store_id " + quickCall1.store_id);
        Log.i(TAG, "Log country_id " + quickCall1.country_id);

        Call call = apiService.getQuickDelivery(headerMap, quickCall1);
        call.enqueue(callbackApi);
    }

    public void getProductList(int category_id, int country_id, int city_id, String user_id, String filter, int page_number, int page_size) {

        Log.i(TAG, "Log getProductList");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log category_id " + category_id);
        Log.i(TAG, "Log country_id " + country_id);
        Log.i(TAG, "Log city_id " + city_id);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log filter " + filter);
        Log.i(TAG, "Log page_number " + page_number);
        Log.i(TAG, "Log page_size " + page_size);

        Call call = apiService.getProductList(headerMap, category_id, country_id, city_id, user_id, filter, page_number, page_size);
        call.enqueue(callbackApi);
    }


    public void getRate(int productId, int store_id) {

        Map<String, Object> params = new HashMap<>();
        params.put("store_id", store_id);
        params.put("product_id", productId);

        Log.i(TAG, "Log getRate");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log store_ID " + store_id);
        Log.i(TAG, "Log product_id " + productId);


        Call call = apiService.GetRates(headerMap, params);
        call.enqueue(callbackApi);
    }


    public void getProductRecipeList(int recipe_id, int country_id, int city_id, String user_id, int page_number, int page_size) {

        Log.i(TAG, "Log getProductRecipeList");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log recipe_id " + recipe_id);
        Log.i(TAG, "Log country_id " + country_id);
        Log.i(TAG, "Log city_id " + city_id);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log page_number " + page_number);
        Log.i(TAG, "Log page_size " + page_size);


        Call call = apiService.getProductRecipeLis(headerMap, recipe_id, country_id, city_id, user_id, page_number, page_size);
        call.enqueue(callbackApi);
    }


    public void getBrochuresList(int store_id, int booklet_id) {

        Log.i(TAG, "Log getBrochuresList");
        Log.i(TAG, "Log headerMap " + headerMap);

        Log.i(TAG, "Log store_ID " + store_id);
        Log.i(TAG, "Log booklet_id " + booklet_id);


        Call call = apiService.getBrochuresList(headerMap, store_id, booklet_id);
        call.enqueue(callbackApi);
    }


    public void getBookletsList(int store_id) {


        Log.i(TAG, "Log getBookletsList");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log store_ID " + store_id);


        Call call = apiService.getBookletsList(headerMap, store_id);
        call.enqueue(callbackApi);
    }


    public void getDinnersList(String lang) {


        Log.i(TAG, "Log getDinnersList");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log lang " + lang);


        Call call = apiService.getDinnersList(headerMap, lang);
        call.enqueue(callbackApi);
    }


    public void getSingleDinner(int dinner_id, String lan) {


        Log.i(TAG, "Log getSingleDinner");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log lang " + lan);
        Log.i(TAG, "Log dinner_id " + dinner_id);


        Call call = apiService.getSingleDinner(headerMap, dinner_id, lan);
        call.enqueue(callbackApi);
    }

    public void getAppRate() {

        Map<String, Object> params = new HashMap<>();

        Log.i(TAG, "Log getAppRate");
        Log.i(TAG, "Log headerMap " + headerMap);


        Call call = apiService.getAppRate(headerMap, params);
        call.enqueue(callbackApi);
    }


    public void setRate(ReviewModel reviewModel) {

        Map<String, Object> params = new HashMap<>();
        params.put("store_id", reviewModel.getStoreId());
        params.put("user_id", reviewModel.getUser_id());
        params.put("product_id", reviewModel.getProductId());
        params.put("rate", reviewModel.getRate());
        params.put("comment", reviewModel.getComment());


        Log.i(TAG, "Log setRate");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log store_ID " + reviewModel.getStoreId());
        Log.i(TAG, "Log product_id " + reviewModel.getProductId());
        Log.i(TAG, "Log rate " + reviewModel.getRate());
        Log.i(TAG, "Log comment " + reviewModel.getComment());


        Call call = apiService.setRate(headerMap, params);
        call.enqueue(callbackApi);
    }

    public void setAppRate(ReviewModel reviewModel) {

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", reviewModel.getUser_id());
        params.put("rate", reviewModel.getRate());
        params.put("comment", reviewModel.getComment());

        Log.i(TAG, "Log setAppRate");
        Log.i(TAG, "Log headerMap " + headerMap);

        Log.i(TAG, "Log user_id " + reviewModel.getUser_id());
        Log.i(TAG, "Log rate " + reviewModel.getRate());
        Log.i(TAG, "Log comment " + reviewModel.getComment());


        Call call = apiService.setAppRate(headerMap, params);
        call.enqueue(callbackApi);
    }


    public void getSetting() {

        Log.i(TAG, "Log getSetting");

        lang = UtilityApp.getLanguage();
        if (lang != null) {
            lang = UtilityApp.getLanguage();
        } else {
            lang = Locale.getDefault().getLanguage();

        }

        Log.i(TAG, "Log lang " + lang);


        Call call = apiService.getSetting(headerMap, lang);
        call.enqueue(callbackApi);
    }


    public void getValidate(String device_type, String app_version, int app_build) {

        Log.i(TAG, "Log getValidate");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log app_version " + app_version);
        Log.i(TAG, "Log app_build " + app_build);

        Call call = apiService.getValidate(headerMap, device_type, app_version, app_build);
        call.enqueue(callbackApi);
    }


    public void getLinks(String country_shortname) {

        Log.i(TAG, "Log getLinks");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_shortname " + country_shortname);

        Call call = apiService.getSocialLink(headerMap, country_shortname);
        call.enqueue(callbackApi);
    }


    public void getOrders(int user_id, String type, String filter) {

        Log.i(TAG, "Log getOrders");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log type " + type);
        Log.i(TAG, "Log filter " + filter);
        orderListCall orderListCall = new orderListCall();
        orderListCall.setUserId(user_id);
        orderListCall.setType(type);
        orderListCall.setFilter(filter);

        Call call = apiService.GetOrdersList(headerMap, orderListCall);
        call.enqueue(callbackApi);
    }

    public void getOrderDetails(int order_id, int user_id, int store_id, String type) {

        Log.i(TAG, "Log getOrderDetails");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log type " + type);
        Log.i(TAG, "Log store_id " + store_id);
        Log.i(TAG, "Log store_id " + order_id);
        Log.i(TAG, "Log user_id " + user_id);

        orderListCall orderListCall = new orderListCall();
        orderListCall.setOrderId(order_id);
        orderListCall.setUserId(user_id);
        orderListCall.setStoreId(store_id);
        orderListCall.setType(type);

        Call call = apiService.GetOrderDetails(headerMap, orderListCall);
        call.enqueue(callbackApi);
    }



}