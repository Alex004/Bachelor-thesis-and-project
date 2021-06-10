package com.example.myapplication.Data.Local.Element;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ElementRegionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(ElementRegionEntity elementRegionEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(List<ElementRegionEntity> elementRegionEntity);

    @Update
    Single<Integer> update(ElementRegionEntity elementRegionEntity);

    @Query("SELECT * FROM element_region_table")
    Single<List<ElementRegionEntity>> getAll();

    @Query("Select Distinct region from element_region_table")
    Single<List<Integer>> getAllDistinctRegeionFromAvailabelItem();

    @Query("SELECT * FROM element_region_table " +
            "WHERE element = :element")
    Single<ElementRegionEntity> getEntityRegion(Integer element);

    @Query("SELECT * FROM element_region_table " +
            "WHERE region = :region")
    Single<List<ElementRegionEntity>> getEntityRegionByRegionId(Integer region);

    @Query("DELETE FROM element_region_table")
    void deleteAllElementRegion();
}
