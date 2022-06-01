package com.amour.shop.ApiHandler

import android.util.Log
import com.google.gson.GsonBuilder
import com.amour.shop.BuildConfig
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData
import com.amour.shop.classes.UtilityApp
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {
    var BASE_URL = ""
    private var retrofit: Retrofit? = null
    private var retrofitLong: Retrofit? = null
    private var country: String? = null
    var apiPath = ""

    fun getClient(currBaseUrl: String?): Retrofit? {
        val localModel = UtilityApp.getLocalData()
        if (localModel.shortname != null) {
            country = localModel.shortname
            Log.i("TAG", "Log country Local  $country")
        } else {
            country = GlobalData.COUNTRY
            Log.i("TAG", "Log country $country")
        }
        // will read from shared
        apiPath = GlobalData.amour
        country = Constants.default_amour_short_name

        BASE_URL = "$currBaseUrl$country$apiPath${GlobalData.Api}"
        //        BASE_URL = UtilityApp.getUrl() + country + GlobalData.grocery + GlobalData.Api;
        Log.i("TAG", "Log BASE_URL $BASE_URL")
        val gson = GsonBuilder().setLenient().create()
        //        if (retrofit == null) {
        retrofit = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).client(okClient).build()
        //        }
        return retrofit
    }

    fun getLongClient(currBaseUrl: String?): Retrofit? {
        val localModel = UtilityApp.getLocalData()
        country = if (localModel.shortname != null) {
            localModel.shortname
        } else {
            GlobalData.COUNTRY
        }


        apiPath = GlobalData.amour
        country = Constants.default_amour_short_name
        BASE_URL = "$currBaseUrl$country$apiPath${GlobalData.Api}"

        val gson = GsonBuilder().setLenient().create()


//        if (retrofitLong == null) {
        retrofitLong = Retrofit.Builder()
            .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson))
            .client(getLongOkClient()).build()
        //        }
        return retrofitLong
    }

    private val okClient: OkHttpClient
        get() {
            val interceptor = Interceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Accept", "application/json,*/*")
                    .header("Connection", "close")
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
            return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }

    private fun getLongOkClient(): OkHttpClient {
        val interceptor = Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Accept", "application/json")
                .header("Connection", "close")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(20, TimeUnit.MINUTES)
            .readTimeout(20, TimeUnit.MINUTES)
            .writeTimeout(20, TimeUnit.MINUTES)
            .build()
    }
}