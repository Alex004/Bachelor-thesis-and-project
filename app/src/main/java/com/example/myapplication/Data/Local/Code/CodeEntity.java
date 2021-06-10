package com.example.myapplication.Data.Local.Code;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.myapplication.Utils.Node;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@Entity(tableName = "code_table")
public class CodeEntity implements Serializable {

    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "code")
    private String code;

    public CodeEntity(int id, String code) {
        this.id = id;
        this.code = code;
    }

//    public CodeEntity(String code)
//    {
//        this.code = code;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

//    public int compareTo(Node other){
//        return Double.compare(shortestDistance, other.shortestDistance);
//    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof CodeEntity)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        CodeEntity c = (CodeEntity) o;

        // Compare the data members and return accordingly
        return this.code.equals(c.code);
    }

    @Override
    public int hashCode() {
        return this.code.hashCode();
    }
}
