package com.example.speechrecognition.adapter.viewholder;

import com.example.speechrecognition.base.MyViewHolder;
import com.example.speechrecognition.databinding.AppViewHolderBinding;

public class AppViewHolder extends MyViewHolder {

    private AppViewHolderBinding binding;

    public AppViewHolder(AppViewHolderBinding binding) {
        super(binding);
        this.binding = binding;
    }

    @Override
    public void bind(String cityName) {
        binding.appCityText.setText(cityName);
    }
}
