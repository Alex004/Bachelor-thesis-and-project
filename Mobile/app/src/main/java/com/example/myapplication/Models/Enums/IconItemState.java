package com.example.myapplication.Models.Enums;

public enum IconItemState {
    FOUND("FOUND",0),
    NOT_FOUND("NOT FOUND",1),
    SEARCHING("SEARCHING",2);

    private final String key;
    private final Integer value;

    IconItemState(String key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }
    public Integer getValue() {
        return value;
    }
}
