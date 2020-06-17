package com.example.speechrecognition.adapter.viewholder;

import com.example.speechrecognition.base.MyViewHolder;
import com.example.speechrecognition.databinding.UserViewHolderBinding;

public class UserViewHolder extends MyViewHolder {

    private UserViewHolderBinding binding;

    public UserViewHolder(UserViewHolderBinding binding) {
        super(binding);
        this.binding = binding;
    }

    @Override
    public void bind(String cityName) {
        binding.userCityText.setText(cityName);
    }
}
