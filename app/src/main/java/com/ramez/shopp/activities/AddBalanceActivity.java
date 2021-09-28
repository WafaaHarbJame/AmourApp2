package com.ramez.shopp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityAddBalanceBinding;

public class AddBalanceActivity extends ActivityBase {
    ActivityAddBalanceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBalanceBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.toolBar.backBtn.setOnClickListener(view1 -> {
            onBackPressed();
        });
        binding.followBut.setOnClickListener(view1 -> {
            startNoBalanceActivity();


        });


        setTitle(R.string.add_balance);


    }
    private void startNoBalanceActivity(){
        Intent intent=new Intent(getActiviy(), NoBalanceActivity.class);
        startActivity(intent);
    }
}