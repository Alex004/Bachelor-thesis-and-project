package com.example.myapplication.Data.Local.Route;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "route_table")
public class RouteEntity implements Serializable {

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "distance")
    private int distance;
    @ColumnInfo(name = "code1")
    private int code1;
    @ColumnInfo(name = "code2")
    private int code2;

    @ColumnInfo(name = "aidMessage")
    private String aid_message;

    public RouteEntity(int id, int distance, int code1, int code2, String aid_message) {
        this.id = id;
        this.distance = distance;
        this.code1 = code1;
        this.code2 = code2;
        this.aid_message = aid_message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCode1() {
        return code1;
    }

    public void setCode1(int code1) {
        this.code1 = code1;
    }

    public int getCode2() {
        return code2;
    }

    public void setCode2(int code2) {
        this.code2 = code2;
    }

    public String getAid_message() {
        return aid_message;
    }

    public void setAid_message(String aid_message) {
        this.aid_message = aid_message;
    }

}
