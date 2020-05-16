package com.example.speechrecognition.base;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public abstract class MyViewHolder extends RecyclerView.ViewHolder {

    protected MyViewHolder(ViewBinding binding) {
        super(binding.getRoot());
    }

    public abstract void bind(String cityName);
}
