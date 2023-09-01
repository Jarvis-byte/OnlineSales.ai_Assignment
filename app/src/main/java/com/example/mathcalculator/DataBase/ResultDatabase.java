package com.example.mathcalculator.DataBase;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ResultEntity.class}, version = 1, exportSchema = false)
public abstract class ResultDatabase extends RoomDatabase {
    private static ResultDatabase instance;

    public abstract ResultDao resultDao();

    public static synchronized ResultDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            ResultDatabase.class, "result_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);
}


