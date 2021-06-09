package com.ramez.shopp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityPriceCheckerResultBinding;

public class PriceCheckerResultActivity extends ActivityBase {
    ActivityPriceCheckerResultBinding binding;
    private int SEARCH_CODE = 2000;
    private String CODE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPriceCheckerResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setTitle(R.string.Price_Checker);

        binding.scanAgainBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActiviy(), FullScannerActivity.class);
                startActivityForResult(intent, SEARCH_CODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH_CODE) {

            if (data != null) {
                CODE = data.getStringExtra(Constants.CODE);


            }


        }
    }




}