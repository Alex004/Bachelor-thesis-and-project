package com.example.myapplication.Data.Local.Element;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "element_region_table")
public class ElementRegionEntity implements Serializable {

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "region")
    private int region;
    @ColumnInfo(name = "element")
    private int element;

    public ElementRegionEntity(int id, int region, int element) {
        this.id = id;
        this.region = region;
        this.element = element;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public int getElement() {
        return element;
    }

    public void setElement(int element) {
        this.element = element;
    }
}
