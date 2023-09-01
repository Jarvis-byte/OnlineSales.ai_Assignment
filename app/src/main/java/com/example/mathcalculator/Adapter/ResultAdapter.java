package com.example.mathcalculator.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import com.example.mathcalculator.DataBase.ResultEntity;
import com.example.mathcalculator.R;
import com.example.mathcalculator.ViewHolder.ResultViewHolder;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.Objects;

public class ResultAdapter extends ListAdapter<ResultEntity, ResultViewHolder> {
    private final Context context;

    public ResultAdapter(Context context, List<ResultEntity> resultList) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        ResultEntity resultEntity = getItem(position);
        holder.textViewDate.setText("Date: " + resultEntity.getDate());
        holder.textViewArrayElement.setText("Array Element: " + resultEntity.getArrayElement());
        holder.textViewResult.setText("Result: " + resultEntity.getResult());
    }

    private static final DiffUtil.ItemCallback<ResultEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<ResultEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull ResultEntity oldItem, @NonNull ResultEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ResultEntity oldItem, @NonNull ResultEntity newItem) {
            return Objects.equals(oldItem, newItem);
        }
    };
}


