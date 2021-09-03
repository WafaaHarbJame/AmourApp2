package com.ramez.shopp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ramez.shopp.Adapter.WelcomeSliderAdapter;
import com.ramez.shopp.Models.WelcomeModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityWelcomeBinding;

import java.util.ArrayList;

public class WelcomeActivity extends ActivityBase {
    private ActivityWelcomeBinding binding;
    public ArrayList<WelcomeModel> welcomeSliderModels;
    private WelcomeSliderAdapter welcomeSliderAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        welcomeSliderModels=new ArrayList<>();
        welcomeSliderModels.add(new WelcomeModel(getString(R.string.dummy2),R.drawable.screen1,getString(R.string.string_menu_home)));
        welcomeSliderModels.add(new WelcomeModel(getString(R.string.dummy2),R.drawable.screen4,getString(R.string.categories)));
        welcomeSliderModels.add(new WelcomeModel(getString(R.string.dummy2),R.drawable.screen3,getString(R.string.offer_text)));

        welcomeSliderAdapter=new  WelcomeSliderAdapter(this,welcomeSliderModels);
        binding.viewPager.setAdapter(welcomeSliderAdapter);



        binding.nextBut.setOnClickListener(view12 -> {
            binding.viewPager.setCurrentItem(getItem(+1), true);

            if(binding.viewPager.getAdapter().getCount()==binding.viewPager.getCurrentItem()+1){
                navigateChooseCityActivity();

            }




        });

        binding.skipBtn.setOnClickListener(view1 -> navigateChooseCityActivity());


    }


    private int getItem(int i) {
        return binding.viewPager.getCurrentItem() + i;
    }


    public void navigateChooseCityActivity() {
//        startActivity(new Intent(WelcomeActivity.this, ChooseCityActivity.class));
//        finish();
    }



}