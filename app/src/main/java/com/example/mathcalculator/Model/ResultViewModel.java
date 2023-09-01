package com.example.mathcalculator.Model;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mathcalculator.DataBase.ResultDao;
import com.example.mathcalculator.DataBase.ResultDatabase;
import com.example.mathcalculator.DataBase.ResultEntity;

import java.util.List;

public class ResultViewModel extends AndroidViewModel {
    private ResultDao resultDao;
    private LiveData<List<ResultEntity>> allResults;

    public ResultViewModel(Application application) {
        super(application);
        ResultDatabase database = ResultDatabase.getInstance(application);
        resultDao = database.resultDao();
        allResults = resultDao.getAllResults();
    }

    public void insert(ResultEntity resultEntity) {
        ResultDatabase.databaseWriteExecutor.execute(() -> {
            resultDao.insert(resultEntity);
        });
    }

    public LiveData<List<ResultEntity>> getAllResults() {
        return allResults;
    }
}

