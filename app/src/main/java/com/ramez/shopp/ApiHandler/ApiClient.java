package com.ramez.shopp.ApiHandler;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.LocalModel;


import java.util.concurrent.TimeUnit;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    public static String BASE_URL = "";
    private static Retrofit retrofit = null;
    private static Retrofit retrofitCustom = null;
    private static Retrofit retrofitLong = null;
    private static String country;

    private static ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS).supportsTlsExtensions(true).tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0).cipherSuites(CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA, CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA, CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA, CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA).build();

    public static Retrofit getCustomClient(String baseUrl) {
        Gson gson = new GsonBuilder().setLenient().create();

//        if (retrofit == null) {
        retrofitCustom = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create(gson)).client(getOkClient()).build();
//        }
        return retrofitCustom;
    }


    public static Retrofit getClient() {

        LocalModel localModel = UtilityApp.getLocalData();

        if (localModel != null && localModel.getShortname() != null) {

            country = localModel.getShortname();
            Log.i("TAG", "Log country Local  " + country);
        } else {
            country = GlobalData.COUNTRY;
            Log.i("TAG", "Log country " + country);
        }
        BASE_URL = GlobalData.BetaBaseURL + country + GlobalData.grocery + GlobalData.Api;
        Log.i("TAG", "Log BASE_URL " + BASE_URL);

        Gson gson = new GsonBuilder().setLenient().create();
//        if (retrofit == null) {
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).client(getOkClient()).build();
//        }

        return retrofit;
    }

    public static Retrofit getLongClient() {
        LocalModel localModel = UtilityApp.getLocalData();

        if (localModel != null && localModel.getShortname() != null) {
            country = localModel.getShortname();

        } else {
            country = GlobalData.COUNTRY;

        }
        BASE_URL = GlobalData.BetaBaseURL + country + GlobalData.grocery + GlobalData.Api;

        Gson gson = new GsonBuilder().setLenient().create();


//        if (retrofitLong == null) {
        retrofitLong = new Retrofit.Builder()
                .baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).client(getLongOkClient()).build();
//        }
        return retrofitLong;
    }

//    private static OkHttpClient getOkClient() {
//
//        List<ConnectionSpec> specs = new ArrayList<>();
//        specs.add(spec);
//        specs.add(ConnectionSpec.COMPATIBLE_TLS);
//        specs.add(ConnectionSpec.CLEARTEXT);
//
//        OkHttpClient.Builder client = new OkHttpClient.Builder()
////                .addInterceptor(interceptor)
//                .connectionSpecs(specs).connectTimeout(30, TimeUnit.SECONDS)
//                .readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS);
//
//
//        try {
////            ProviderInstaller.installIfNeeded(RootApplication.getInstance());
//
//            // Create a trust manager that does not validate certificate chains
//            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                @Override
//                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                }
//
//                @Override
//                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                }
//
//                @Override
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return new java.security.cert.X509Certificate[]{};
//                }
//            }};
//
//
//            client.sslSocketFactory(new TLSSocketFactory(), (X509TrustManager) trustAllCerts[0]);
//            client.hostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return client.build();
//    }

    private static OkHttpClient getOkClient() {
        Interceptor interceptor = chain -> {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("Accept", "application/json,*/*")
                    .header("Connection", "close")
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };

        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

//    private static OkHttpClient getLongOkClient() {
//
//        List<ConnectionSpec> specs = new ArrayList<>();
//        specs.add(spec);
//        specs.add(ConnectionSpec.COMPATIBLE_TLS);
//        specs.add(ConnectionSpec.CLEARTEXT);
//
//        OkHttpClient.Builder client = new OkHttpClient.Builder()
////                .addInterceptor(interceptor)
//                .connectionSpecs(specs).connectTimeout(10,
//                        TimeUnit.MINUTES).readTimeout(10,
//                        TimeUnit.MINUTES).writeTimeout(10, TimeUnit.MINUTES);
//
//        try {
////            ProviderInstaller.installIfNeeded(RootApplication.getInstance());
//
//            // Create a trust manager that does not validate certificate chains
//            final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                @Override
//                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                }
//
//                @Override
//                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
//                }
//
//                @Override
//                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                    return new java.security.cert.X509Certificate[]{};
//                }
//            }};
//
//            // Install the all-trusting trust manager
////            final SSLContext sslContext = SSLContext.getInstance("SSL");
////            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//
//            // Create an ssl socket factory with our all-trusting manager
////            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//            client.sslSocketFactory(new TLSSocketFactory(), (X509TrustManager) trustAllCerts[0]);
////            client.sslSocketFactory(new TLSSocketFactory());
//            client.hostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return client.build();
//    }

    private static OkHttpClient getLongOkClient() {

        Interceptor interceptor = chain -> {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Connection", "close")
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        };

        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(20, TimeUnit.MINUTES)
                .readTimeout(20, TimeUnit.MINUTES)
                .writeTimeout(20, TimeUnit.MINUTES)
                .build();
    }

}
