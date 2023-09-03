package com.example.mathcalculator.UI;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.example.mathcalculator.Adapter.ResultAdapter;
import com.example.mathcalculator.DataBase.ResultDao;
import com.example.mathcalculator.DataBase.ResultDatabase;
import com.example.mathcalculator.DataBase.ResultEntity;
import com.example.mathcalculator.Model.ResultViewModel;
import com.example.mathcalculator.R;

import java.util.List;

public class HistoryView extends AppCompatActivity {
    private ResultViewModel resultViewModel;
    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private List<ResultEntity> resultList; // Add this line

    private ImageView back, imageView, delete_history_button;
    private ResultDatabase resultDatabase;
    private ResultDao resultDao;

    private TextView text_no_data, txt_delete;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        back = findViewById(R.id.back);
        imageView = findViewById(R.id.imageView);
        text_no_data = findViewById(R.id.text_no_data);
        delete_history_button = findViewById(R.id.delete_history_button);
        txt_delete = findViewById(R.id.txt_delete);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResultAdapter(this, resultList);

        recyclerView.setAdapter(adapter);
        // Initialize your Room database
        resultDatabase = Room.databaseBuilder(getApplicationContext(), ResultDatabase.class, "result_database").build();
        resultDao = resultDatabase.resultDao();
        // Perform database operations in an AsyncTask
        new DatabaseOperationTask().execute();
        // Check if there is no data in the database


        // Initialize ResultViewModel
        resultViewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        // Observe changes in the database and update the adapter when data changes
        resultViewModel.getAllResults().observe(this, resultEntities -> {
            adapter.submitList(resultEntities);
        });

        //Back to go to home screen
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        delete_history_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResultViewModel resultViewModel = new ResultViewModel(getApplication());
                resultViewModel.deleteAllData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This code will be executed after 1 second
                        new DatabaseOperationTask().execute();
                    }
                }, 500);

            }
        });
        txt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResultViewModel resultViewModel = new ResultViewModel(getApplication());
                resultViewModel.deleteAllData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This code will be executed after 1 second
                        new DatabaseOperationTask().execute();
                    }
                }, 500);
            }
        });
    }

    private class DatabaseOperationTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            // Perform database operation in the background
            int rowCount = resultDao.getCount();
            return rowCount;
        }

        @Override
        protected void onPostExecute(Integer rowCount) {
            super.onPostExecute(rowCount);


            if (rowCount == 0) {
                // There is no data in the database
                // You can perform actions accordingly
                imageView.setVisibility(View.VISIBLE);
                text_no_data.setVisibility(View.VISIBLE);
                Glide.with(HistoryView.this).load(R.drawable.empty_3).into(imageView);
                recyclerView.setVisibility(View.GONE);
//                Toast toast = Toast.makeText(getApplicationContext(), "No Data", Toast.LENGTH_SHORT);
//
//                // Show the Toast message
//                toast.show();
            } else {
                imageView.setVisibility(View.GONE);
                text_no_data.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);


            }
        }
    }
}