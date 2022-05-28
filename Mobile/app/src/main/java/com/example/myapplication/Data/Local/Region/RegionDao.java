package com.example.myapplication.Data.Local.Region;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.Data.Local.Element.ElementRegionEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface RegionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(RegionEntity regionEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(List<RegionEntity> regionEntity);

    @Update
    Single<Integer> update(RegionEntity regionEntity);

    @Query("SELECT * FROM region_table")
    Single<List<RegionEntity>> getAll();

    @Query("SELECT * FROM region_table " +
            "WHERE id = :id")
    Single<RegionEntity> getRegionById(int id);

    @Query("SELECT * FROM region_table " +
            "WHERE codeLocation = :codeLocation")
    Single<RegionEntity> getRegionByCodeLocation(int codeLocation);

    @Query("DELETE FROM region_table")
    void deleteAllRegion();
}
