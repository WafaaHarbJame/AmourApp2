package com.ramez.shopp.ApiHandler;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.ramez.shopp.Models.CategoryModel;
import com.ramez.shopp.Models.CountryModel;
import com.ramez.shopp.Models.request.ProductRequest;
import com.ramez.shopp.classes.Constants;
import com.ramez.shopp.classes.FilterModel;
import com.ramez.shopp.classes.GlobalData;
import com.ramez.shopp.Models.OtpModel;
import com.ramez.shopp.classes.SortModel;
import com.ramez.shopp.classes.UtilityApp;
import com.ramez.shopp.Models.orderListCall;
import com.ramez.shopp.Models.AddExtraCall;
import com.ramez.shopp.Models.AddressModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.LoginResultModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.OrderCall;
import com.ramez.shopp.Models.QuickCall;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.Models.ReviewModel;
import com.ramez.shopp.activities.RegisterLoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mobi.foo.benefitinapp.data.Transaction;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DataFeacher {
    final String TAG = "Log";
    final String LOGIN_URL = "/" + GlobalData.COUNTRY + "/GroceryStoreApi/api/v9/Account/login";
    DataFetcherCallBack dataFetcherCallBack;
    ApiInterface apiService;
    //    int city;
    String lang;
    Map<String, Object> headerMap = new HashMap<>();
    private Callback callbackApi;
    private Activity activity;

    public DataFeacher() {
        apiService = ApiClient.getClient().create(ApiInterface.class);
        this.dataFetcherCallBack = (obj, func, IsSuccess) -> {

        };

        String token = UtilityApp.getToken() != null ? UtilityApp.getToken() : "token";

        headerMap.put("ApiKey", Constants.api_key);
        headerMap.put("device_type", Constants.deviceType);
        headerMap.put("app_version", UtilityApp.getAppVersionStr());
        headerMap.put("token", token);
        headerMap.put("Accept", "application/json");
        headerMap.put("Content-Type", "application/json");
        String accessToken = UtilityApp.getUserToken();
        if (!accessToken.isEmpty()) {
            headerMap.put("Authorization", Constants.TOKEN_PREFIX.concat(accessToken));

        }

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

                    if (response.code() == 404 || response.code() == 443) {
                        System.out.println("Log 404 not found change url");
                        changeUrl();
                        dataFetcherCallBack.Result(errorModel, Constants.ERROR, false);
                    } else if (response.code() == 401) {
                        dataFetcherCallBack.Result(errorModel, Constants.UnAuthorize, false);

                    } else {
                        dataFetcherCallBack.Result(errorModel, Constants.ERROR, false);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
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

        String token = UtilityApp.getToken() != null ? UtilityApp.getToken() : "token";
        headerMap.put("ApiKey", Constants.api_key);
        headerMap.put("device_type", Constants.deviceType);
        headerMap.put("app_version", UtilityApp.getAppVersionStr());
        headerMap.put("token", token);
        headerMap.put("Accept", "application/json");
        headerMap.put("Content-Type", "application/json");
        String accessToken = UtilityApp.getUserToken();
        if (!accessToken.isEmpty()) {
            headerMap.put("Authorization", Constants.TOKEN_PREFIX.concat(accessToken));
//            headerMap.put("Authorization",Constants.TOKEN_PREFIX +"euuJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRhbGFsLmF3YWRpMkBnbWFpbC5jb20iLCJuYW1lIjoiMzMzMTA0ODgiLCJuYW1laWQiOiIxMTgiLCJqdGkiOiI1MmYyZmM5ZS0yNjhlLTQ4OWEtYjJhYi0wYjhjODkwNzNmMTkiLCJuYmYiOjE2NDEzODI4NDksImV4cCI6MTY0MTQwMjY0OSwiaWF0IjoxNjQxMzgyODQ5LCJpc3MiOiJodHRwczovL3Jpc3RlaC5jb20iLCJhdWQiOiJodHRwczovL3Jpc3RlaC5jb20vIn0.7eL6bF9uVQ3a6b6QH9QoNq1BY2nYhFLvxYnDV2D_5V8");

        }
        callbackApi = new Callback() {
            @Override
            public void onResponse(Call call, Response response) {

                String url = call.request().url().url().getPath();
                if (response.isSuccessful()) {
                    System.out.println("Log url " + call.request().url());

                    Log.i("Log", "Log errorApiUrl " + url);

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
                        Log.i("Log", "Log errorApiUrl " + url);
                        Log.e("Log", "Log error " + error);
                        Log.e("Log", "Log error code  " + response.code());
                        errorModel = new Gson().fromJson(error, new TypeToken<ResultAPIModel>() {
                        }.getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (response.code() == 404 || response.code() == 443) {
                        System.out.println("Log 404 not found change url");
                        changeUrl();
                        dataFetcherCallBack.Result(errorModel, Constants.ERROR, false);
                    } else if (response.code() == 401) {
                        dataFetcherCallBack.Result(errorModel, Constants.UnAuthorize, false);

                    } else {
                        dataFetcherCallBack.Result(errorModel, Constants.ERROR, false);
                    }

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

    private void changeUrl() {

        String url = UtilityApp.getUrl();
        if (url.equals(GlobalData.BetaBaseURL1)) {
            UtilityApp.setUrl(GlobalData.BetaBaseURL2);

        } else {
            UtilityApp.setUrl(GlobalData.BetaBaseURL1);

        }


    }

    public void loginHandle(MemberModel memberModel) {

        Map<String, Object> params = new HashMap<>();
        params.put("mobile_number", memberModel.getMobileNumber());
        params.put("password", memberModel.getPassword());
        params.put("user_type", memberModel.getUserType());
        params.put("device_type", Constants.deviceType);
        params.put("device_token", memberModel.getDeviceToken() + "");
        params.put("device_id", memberModel.getDeviceToken() + "");
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


    public void RefreshToken(String token, String refreshToken) {

        Map<String, Object> params = new HashMap<>();
        params.put("token", token);
        params.put("refreshToken", refreshToken);

        Log.i(TAG, "Log RefreshToken");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log token " + token);
        Log.i(TAG, "Log refreshToken " + refreshToken);

        Call call = apiService.RefreshToken(headerMap, params);
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
        params.put("mobile_number", memberModel.getMobileNumber() + "");
        params.put("password", memberModel.getPassword() + "");
        params.put("user_type", Constants.user_type + "");
        params.put("device_type", memberModel.getDeviceType() + "");
        params.put("device_token", memberModel.getDeviceToken() + "");
        params.put("name", memberModel.getName() + "");
        params.put("country", memberModel.getCountry() + "");
        params.put("city", memberModel.getCity() + "");
        params.put("email", memberModel.getEmail() + "");
        params.put("device_id", memberModel.getDeviceId() + "");
        params.put("prefix", memberModel.getPrefix() + "");

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
        Log.i(TAG, "Log EMAIL " + "");

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
        Log.i(TAG, "Log headerMap " + headerMap);

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

    public void CityHandle(int country_id, Activity activity) {

        Log.i(TAG, "Log CityHandle");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log country_id " + country_id);

        String countryCode = "";
        LocalModel localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(activity);

        if (localModel.getShortname() != null) {
            countryCode = localModel.getShortname();

        } else {
            countryCode = GlobalData.COUNTRY;
        }


        String url = UtilityApp.getUrl() + countryCode + "/GroceryStoreApi/api/v9/Locations/citiesByCountry";

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


    public void getCountryDetail(String shortName) {

        Log.i(TAG, "Log getCountryDetail");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log lan " + lang);
        Log.i(TAG, "Log shortName " + shortName);

        Map<String, Object> params = new HashMap<>();
        lang = UtilityApp.getLanguage();
        if (lang != null) {
            params.put("lan", lang);
        } else {
            params.put("lan", Locale.getDefault().getLanguage());

        }
        params.put("shortname", shortName);


        Call call = apiService.getCountryDetail(headerMap, params);
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
        Log.i(TAG, "Log GetMainPage headerMap " + headerMap);
        Log.i(TAG, "Log GetMainPage country_id " + country_id);
        Log.i(TAG, "Log GetMainPage category_id " + category_id);
        Log.i(TAG, "Log GetMainPage user_id " + user_id);
        Log.i(TAG, "Log GetMainPage city_id " + city_id);

        Call call = apiService.GetMainPage(headerMap, category_id, country_id, city_id, user_id);
        call.enqueue(callbackApi);
    }

    public void GetSingleProduct(int country_id, int city_id, int product_id, String user_id) {
//        product_id = ;

        Log.i(TAG, "Log GetSingleProduct");
        Log.i(TAG, "Log  GetSingleProduct headerMap " + headerMap);
        Log.i(TAG, "Log  GetSingleProduct country_id " + country_id);
        Log.i(TAG, "Log  GetSingleProduct product_id " + product_id);
        Log.i(TAG, "Log  GetSingleProduct user_id " + user_id);
        Log.i(TAG, "Log  GetSingleProduct city_id " + city_id);

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


    public void getAllKindsList() {

        Log.i(TAG, "Log getAllKindsList");
        Log.i(TAG, "Log headerMap " + headerMap);

        Call call = apiService.getAllKindsList(headerMap);
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
        Log.i(TAG, "Log GetCarts headerMap " + headerMap);

        Log.i(TAG, "Log GetCarts storeId  " + sotre_id);
        Log.i(TAG, "Log GetCarts userId " + user_id);

        Call call = apiService.GetACarts(headerMap, user_id, sotre_id);
        call.enqueue(callbackApi);


    }


    public void getDeliveryTimeList(int sotre_id, int user_id) {

        Log.i(TAG, "Log checkCart");
        Log.i(TAG, "Log headerMap " + headerMap);

        Log.i(TAG, "Log sotre_id " + sotre_id);
        Log.i(TAG, "Log user_id " + user_id);

        Call call = apiService.checkCart(headerMap, user_id, sotre_id);
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


    public void getUserDetails(int user_id, int store_id) {

        Log.i(TAG, "Log getUserDetails");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log store_id " + store_id);

        Call call = apiService.getUserDetail(headerMap, user_id, store_id);
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

    public void addToFastQCart(int city_id, String user_id, String filter) {

        Log.i(TAG, "Log addToFastQCart");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log barcode " + filter);
        Log.i(TAG, "Log city_id " + city_id);
        Log.i(TAG, "Log user_id " + user_id);

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("barcode", filter);
        params.put("store_id", city_id);

        Call call = apiService.addToFastQCart(headerMap, params);
        call.enqueue(callbackApi);
    }


    public void updateCartFastQ(int id, int qty, int city_id, String user_id) {

        Log.i(TAG, "Log updateCartFastQ");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log id " + id);
        Log.i(TAG, "Log qty " + qty);
        Log.i(TAG, "Log user_id " + user_id);
        Log.i(TAG, "Log store_id " + city_id);

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("id", id);
        params.put("qty", qty);
        params.put("store_id", city_id);

        Call call = apiService.updateFastQCart(headerMap, params);
        call.enqueue(callbackApi);
    }


    public void getFastQCarts(int city_id, String user_id) {

        Log.i(TAG, "Log getFastQCarts");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log store_id " + city_id);
        Log.i(TAG, "Log user_id " + user_id);

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("store_id", city_id);

        Call call = apiService.getFastQCarts(headerMap, params);
        call.enqueue(callbackApi);
    }


    public void generateOrders(int city_id, String user_id, String Cashier) {

        Log.i(TAG, "Log generateOrders");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log store_id " + city_id);
        Log.i(TAG, "Log user_id " + user_id);

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("store_id", city_id);
        params.put("Cashier", Cashier);

        Call call = apiService.generateOrders(headerMap, params);
        call.enqueue(callbackApi);


    }


    public void GetSetting(int city_id, String user_id) {

        Log.i(TAG, "Log Get Fastq Setting");
        Log.i(TAG, "Log Get Fastq headerMap " + headerMap);
        Log.i(TAG, "Log Get Fastq store_id " + city_id);
        Log.i(TAG, "Log Get Fastq  user_id " + user_id);

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("store_id", city_id);

        Call call = apiService.GetSetting(headerMap, params);
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


    public Call getFavorite(ProductRequest productRequest) {
        //int kind_id, String srots, int category_id, int country_id, int city_id, String user_id, String filter, int brand_id, int page_number, int page_size

        Map<String, Object> params = new HashMap<>();

        FilterModel filterModel = new FilterModel();
        List<FilterModel> filtersList = new ArrayList<FilterModel>();
        filtersList.add(filterModel);

        String filtersJson = new Gson().toJson(filtersList);
        JSONArray filtersJsonArr = null;
        try {
            filtersJsonArr = new JSONArray(filtersJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SortModel sortModel = new SortModel();
        List<SortModel> sortList = new ArrayList<SortModel>();
        sortList.add(sortModel);
        String sortJson = new Gson().toJson(sortList);
        JSONArray sortJsonArr = null;
        try {
            sortJsonArr = new JSONArray(sortJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        params.put("category_id", productRequest.getCategoryId());
        params.put("country_id", productRequest.getCountryId());
        params.put("city_id", productRequest.getCityId());
        params.put("filter", productRequest.getFilter());
        params.put("brand_id", productRequest.getBrandId());
        params.put("page_number", productRequest.getPageNumber());
        params.put("page_size", productRequest.getPageSize());
        params.put("kind_id", productRequest.getKindId());
//        if (filtersList != null && !filtersList.isEmpty())
//        params.put("filters", filtersJsonArr);
//        params.put("srots", sortJsonArr);

        Log.i(TAG, "Log getFavorite");
        Log.i(TAG, "Log getFavorite headerMap " + headerMap);
        Log.i(TAG, "Log params " + params);

        Call call = apiService.GetFavoriteProducts(headerMap, params);
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


    public void makeOrder(OrderCall orderCalls) {
//        OrderCall orderCall = new OrderCall();
//        orderCall.user_id = orderCalls.user_id;
//        orderCall.store_ID = orderCalls.store_ID;
//        orderCall.address_id = orderCalls.address_id;
//        orderCall.payment_method = orderCalls.payment_method;
//        orderCall.coupon_code_id = orderCalls.coupon_code_id;
//        orderCall.delivery_date_id = orderCalls.delivery_date_id;
//        orderCall.expressDelivery = orderCalls.expressDelivery;
//        orderCall.itemNotFoundAction=orderCalls.itemNotFoundAction;

        Log.i(TAG, "Log makeOrder");
        Log.i(TAG, "Log headerMap " + headerMap);
//        Log.i(TAG, "Log user_id " + orderCalls.user_id);
        Log.i(TAG, "Log store_ID " + orderCalls.store_ID);
        Log.i(TAG, "Log addressId " + orderCalls.address_id);
        Log.i(TAG, "Log payment_method " + orderCalls.payment_method);
        Log.i(TAG, "Log coupon_code_id " + orderCalls.coupon_code_id);
        Log.i(TAG, "Log delivery_date_id " + orderCalls.delivery_date_id);
        Log.i(TAG, "Log expressDelivery " + orderCalls.expressDelivery);
        Log.i(TAG, "Log itemNotFoundAction " + orderCalls.itemNotFoundAction);
        Log.i(TAG, "Log payToken " + orderCalls.pay_token);
        Log.i(TAG, "Log delivery_type " + orderCalls.delivery_type);

        Call call = apiService.makeOrder(headerMap, orderCalls);
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


    public void saveSucessTransactionD(Transaction transaction, Integer orderId) {

        Map<String, Object> params = new HashMap<>();
        params.put("terminalId", transaction.getTerminalId());
        params.put("cardNumber", transaction.getCardNumber());
        params.put("transactionMessage", transaction.getTransactionMessage());
        params.put("referenceNumber", transaction.getReferenceNumber());
        params.put("amount", transaction.getAmount());
        params.put("orderid", orderId);


        Log.i(TAG, "Log saveTransactionData");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log merchant " + transaction.getMerchant());
        Log.i(TAG, "Log transactionMessage " + transaction.getTransactionMessage());
        Log.i(TAG, "Log reference number " + transaction.getReferenceNumber());
        Log.i(TAG, "Log amount " + transaction.getAmount());
        Log.i(TAG, "Log ordeId " + orderId);


        Call call = apiService.PaySuccess(headerMap, params);
        call.enqueue(callbackApi);
    }

    public void saveFailTransaction(Transaction transaction, Integer orderId) {

        Map<String, Object> params = new HashMap<>();
        params.put("terminalId", transaction.getTerminalId());
        params.put("cardNumber", transaction.getCardNumber());
        params.put("transactionMessage", transaction.getTransactionMessage());
        params.put("referenceNumber", transaction.getReferenceNumber());
        params.put("amount", transaction.getAmount());
        params.put("orderid", orderId);
        Log.i(TAG, "Log saveTransactionData");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log merchant " + transaction.getMerchant());
        Log.i(TAG, "Log transactionMessage " + transaction.getTransactionMessage());
        Log.i(TAG, "Log reference number " + transaction.getReferenceNumber());
        Log.i(TAG, "Log amount " + transaction.getAmount());
        Log.i(TAG, "Log ordeId " + orderId);


        Call call = apiService.PayError(headerMap, params);
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


    public void getLinks(int store_id) {

        Log.i(TAG, "Log getLinks");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log store_id " + store_id);

        Call call = apiService.getSocialLink(headerMap, store_id);
        call.enqueue(callbackApi);
    }


    public void getOrders(int user_id, String type, String filter) {

        Log.i(TAG, "Log getOrders");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log type " + type);
        Log.i(TAG, "Log filter " + filter);
        Log.i(TAG, "Log user_id " + user_id);
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


    public void GetDeliveryInfo(int store_ID, int address_id) {

        Map<String, Object> params = new HashMap<>();
        params.put("address_id", address_id);
        params.put("store_ID", store_ID);

        Log.i(TAG, "Log GetDeliveryInfo");
        Log.i(TAG, "Log headerMap " + headerMap);
        Log.i(TAG, "Log address_id " + address_id);
        Log.i(TAG, "Log store_ID " + store_ID);

        Call call = apiService.GetDeliveryInfo(headerMap, params);
        call.enqueue(callbackApi);

    }

    private void loginUser(MemberModel memberModel) {


        new DataFeacher(false, (obj, func, IsSuccess) -> {


            if (IsSuccess) {
                LoginResultModel result = (LoginResultModel) obj;

                if (result != null) {
                    if (result.getStatus() == 200) {

                        MemberModel user = result.getData();
                        user.setPassword(memberModel.getPassword());
                        user.setMobileNumber(memberModel.getMobileNumber());
                        UtilityApp.setUserData(user);
                        UtilityApp.setUserToken(result.getToken());
                        UtilityApp.setRefreshToken(result.getRefreshToken());


                    } else {
                        Intent intent = new Intent(activity, RegisterLoginActivity.class);
                        intent.putExtra(Constants.LOGIN, true);
                        activity.startActivity(intent);

                    }
                }
            }


        }).loginHandle(memberModel);

    }


}