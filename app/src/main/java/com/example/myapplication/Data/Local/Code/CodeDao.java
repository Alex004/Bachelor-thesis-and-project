package com.example.myapplication.Data.Local.Code;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.Data.Local.Route.RouteEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface CodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(CodeEntity codeEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(List<CodeEntity> codeEntity);

    @Update
    Single<Integer> update(CodeEntity codeEntity);

    @Query("SELECT * FROM code_table")
    Single<List<CodeEntity>> getAll();

    @Query("SELECT * FROM code_table " +
            "WHERE id = :id")
    Single<CodeEntity> getCodeEntityById(int id);

    @Query("SELECT * FROM code_table " +
            "WHERE code = :code")
    Single<CodeEntity> getCodeEntityByCode(String code);


//    @Query("SELECT * FROM trailer_item_table " +
//            "WHERE defaultValue > 0 ")
//    Single<List<TrailerItemEntity>> getAllNumerableItems();

    @Query("DELETE FROM code_table")
    void deleteAllCode();
}
