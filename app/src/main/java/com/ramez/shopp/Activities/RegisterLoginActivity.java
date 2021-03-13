package com.ramez.shopp.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.ramez.shopp.Adapter.RegisterLoginAdapter;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityRegisteLoginBinding;

public class RegisterLoginActivity extends ActivityBase {
    private ActivityRegisteLoginBinding binding;
    private RegisterLoginAdapter adapter;
    private boolean login=false,register=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisteLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.toolBar.backBtn.setVisibility(View.GONE);
        getIntentExtra();

        adapter = new RegisterLoginAdapter(getActiviy(), getSupportFragmentManager());

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {

                } else  if(position == 1) {
                }
                else  if(position == 2) {
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




        binding.viewPager.setAdapter(adapter);
        binding.tabs.setSelectedTabIndicatorColor(Color.WHITE);
        binding.tabs.setupWithViewPager(binding.viewPager);
        binding.tabs.setSelectedTabIndicatorColor(ContextCompat.getColor(getActiviy(),R.color.colorAccent));





        if(login){
            binding.viewPager.setCurrentItem(1);

        }
        else {
            binding.viewPager.setCurrentItem(0);

        }

    }

    private void getIntentExtra() {
        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            login=getIntent().getBooleanExtra(Constants.LOGIN,false);
            register=getIntent().getBooleanExtra(Constants.REGISTER,false);



        }
    }
}