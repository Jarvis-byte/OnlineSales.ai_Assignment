package com.example.mathcalculator.UI;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.mathcalculator.DataBase.ResultDao;
import com.example.mathcalculator.DataBase.ResultDatabase;
import com.example.mathcalculator.DataBase.ResultEntity;
import com.example.mathcalculator.HttpRequest.Methods;
import com.example.mathcalculator.HttpRequest.RetrofitClient;
import com.example.mathcalculator.Model.ResultViewModel;
import com.example.mathcalculator.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreen extends AppCompatActivity {
    private String TAG = "JSON_Response";
    private String FAIL_TAG = "FAIL_TAG";
    private EditText ExpressionSubmitBox;
    private ConstraintLayout btn_submit;

    private ImageView btn_history;
    private TextView txt_test;

    private int currentIndex = 0;

    private String[] stringArray = new String[0];

    private ResultDatabase resultDatabase;
    private ResultDao resultDao;
    private ResultViewModel resultViewModel;
    private boolean first = false;

    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExpressionSubmitBox = findViewById(R.id.ExpressionSubmitBox);
        btn_submit = findViewById(R.id.btn_submit);
        txt_test = findViewById(R.id.txt_test);
        btn_history = findViewById(R.id.btn_history);
        // Initialize Room Database
        resultDatabase = Room.databaseBuilder(getApplicationContext(), ResultDatabase.class, "result_database").build();
        resultDao = resultDatabase.resultDao();

        // Initialize ViewModel
        resultViewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_test.setText("Calculating Results ...");
                processEditTextContent();


            }
        });
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, HistoryView.class);

                startActivity(intent);
            }
        });
        ExpressionSubmitBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the hint text when the EditText is clicked
                ExpressionSubmitBox.setHint("");
            }
        });

        ExpressionSubmitBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Clear the hint text when the EditText gains focus
                    ExpressionSubmitBox.setHint("");
                }
            }
        });

//        // Observe changes in the database and update the UI accordingly
//        resultViewModel.getAllResults().observe(this, new Observer<List<ResultEntity>>() {
//            @Override
//            public void onChanged(List<ResultEntity> resultEntities) {
//                // Update the UI with the stored results
//                updateResultTextView(resultEntities);
//            }
//        });
    }

    protected void fetchNextResult() {
        if (currentIndex < stringArray.length) {
            String expression = stringArray[currentIndex];
            currentIndex++;


            Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
            Call<ResponseBody> call = methods.getAllData(expression);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String result = response.body().string();
                        Log.d("API Response", "Result: " + result);
                        if (first != true) {
                            txt_test.setText("");
                        }
                        first = true;

                        displayResult(stringArray[currentIndex - 1], result);

                        // Fetch the next result after a delay (e.g., 1 second)
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                fetchNextResult();
                            }
                        }, 1000); // Delay for 1 second before making the next API call
                        insertResultToDatabase(expression, result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, e.toString());
                        // Handle exceptions
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(FAIL_TAG, String.valueOf(t.toString()));
                    // Handle API call failure
                }
            });


        } else {
            // All results have been fetched
            // You can perform any final actions here
        }
    }

    private void insertResultToDatabase(String expression, String result) {
        // Get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");
        String currentDate = sdf.format(Calendar.getInstance().getTime());

        // Insert the result into the Room Database
        ResultEntity resultEntity = new ResultEntity(currentDate, expression, result);
        resultViewModel.insert(resultEntity);
//        ResultViewModel resultViewModel = new ResultViewModel(getApplication());
//        resultViewModel.deleteAllData();

        // Continue fetching the next result
        fetchNextResult();
    }

    private void processEditTextContent() {
        String editTextContent = ExpressionSubmitBox.getText().toString();
        String[] lines = editTextContent.split("\n");
        stringArray = new String[lines.length];
        System.arraycopy(lines, 0, stringArray, 0, lines.length);
        ExpressionSubmitBox.setText(""); // Clear the EditText
        currentIndex = 0; // Reset the index

        // Start fetching results
        fetchNextResult();
    }

    private void updateResultTextView(List<ResultEntity> resultEntities) {
        // Clear the resultTextView
        txt_test.setText("");

        // Iterate through the stored results and display them in the TextView
        for (ResultEntity resultEntity : resultEntities) {
            String text = resultEntity.getArrayElement() + " => " + resultEntity.getResult() + "\n";
            txt_test.append(text);
        }
    }

    private void displayResult(String s, String result) {
        // Append the result to the TextView
        txt_test.append(s + " => " + result + "\n");
    }

}