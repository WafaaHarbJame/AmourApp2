package com.ramez.shopp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.ramez.shopp.Adapter.BranchAdapter;
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

public class ChooseNearCity extends ActivityBase {
    ActivityChooseNearstCityBinding binding;
    ArrayList<CityModel> list;
    int city_id = 0;
    LocalModel localModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseNearstCityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        list = new ArrayList<>();

        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActiviy());
        setTitle(getString(R.string.change_city_branch));

        binding.recycler.setLayoutManager(new LinearLayoutManager(getActiviy()));

        binding.toolBar.mainTitleTv.setVisibility(View.VISIBLE);
        binding.toolBar.logoImg.setVisibility(View.GONE);
        binding.toolBar.backBtn.setVisibility(View.VISIBLE);

        getExtraIntent();


    }

    private void getExtraIntent() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            int country_id = getIntent().getIntExtra(Constants.COUNTRY_ID, 0);
            Log.i("TAG", "Log ShortName" + localModel.getShortname());
            Log.i("TAG", "Log country_id" + localModel.getCountryId());
            Log.i("TAG", "Log country_id Intent" + country_id);
            Log.i("TAG", "Log country_id model " + localModel.getCountryId());

            getCityList(country_id,getActiviy());

        }


    }

    public void initAdapter() {

        if (localModel != null && localModel.getCityId() != null)
            city_id = Integer.parseInt(localModel.getCityId());
        BranchAdapter cityAdapter = new BranchAdapter(getActiviy(), list, city_id, (position, cityModel) ->
                onCitySelected(cityModel));
        binding.recycler.setAdapter(cityAdapter);


    }


    private void getCityList(int country_id, Activity activity) {
        list.clear();
        Log.i("TAG", "Log country_id" + country_id);

        GlobalData.progressDialog(getActiviy(), R.string.upload_date, R.string.please_wait_upload);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            GlobalData.hideProgressDialog();
            CityModelResult result = (CityModelResult) obj;

            if (func.equals(Constants.ERROR)) {
                String message = getString(R.string.fail_to_get_data);
                if (result != null && result.getMessage() != null) {
                    message = result.getMessage();
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.recycler.setVisibility(View.GONE);


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
                        binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);
                        binding.recycler.setVisibility(View.GONE);

                        Toast(getString(R.string.no_cities));
                    }
                } else {
                    Toast(getString(R.string.fail_to_get_data));
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.recycler.setVisibility(View.GONE);

                }
            }

        }).CityHandle(country_id,activity);

    }

    public void onCitySelected(CityModel cityModel) {
        UtilityApp.setIsFirstRun(false);
        city_id = cityModel.getId();
        localModel.setCityId(String.valueOf(city_id));
        UtilityApp.setLocalData(localModel);
        if (getCallingActivity() != null) {
            setResult(RESULT_OK);
        } else {
            Intent intent = new Intent(getActiviy(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra(Constants.FRAG_HOME,true);
            startActivity(intent);
        }
        finish();

    }

}