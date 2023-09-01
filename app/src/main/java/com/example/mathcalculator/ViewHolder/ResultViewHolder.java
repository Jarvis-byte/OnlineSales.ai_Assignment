package com.example.mathcalculator.ViewHolder;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathcalculator.R;

public class ResultViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewDate;
    public TextView textViewArrayElement;
    public TextView textViewResult;

    public ResultViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewDate = itemView.findViewById(R.id.textViewDate);
        textViewArrayElement = itemView.findViewById(R.id.textViewArrayElement);
        textViewResult = itemView.findViewById(R.id.textViewResult);
    }
}

