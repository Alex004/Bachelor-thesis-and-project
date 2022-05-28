package com.example.myapplication.Config;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.google.gson.GsonBuilder;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private static Retrofit retrofit = null;
    private static Retrofit mockRetrofit = null;

    private RetrofitFactory() {
    }

    public static void initializeRetrofit(Context context, String base_url) {
//        // Create a holder for the request service
////        BackendServiceHolder holder = new BackendServiceHolder();
//
//        // Create logging interceptor
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        // Create authenticator - TO ADD REFRESH TOKEN SOON
////            RefreshTokenAuthenticator authenticator = new RefreshTokenAuthenticator(context,holder);
////        AccessTokenAuthenticator authenticator = new AccessTokenAuthenticator(context);
//
//        // Create auth interceptor
//        Interceptor authInterceptor = chain -> {
//            okhttp3.Request request = chain.request();
//
//            SharedPreferences preferences = context.getSharedPreferences(SharedPrefUtils.fileKey, Context.MODE_PRIVATE);
//            String accessToken = preferences.getString(SharedPrefUtils.accessTokenKey, null);
//            if (accessToken == null) {
//                return chain.proceed(request);
//            }
//
//            Headers headers = request.headers().newBuilder().add("Authorization", "Bearer " + accessToken).build();
//            request = request.newBuilder().headers(headers).build();
//            return chain.proceed(request);
//        };
//
        try {
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init((KeyStore) null);
//
//            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
//            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
//                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
//            }
//
//            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, new TrustManager[]{trustManager}, null);
//            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
            // Build HttpClient
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

            OkHttpClient httpClient = builder
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
//                    .sslSocketFactory(sslSocketFactory, trustManager)
//                    .addInterceptor(logging)
//                    .addInterceptor(authInterceptor)
//                    .authenticator(authenticator)
                    .build();

            GsonBuilder gsonBuilder = new GsonBuilder();
//                 Build Retrofit
            retrofit = new Retrofit.Builder()
                    .baseUrl(base_url)
                          .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build();

//            holder.set(retrofit.create(CommonWebService.class));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initializeMockRetrofit(Context context, String base_url) {
        // Create logging interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

//        AccessTokenAuthenticator authenticator = new AccessTokenAuthenticator(context);

        try {
            // Build HttpClient
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder();

            OkHttpClient httpClient = builder
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
//                    .authenticator(authenticator)
                    .build();

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapterFactory(new ItemTypeAdapterFactory());
            retrofit= new Retrofit.Builder()
                    .baseUrl(base_url)
                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(httpClient)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Retrofit getRetrofit(Context context) {
        if (retrofit == null)
            initializeRetrofit(context, "https://licenta2021.herokuapp.com");
//            initializeRetrofit(context, "http://192.168.43.185:5006");
//            initializeMockRetrofit(context, "http://192.168.43.185");

        return retrofit;
    }

//    public static Retrofit getMockRetrofit(Context context) {
//        if (mockRetrofit == null)
//            initializeMockRetrofit(context, "http://192.168.100.12:5006");
//
//        return mockRetrofit;
//    }
}
