package com.example.myapplication.Data.Remote;

import com.example.myapplication.Data.Local.Code.CodeEntity;
import com.example.myapplication.Data.Local.Element.ElementEntity;
import com.example.myapplication.Data.Local.Element.ElementRegionEntity;
import com.example.myapplication.Data.Local.Region.RegionEntity;
import com.example.myapplication.Data.Local.Route.RouteEntity;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface WebService {
    @POST("/test/")
    Single<Response<JsonObject>> test(
            @Body JsonObject obj

    );

    @GET("/getInfoLocation/")
    Single<Response<JsonObject>> checkLocation(@Header("idLocation") String idLocation);

    @GET("/getRoute/")
    Single<Response<List<RouteEntity>>> getRoute(@Header("location") String location);

    @GET("/getCode/")
    Single<Response<List<CodeEntity>>> getCode(@Header("location") String location);

    @GET("/getRegion/")
    Single<Response<List<RegionEntity>>> getRegion(@Header("location") String location);

    @GET("/getElementRegion/")
    Single<Response<List<ElementRegionEntity>>> getElementRegion(@Header("location") String location);

    @GET("/getElements/")
    Single<Response<List<ElementEntity>>> getElements(@Header("location") String location);
}
