package com.example.myapplication.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// This class is used for sending elements and state from list for each element (state means if it selected or not)
// between activities
public class ElementDTO implements Serializable {

    private List<String> selectItems;
    private List<Boolean> saveStates;

    public ElementDTO()
    {
        selectItems = new ArrayList<>();
        saveStates = new ArrayList<>();
    }

    public ElementDTO(List<String> selectItem, List<Boolean> saveStates){
        this.selectItems = selectItem;
        this.saveStates = saveStates;
    }

    public List<Boolean> getSaveStates() {
        return saveStates;
    }

    public void setSaveStates(List<Boolean> saveStates) {
        this.saveStates = saveStates;
    }

    public List<String> getSelectItems() {
        return selectItems;
    }

    public void setSelectItems(List<String> selectItems) {
        this.selectItems = selectItems;
    }




}
