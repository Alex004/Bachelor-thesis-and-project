package com.example.myapplication.Models.Enums;

public enum ActionEventMain {
    GET_INFO_LOCATION("GET_INFO_LOCATION",0),
    SHOW_INFO_LOCATION("SHOW_INFO_LOCATION",1),
    END_ROUTE_ACTIVITY("END_ROUTE_ACTIVITY",2),
    END_GROUP_DETECTION("END_GROUP_DETECTION",3);

    private final String key;
    private final Integer value;

    ActionEventMain(String key, Integer value) {
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
