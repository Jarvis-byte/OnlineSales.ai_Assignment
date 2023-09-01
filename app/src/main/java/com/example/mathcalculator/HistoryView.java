package com.example.mathcalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.mathcalculator.Adapter.ResultAdapter;
import com.example.mathcalculator.DataBase.ResultEntity;
import com.example.mathcalculator.Model.ResultViewModel;

import java.util.List;

public class HistoryView extends AppCompatActivity {
    private ResultViewModel resultViewModel;
    private RecyclerView recyclerView;
    private ResultAdapter adapter;
    private List<ResultEntity> resultList; // Add this line


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResultAdapter(this, resultList);
        recyclerView.setAdapter(adapter);

        // Initialize ResultViewModel
        resultViewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        // Observe changes in the database and update the adapter when data changes
        resultViewModel.getAllResults().observe(this, resultEntities -> {
            adapter.submitList(resultEntities);
        });
    }
}