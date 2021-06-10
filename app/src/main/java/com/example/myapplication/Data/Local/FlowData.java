package com.example.myapplication.Data.Local;

import android.app.Application;
import android.graphics.Region;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.myapplication.Data.Local.Code.CodeDao;
import com.example.myapplication.Data.Local.Code.CodeEntity;
import com.example.myapplication.Data.Local.Element.ElementDao;
import com.example.myapplication.Data.Local.Element.ElementEntity;
import com.example.myapplication.Data.Local.Element.ElementRegionDao;
import com.example.myapplication.Data.Local.Element.ElementRegionEntity;
import com.example.myapplication.Data.Local.Region.RegionDao;
import com.example.myapplication.Data.Local.Region.RegionEntity;
import com.example.myapplication.Data.Local.Route.RouteDao;
import com.example.myapplication.Data.Local.Route.RouteEntity;
import com.example.myapplication.Models.ElementRepository;
import com.example.myapplication.Utils.Result;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class FlowData extends AndroidViewModel {

    private ElementRepository elementRepository;
    private RouteDao routeDao;
    private CodeDao codeDao;
    private ElementDao elementDao;
    private ElementRegionDao elementRegionDao;
    private RegionDao regionDao;

    private CompositeDisposable disposable = new CompositeDisposable();


    public FlowData(@NonNull Application application) {
        super(application);
        elementRepository = new ElementRepository(getApplication().getApplicationContext());
        routeDao = Database.getDatabase(getApplication()).routeDao();
        codeDao = Database.getDatabase(getApplication()).codeDao();
        elementDao = Database.getDatabase(getApplication()).elementDao();
        elementRegionDao = Database.getDatabase(getApplication()).elementRegionDao();
        regionDao = Database.getDatabase(getApplication()).regionDao();
    }

    public CompositeDisposable getDisposable() {
        return disposable;
    }

    public void setDisposable(CompositeDisposable disposable) {
        this.disposable = disposable;
    }

    public Single<Response<JsonObject>> test()
    {
        return elementRepository.test();
    }


    public Single<Result<RouteEntity>> getRouteUsingTwoPoints(int currentCode, int nextCode)
    {
        return routeDao.getRouteUsingTwoPoints(currentCode,nextCode)
                .map(Result::success)
                .onErrorReturn(e -> {
                    Log.e("ERR",e.getMessage());
                    throw new Exception("Non item found");});
    }

    public void deleteAllRoute(){
        routeDao.deleteAllRoute();
    }

    public Single<Response<JsonObject>> checkLocation(String idLocation)
    {
        return elementRepository.checkLocation(idLocation);
    }

    public Single<Result<List<Integer>>> getAllDistinctRegeionFromAvailabelItem(){
        return elementRegionDao.getAllDistinctRegeionFromAvailabelItem()
                .map(Result::success)
                .onErrorReturn(Result::error);
    }

    public Single<Result<ElementEntity>> getElementByNameWithoutSingle(String name)
    {
        return elementDao.getElementByNameWithoutSingle(name)
                .map(Result::success)
                .onErrorReturn(e -> {
                    Log.e("ERR",e.getMessage());
                    throw new Exception("Non item found");});
    }

    public Single<Result<ElementEntity>> getElementById(int id){
        return elementDao.getElementById(id)
                .map(Result::success)
                .onErrorReturn(e -> {
                    Log.e("ERR",e.getMessage());
                    throw new Exception("Non item found");});
    }

    public void deleteAllElement()
    {
        elementDao.deleteAllElement();
    }

    public Single<Result<ElementRegionEntity>> getEntityRegion(Integer element)
    {
        return elementRegionDao.getEntityRegion(element)
                .map(Result::success)
                .onErrorReturn(e -> {
                    Log.e("ERR",e.getMessage());
                    throw new Exception("Non elementRegion found");});
    }

    public Single<Result<List<ElementRegionEntity>>> getEntityRegionByRegionId(Integer region){
        return elementRegionDao.getEntityRegionByRegionId(region)
                .map(Result::success)
                .onErrorReturn(e -> {
                    Log.e("ERR",e.getMessage());
                    throw new Exception("Non elementRegion found");});
    }

    public void deleteAllElementRegion()
    {
        elementRegionDao.deleteAllElementRegion();
    }

    public Single<Result<RegionEntity>> getRegionById(int id){
        return regionDao.getRegionById(id)
                .map(Result::success)
                .onErrorReturn(e -> {
                    Log.e("ERR",e.getMessage());
                    throw new Exception("Non region found");});
    }

    public Single<Result<RegionEntity>> getRegionByCodeLocation(int codeLocation)
    {
        return regionDao.getRegionByCodeLocation(codeLocation)
                .map(Result::success)
                .onErrorReturn(e -> {
                    Log.e("ERR",e.getMessage());
                    throw new Exception("Non region found");});
    }

    public void deleteAllRegion()
    {
        regionDao.deleteAllRegion();
    }

    public Single<Result<CodeEntity>> getCodeEntityById(int id)
    {
        return codeDao.getCodeEntityById(id)
                .map(Result::success)
                .onErrorReturn(e -> {
                    Log.e("ERR",e.getMessage());
                    throw new Exception("Non code found");});
    }

    public Single<Result<CodeEntity>> getCodeEntityByCode(String code)
    {
        return codeDao.getCodeEntityByCode(code)
                .map(Result::success)
                .onErrorReturn(e -> {
                    Log.e("ERR",e.getMessage());
                    throw new Exception("Non code found");});
    }

    public void deleteAllCode()
    {
        codeDao.deleteAllCode();
    }

    public Single<Result<List<RouteEntity>>> getAllRouteDao(){

        return routeDao.getAll()
                .map(result ->{
                    Log.i("Gettin All Routes","Success");
                    return Result.success(result);
                } )

                .onErrorReturn(e -> {
                    Log.e("ERR Route",e.getMessage());
                    throw new Exception("Non item found");
                });
    }

    public Single<Result<List<CodeEntity>>> getAllCodeDao(){
        return codeDao.getAll()
                .map(Result::success)
                .onErrorReturn(Result::error);
    }

    public Single<Result<List<ElementEntity>>> getAllElementDao(){
        return elementDao.getAll()
                .map(Result::success)
                .onErrorReturn(Result::error);
    }

    public Single<Result<ElementEntity>> getElementByName(String name){
        return elementDao.getElementByName(name)
                .map(Result::success)
                .onErrorReturn(e -> {
                    Log.e("ERR",e.getMessage());
                    throw new Exception("Non item found");});
    }

    public Single<Result<List<ElementRegionEntity>>> getAllElementRegionDao(){
        return elementRegionDao.getAll()
                .map(Result::success)
                .onErrorReturn(Result::error);
    }

    public Single<Result<List<RegionEntity>>> getAllRegionDao(){
        return regionDao.getAll()
                .map(Result::success)
                .onErrorReturn(Result::error);
    }

    public Single<Result<List<Long>>> insertRoute(List<RouteEntity> routeEntityList) {
        return routeDao.insertAll(routeEntityList)
                .map(Result::success)
                .onErrorReturn(Result::error);
    }

    public Single<Result<List<Long>>> insertCode(List<CodeEntity> codeEntityList) {
        return codeDao.insertAll(codeEntityList)
                .map(result -> Result.success(result))
                .onErrorReturn(e -> Result.error(e));
    }

    public Single<Result<List<Long>>> insertElement(List<ElementEntity> elementEntityList) {
        return elementDao.insertAll(elementEntityList)
                .map(result -> Result.success(result))
                .onErrorReturn(e -> Result.error(e));
    }

    public Single<Result<List<Long>>> insertElementRegion(List<ElementRegionEntity> elementRegionEntityList) {
        return elementRegionDao.insertAll(elementRegionEntityList)
                .map(result -> Result.success(result))
                .onErrorReturn(e -> Result.error(e));
    }

    public Single<Result<List<Long>>> insertRegion(List<RegionEntity> regionEntityList) {
        return regionDao.insertAll(regionEntityList)
                .map(result -> Result.success(result))
                .onErrorReturn(e -> Result.error(e));
    }

    public Single<Response<List<RouteEntity>>> getRoute(String location)
    {
        return elementRepository.getRoute(location);
    }

    public Single<Response<List<CodeEntity>>> getCode(String location){
        return elementRepository.getCode(location);
    }

    public Single<Response<List<ElementEntity>>> getElements(String location){
        return elementRepository.getElements(location);
    }

    public Single<Response<List<RegionEntity>>> getRegion(String location){
        return elementRepository.getRegion(location);
    }

    public Single<Response<List<ElementRegionEntity>>> getElementRegion(String location){
        return elementRepository.getElementRegion(location);
    }

    @Override
    protected void onCleared() {
        disposable.clear();
        super.onCleared();
    }


}
