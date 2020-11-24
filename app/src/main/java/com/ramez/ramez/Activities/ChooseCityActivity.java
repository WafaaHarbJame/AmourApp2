package com.ramez.ramez.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ramez.ramez.Adapter.CountriesAdapter;
import com.ramez.ramez.Models.CountryModel;
import com.ramez.ramez.R;
import com.ramez.ramez.databinding.ActivityChooseCityBinding;

import java.util.ArrayList;

public class ChooseCityActivity extends ActivityBase implements CountriesAdapter.OnItemClick {
    private ActivityChooseCityBinding binding;
    private CountriesAdapter countriesAdapter;
    ArrayList<CountryModel> countries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseCityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        countries=new ArrayList<>();
        binding.chooseCityTv.setOnClickListener(view1 -> {
            binding.cityContainer.setVisibility(View.VISIBLE);
            binding.chooseCityTv.setVisibility(View.GONE);

        });

        countries.add(new CountryModel(1,972,getString(R.string.text_registration_country),
                "","","","",getString(R.string.text_registration_country),""));

        countries.add(new CountryModel(1,972,getString(R.string.text_registration_country),
                "","","","",getString(R.string.text_registration_country),""));
        countries.add(new CountryModel(1,972,getString(R.string.text_registration_country),
                "","","","",getString(R.string.text_registration_country),""));
        countries.add(new CountryModel(1,972,getString(R.string.text_registration_country),
                "","","","",getString(R.string.text_registration_country),""));
        countries.add(new CountryModel(1,972,getString(R.string.text_registration_country),
                "","","",getString(R.string.text_registration_country),getString(R.string.text_registration_country),""));

        initAdapter();


        binding.toolBar.homeBtn.setVisibility(View.GONE);

    }

    private void GoToChooseNearCity() {
        Intent intent = new Intent(getActiviy(), ChooseNearCity.class);
        startActivity(intent);
        finish();

    }
    public void initAdapter(){
        countriesAdapter = new CountriesAdapter(getActiviy(), this, countries);
        binding.recycler.setAdapter(countriesAdapter);
    }


    @Override
    public void onItemClicked(int position, CountryModel countryModel) {
        GoToChooseNearCity();
    }
}