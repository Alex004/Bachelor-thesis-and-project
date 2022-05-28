package com.example.myapplication.Data.Local.Route;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(RouteEntity routeEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(List<RouteEntity> routeEntity);

    @Update
    Single<Integer> update(RouteEntity routeEntity);

    @Query("SELECT * FROM route_table")
    Single<List<RouteEntity>> getAll();

    @Query("SELECT * FROM route_table " +
            "WHERE code1 = :currentCode and code2 = :nextCode")
    Single<RouteEntity> getRouteUsingTwoPoints(int currentCode, int nextCode);

    @Query("DELETE FROM route_table")
    void deleteAllRoute();

}
