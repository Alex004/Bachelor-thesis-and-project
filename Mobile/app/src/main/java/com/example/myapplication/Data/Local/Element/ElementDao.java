package com.example.myapplication.Data.Local.Element;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.Data.Local.Route.RouteEntity;
import com.example.myapplication.Utils.Result;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ElementDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(ElementEntity elementEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(List<ElementEntity> elementEntity);

    @Update
    Single<Integer> update(ElementEntity elementEntity);

    @Query("SELECT * FROM element_table")
    Single<List<ElementEntity>> getAll();

    @Query("SELECT * FROM element_table " +
            "WHERE name = :name Limit 1")
    Single<ElementEntity> getElementByName(String name);

    @Query("SELECT * FROM element_table " +
            "WHERE name = :name Limit 1")
    Single<ElementEntity> getElementByNameWithoutSingle(String name);

    @Query("SELECT * FROM element_table " +
            "WHERE id = :id Limit 1")
    Single<ElementEntity> getElementById(int id);


    @Query("DELETE FROM element_table")
    void deleteAllElement();
}
