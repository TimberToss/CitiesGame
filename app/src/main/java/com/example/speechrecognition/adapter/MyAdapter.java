package com.example.speechrecognition.adapter;

import android.view.LayoutInflater;

import com.example.speechrecognition.adapter.viewholder.AppViewHolder;
import com.example.speechrecognition.adapter.viewholder.UserViewHolder;
import com.example.speechrecognition.base.MyViewHolder;
import com.example.speechrecognition.data.entity.CityForRecyclerView;
import com.example.speechrecognition.databinding.AppViewHolderBinding;
import com.example.speechrecognition.databinding.UserViewHolderBinding;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private List<CityForRecyclerView> cities;

    public MyAdapter(List<CityForRecyclerView> cities) {
        this.cities = cities;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new AppViewHolder(
                    AppViewHolderBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false)
            );

        } else {
            return new UserViewHolder(
                    UserViewHolderBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false)
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(cities.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (cities.get(position).getType() == CityForRecyclerView.CityType.APP_CITY) {
            return 0;
        } else {
            return 1;
        }
    }
}
