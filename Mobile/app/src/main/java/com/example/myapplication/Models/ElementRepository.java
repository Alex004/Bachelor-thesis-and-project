package com.example.myapplication.Models;

import android.content.Context;

import com.example.myapplication.Config.RetrofitFactory;
import com.example.myapplication.Data.Local.Code.CodeEntity;
import com.example.myapplication.Data.Local.Element.ElementEntity;
import com.example.myapplication.Data.Local.Element.ElementRegionEntity;
import com.example.myapplication.Data.Local.Region.RegionEntity;
import com.example.myapplication.Data.Local.Route.RouteEntity;
import com.example.myapplication.Data.Remote.WebService;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;

public class ElementRepository {
    private Context context;
    private WebService webService;

    public ElementRepository(Context context)
    {
        this.context = context;
        this.webService = RetrofitFactory.getRetrofit(context).create(WebService.class);
    }

    public Single<Response<JsonObject>> test()
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("mesaj","A mers ceva");
        return webService.test(obj);
    }

    public Single<Response<JsonObject>> checkLocation(String idLocation) {
        return webService.checkLocation(idLocation);
    }

    public Single<Response<List<RouteEntity>>> getRoute(String location){
        return webService.getRoute(location);
    }

    public Single<Response<List<CodeEntity>>> getCode(String location){
        return webService.getCode(location);
    }

    public Single<Response<List<ElementEntity>>> getElements(String location){
        return webService.getElements(location);
    }

    public Single<Response<List<RegionEntity>>> getRegion(String location){
        return webService.getRegion(location);
    }

    public Single<Response<List<ElementRegionEntity>>> getElementRegion(String location){
        return webService.getElementRegion(location);
    }


}
