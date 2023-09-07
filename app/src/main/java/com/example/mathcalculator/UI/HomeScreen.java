package com.example.mathcalculator.UI;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeScreen extends AppCompatActivity {
    private String TAG = "JSON_Response";
    private String FAIL_TAG = "FAIL_TAG";
    private EditText ExpressionSubmitBox;
    private ConstraintLayout btn_submit;

    private ImageView btn_history, btn_clear;
    private TextView txt_test;

    private int currentIndex = 0;

    private String[] stringArray = new String[0];

    private ResultDatabase resultDatabase;
    private ResultDao resultDao;
    private ResultViewModel resultViewModel;
    private boolean first = false;
    private Handler handler = new Handler(Looper.getMainLooper());

    private HashMap<Integer, String> storeResult;

    public HomeScreen(
            EditText expressionSubmitBox,
            ConstraintLayout btnSubmit,
            TextView txtTest,
            ImageView btnHistory,
            ImageView btnClear,
            ResultDatabase resultDatabase,
            ResultViewModel resultViewModel
    ) {
        ExpressionSubmitBox = expressionSubmitBox;
        btn_submit = btnSubmit;
        this.txt_test = txtTest;
        btn_history = btnHistory;
        this.btn_clear = btnClear;
        this.resultDatabase = resultDatabase;
        this.resultViewModel = resultViewModel;

    }

    public HomeScreen() {

    }

    @SuppressLint("MissingInflatedId")
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExpressionSubmitBox = findViewById(R.id.ExpressionSubmitBox);
        btn_submit = findViewById(R.id.btn_submit);
        txt_test = findViewById(R.id.txt_test);
        btn_history = findViewById(R.id.btn_history);
        btn_clear = findViewById(R.id.btn_clear);
        // Initialize Room Database
        resultDatabase = Room.databaseBuilder(getApplicationContext(), ResultDatabase.class, "result_database").build();
        resultDao = resultDatabase.resultDao();

        // Initialize ViewModel
        resultViewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the text from the EditText
                String editTextContent = ExpressionSubmitBox.getText().toString().trim();

                // Check if the EditText is empty
                if (TextUtils.isEmpty(editTextContent)) {
                    // Show an error message in the EditText
                    ExpressionSubmitBox.setError("Field cannot be empty");
                    ExpressionSubmitBox.requestFocus(); // Focus on the EditText
                } else {
                    if (isValidMathExpression(editTextContent)) {
                        // Clear any previous error
                        ExpressionSubmitBox.setError(null);

                        // Set the text while calculating results
                        txt_test.setText("Calculating Results ...");

                        // Call the function to process the EditText content
                        first = false;
                        processEditTextContent();
                    } else {
                        ExpressionSubmitBox.setError("Not a Valid Expression");
                        ExpressionSubmitBox.requestFocus();

                    }

                }


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
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_test.setText("");
                txt_test.setHint("Results");
                txt_test.setHintTextColor(Color.parseColor("#000000"));
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


    public boolean isValidMathExpression(String input) {
        // Split the input into lines
        String[] lines = input.split("\\r?\\n");

        // Define a regular expression pattern for a valid mathematical expression
        String regex = "^[\\d+\\-*/()^\\s]+|sqrt\\([^)]+\\)$";

        // Compile the regular expression
        Pattern pattern = Pattern.compile(regex);

        // Iterate through each line and check if it matches the pattern
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line.trim());
            if (!matcher.matches()) {
                return false; // If any line is invalid, return false
            }
        }

        return true; // All lines are valid expressions
    }


    private void startFetchingResults() {
        while (currentIndex < stringArray.length) {
            fetchNextResult();
        }


    }

    // This method fetches the next result
    private void fetchNextResult() {
        if (currentIndex < stringArray.length) {
            String expression = stringArray[currentIndex];
            System.out.println(currentIndex + "->" + expression);
            currentIndex++;

            Methods methods = RetrofitClient.getRetrofitInstance().create(Methods.class);
            Call<ResponseBody> call = methods.getAllData(expression);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String result = response.body().string();
                        Log.d("API Response", "Result: " + result);
                        if (!first) {
                            txt_test.setText("");
                            Log.d("Boolean", "Boolean Value: " + first);
                        }
                        first = true;

                        displayResult(expression, result);
                        insertResultToDatabase(expression, result);


                    } catch (Exception e) {
                        // Handle exceptions
                        e.printStackTrace();
                        Log.e("Fail JSON", e.toString());
                        // Remove the failed expression from the array

                        // Call the function for the next element
                        if (!first) {
                            txt_test.setText("");
                            Log.d("Boolean", "Boolean Value: " + first);
                        }
                        first = true;
                        displayResult(expression, null);

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(FAIL_TAG, String.valueOf(t.toString()));
                    // Handle API call failure
                    if (!first) {
                        txt_test.setText("");
                    }
                    first = true;

                }
            });
        }
    }


    private void insertResultToDatabase(String expression, String result) {
        // Get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY ', Time :' hh:mmaa", Locale.ENGLISH);
        String currentDate = sdf.format(Calendar.getInstance().getTime());

        // Insert the result into the Room Database
        ResultEntity resultEntity = new ResultEntity(currentDate, expression, result);
        resultViewModel.insert(resultEntity);
//        ResultViewModel resultViewModel = new ResultViewModel(getApplication());
//        resultViewModel.deleteAllData();

        // Continue fetching the next result
        startFetchingResults();
    }

    private void processEditTextContent() {
        String editTextContent = ExpressionSubmitBox.getText().toString();
        String[] lines = editTextContent.split("\n");
        stringArray = new String[lines.length];
        System.arraycopy(lines, 0, stringArray, 0, lines.length);
        ExpressionSubmitBox.setText(""); // Clear the EditText
        currentIndex = 0; // Reset the index
        // Start fetching results
        startFetchingResults();
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
        System.out.println(s + "->" + result);
        if (result != null) {
            if (!result.isEmpty()) {
                // Append the result to the TextView for successful HTTP requests
//                Log.i("Expression in print", s + " => " + result + "\n");
                txt_test.append(s + " => " + result + "\n");
            } else {
                Log.i("Else Part in empty", "It is in else");
                txt_test.append(s + " => " + "Wrong Expression" + "\n");
            }
        } else {
            Log.i("Else Part", "It is in else");
            txt_test.append(s + " => " + "Wrong Expression" + "\n");
        }

    }

}