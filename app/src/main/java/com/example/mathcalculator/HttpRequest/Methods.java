package com.example.mathcalculator.HttpRequest;

import com.example.mathcalculator.Model.Model;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Methods {
    @GET("/v4")
    Call<ResponseBody> getAllData(@Query("expr") String expression);
}
