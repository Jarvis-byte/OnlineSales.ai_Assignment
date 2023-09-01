package com.example.mathcalculator.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ResultDao {
    @Insert
    void insert(ResultEntity resultEntity);

    @Query("SELECT * FROM result_table ORDER BY date DESC")
    LiveData<List<ResultEntity>> getAllResults();
}

