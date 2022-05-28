package com.example.myapplication.Models;

import com.example.myapplication.Data.Local.Code.CodeEntity;

import java.io.Serializable;
import java.util.List;

// This class is used for sending all code from a location between activities
public class CodeForRoutingDTO implements Serializable {
    private List<CodeEntity> codeEntityList;

    public CodeForRoutingDTO(List<CodeEntity> codeEntityList) {
        this.codeEntityList = codeEntityList;
    }

    public CodeForRoutingDTO() {
    }

    public List<CodeEntity> getCodeEntityList() {
        return codeEntityList;
    }

    public void setCodeEntityList(List<CodeEntity> codeEntityList) {
        this.codeEntityList = codeEntityList;
    }
}
