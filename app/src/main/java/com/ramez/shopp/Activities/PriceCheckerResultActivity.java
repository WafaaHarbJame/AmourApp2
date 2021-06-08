package com.ramez.shopp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityPriceCheckerResultBinding;
import com.ramez.shopp.databinding.ActivityProductDeatilsBinding;

public class PriceCheckerResultActivity extends ActivityBase {
    ActivityPriceCheckerResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPriceCheckerResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle(R.string.Price_Checker);

    }
}