//package com.example.myapplication.Config;
//
//import android.content.Context;
//import android.util.Log;
//
//import java.io.IOException;
//
//import mobileapp.routier.Utils.SharedPrefUtils;
//import mobileapp.routier.Utils.Tags;
//import okhttp3.Authenticator;
//import okhttp3.Request;
//import okhttp3.Route;
//
//public class AccessTokenAuthenticator implements Authenticator {
//
//    private Context context;
//    public AccessTokenAuthenticator(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public Request authenticate(Route route, okhttp3.Response response) throws IOException {
//        synchronized (this){
//            if(response.request().headers().get("Authorization") != null){
//                Log.e(Tags.AUTHENTICATION, "AT EXPIRED - Removing key: " + response.request().headers().get("Authorization"));
//                SharedPrefUtils.remove(context, SharedPrefUtils.accessTokenKey);
//            }else{
//                Log.e(Tags.AUTHENTICATION, "ERORR AUTHENTICATION CALLED WITHOUT access token");
//            }
//            return null;
//        }
//
//    }
//}
