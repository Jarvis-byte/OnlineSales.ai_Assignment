package com.example.mathcalculator.DataBase;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "result_table")
public class ResultEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String arrayElement;
    private String result;

    public ResultEntity(String date, String arrayElement, String result) {
        this.date = date;
        this.arrayElement = arrayElement;
        this.result = result;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter method for the id field (follow JavaBeans conventions)
    public int getId() {
        return id;
    }

    // Other getter methods for your fields
    public String getDate() {
        return date;
    }

    public String getArrayElement() {
        return arrayElement;
    }

    public String getResult() {
        return result;
    }
}
