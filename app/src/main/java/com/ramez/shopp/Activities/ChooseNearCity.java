package com.ramez.shopp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ramez.shopp.Adapter.CityAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CityModelResult;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.GlobalData;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.CityModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityChooseNearstCityBinding;

import java.util.ArrayList;

public class ChooseNearCity extends ActivityBase implements CityAdapter.OnCityClick {
    ActivityChooseNearstCityBinding binding;
    ArrayList<CityModel> list;
    int city_id = 0;
    LocalModel localModel;
    private CityAdapter cityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseNearstCityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        list = new ArrayList<>();

        localModel = UtilityApp.getLocalData();

        binding.chooseCityTv.setOnClickListener(view1 -> {
            binding.cityContainer.setVisibility(View.VISIBLE);
            binding.chooseCityTv.setVisibility(View.GONE);

        });

        getExtraIntent();

        binding.toolBar.backBtn.setVisibility(View.GONE);


    }

    private void getExtraIntent() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            int country_id = getIntent().getIntExtra(Constants.COUNTRY_ID, 0);
            Log.i("TAG", "Log ShortName" + localModel.getShortname());
            Log.i("TAG", "Log country_id" + localModel.getCountryId());
            Log.i("TAG", "Log country_id Intent" + country_id);
            Log.i("TAG", "Log country_id model " + localModel.getCountryId());

            getCityList(country_id);

        }


    }

    public void initAdapter() {

        cityAdapter = new CityAdapter(getActiviy(), list, 0, this);
        binding.recycler.setAdapter(cityAdapter);
        binding.cityContainer.setVisibility(View.VISIBLE);
        binding.chooseCityTv.setVisibility(View.GONE);

    }


    private void getCityList(int country_id) {
        list.clear();
        Log.i("TAG", "Log country_id" + country_id);

        GlobalData.progressDialog(getActiviy(), R.string.upload_date, R.string.please_wait_upload);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.hideProgressDialog();
            CityModelResult result = (CityModelResult) obj;

            if (func.equals(Constants.ERROR)) {
                String message = getString(R.string.fail_to_get_data);
                if (result != null && result.getMessage() != null) {
                    message = result.getMessage();
                }
                GlobalData.errorDialog(getActiviy(), R.string.fail_to_get_data, message);

            } else if (func.equals(Constants.NO_CONNECTION)) {

                GlobalData.Toast(getActiviy(), getString(R.string.no_internet_connection));

            } else {
                if (IsSuccess) {
                    if (result != null) {
                        list = result.getData();
                        if (list != null && list.size() > 0) {
                            Log.i("TAG", "Log country size " + list.size());

                            initAdapter();
                        }
                    } else {
                        Toast(getString(R.string.no_cities));
                    }
                } else {
                    Toast(getString(R.string.fail_to_get_data));

                }
            }

        }).CityHandle(country_id);

    }

    @Override
    public void onCityClicked(int position, CityModel cityModel) {
        UtilityApp.setIsFirstRun(false);
        city_id = cityModel.getId();
        localModel.setCityId(String.valueOf(city_id));
        UtilityApp.setLocalData(localModel);
        Intent intent = new Intent(getActiviy(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}