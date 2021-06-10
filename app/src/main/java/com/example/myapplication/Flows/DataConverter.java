package com.example.myapplication.Flows;

import androidx.room.TypeConverter;

import com.example.myapplication.Data.Local.Code.CodeEntity;
import com.example.myapplication.Data.Local.Element.ElementEntity;
import com.example.myapplication.Data.Local.Element.ElementRegionEntity;
import com.example.myapplication.Data.Local.Region.RegionEntity;
import com.example.myapplication.Data.Local.Route.RouteEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class DataConverter implements Serializable {

    @TypeConverter
    public String fromRouteEntityList(List<RouteEntity> routeEntityList)
    {
        if (routeEntityList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<RouteEntity>>() {
        }.getType();

        String json = gson.toJson(routeEntityList, type);
        return json;
    }

    @TypeConverter
    public List<RouteEntity> toRouteEntityList(String routeEntityListString) {
        if (routeEntityListString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<RouteEntity>>() {
        }.getType();

        List<RouteEntity> routeEntities = gson.fromJson(routeEntityListString, type);
        return routeEntities;
    }

    @TypeConverter
    public String fromCodeEntityList(List<CodeEntity> codeEntityList)
    {
        if (codeEntityList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<CodeEntity>>() {
        }.getType();

        String json = gson.toJson(codeEntityList, type);
        return json;
    }

    @TypeConverter
    public List<CodeEntity> toCodeEntityList(String codeEntityListString) {
        if (codeEntityListString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<CodeEntity>>() {
        }.getType();

        List<CodeEntity> codeEntities = gson.fromJson(codeEntityListString, type);
        return codeEntities;
    }

    @TypeConverter
    public String fromRegionEntityList(List<RegionEntity> regionEntityList)
    {
        if (regionEntityList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<RegionEntity>>() {
        }.getType();

        String json = gson.toJson(regionEntityList, type);
        return json;
    }

    @TypeConverter
    public List<RegionEntity> toRegionEntityList(String regionEntityListString) {
        if (regionEntityListString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<RegionEntity>>() {
        }.getType();

        List<RegionEntity> regionEntities = gson.fromJson(regionEntityListString, type);
        return regionEntities;
    }

    @TypeConverter
    public String fromElementEntityList(List<ElementEntity> elementEntityList)
    {
        if (elementEntityList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ElementEntity>>() {
        }.getType();

        String json = gson.toJson(elementEntityList, type);
        return json;
    }

    @TypeConverter
    public List<ElementEntity> toElementEntityList(String elementEntityListString) {
        if (elementEntityListString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ElementEntity>>() {
        }.getType();

        List<ElementEntity> elementEntities = gson.fromJson(elementEntityListString, type);
        return elementEntities;
    }

    @TypeConverter
    public String fromElementRegionEntityList(List<ElementRegionEntity> elementRegionEntityList)
    {
        if (elementRegionEntityList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ElementRegionEntity>>() {
        }.getType();

        String json = gson.toJson(elementRegionEntityList, type);
        return json;
    }

    @TypeConverter
    public List<ElementRegionEntity> toElementRegionEntityList(String elementRegionEntityListString) {
        if (elementRegionEntityListString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<ElementRegionEntity>>() {
        }.getType();

        List<ElementRegionEntity> elementRegionEntities = gson.fromJson(elementRegionEntityListString, type);
        return elementRegionEntities;
    }
}
