package com.ramez.shopp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.ramez.shopp.Adapter.BranchAdapter;
import com.ramez.shopp.Adapter.CityAdapter;
import com.ramez.shopp.Adapter.CountriesAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.CityModelResult;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.MainActivity;
import com.ramez.shopp.Models.CityModel;
import com.ramez.shopp.Models.CountryModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityChangeCityBranchBinding;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ChangeCityBranchActivity extends ActivityBase implements CountriesAdapter.OnCountryClick {
    ActivityChangeCityBranchBinding binding;
    ArrayList<CityModel> cityModelArrayList;
    ArrayList<CountryModel> countries;
    int user_id = 0;
    int city_id = 0;
    int countryId;
    LocalModel localModel;
    String oldCountryName;
    String newCountryName;
    private LinearLayoutManager linearLayoutManager;
    private BranchAdapter cityAdapter;
    private CountriesAdapter countriesAdapter;

    private boolean toggleButton = false;
    private boolean toggleLangButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeCityBranchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setTitle(R.string.change_city_branch);
        cityModelArrayList = new ArrayList<>();
        countries = new ArrayList<>();

        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActiviy());

        if (localModel != null && !localModel.getCityId().isEmpty()) {
            city_id = Integer.parseInt(localModel.getCityId());

        }


//        Log.i("tag", "Log cityId" + localModel.getCityId());

        if (UtilityApp.isLogin()) {
            binding.countryLY.setVisibility(View.GONE);

        } else {
            binding.countryLY.setVisibility(View.VISIBLE);

        }


        binding.chooseBranchTv.setOnClickListener(view1 -> {

            toggleButton = !toggleButton;

            if (toggleButton) {
                binding.branchContainer.setVisibility(View.VISIBLE);
                binding.divider.setVisibility(View.GONE);
                binding.branchLY.setBackground(ContextCompat.getDrawable(getActiviy(), R.color.white));

            } else {
                binding.branchContainer.setVisibility(View.GONE);
                binding.divider.setVisibility(View.VISIBLE);
                binding.branchLY.setBackground(ContextCompat.getDrawable(getActiviy(), R.drawable.spinner_style));
            }

        });

        binding.chooseCountryTv.setOnClickListener(view1 -> {

            toggleLangButton = !toggleLangButton;

            if (toggleLangButton) {
                binding.countryContainer.setVisibility(View.VISIBLE);
                binding.countryLY.setBackground(ContextCompat.getDrawable(getActiviy(), R.drawable.lang_style));
            } else {
                binding.countryContainer.setVisibility(View.GONE);
                binding.countryLY.setBackground(ContextCompat.getDrawable(getActiviy(), R.drawable.spinner_style));
            }

        });


        linearLayoutManager = new LinearLayoutManager(getActiviy());
        binding.branchRecycler.setLayoutManager(linearLayoutManager);

        countryId = localModel.getCountryId();

        countries.add(new CountryModel(4, getString(R.string.Oman_ar), getString(R.string.Oman), getString(R.string.oman_shotname), 968, "OMR", Constants.three, R.drawable.ic_flag_oman));
        countries.add(new CountryModel(17, getString(R.string.Bahrain_ar), getString(R.string.Bahrain), getString(R.string.bahrain_shotname), 973, "BHD", Constants.three, R.drawable.ic_flag_behrain));
        countries.add(new CountryModel(117, getString(R.string.Kuwait_ar), getString(R.string.Kuwait), getString(R.string.Kuwait_shotname), 965, "KWD", Constants.three, R.drawable.ic_flag_kuwait));
        countries.add(new CountryModel(178, getString(R.string.Qatar_ar), getString(R.string.Qatar), getString(R.string.Qatar_shotname), 974, "QAR", Constants.two, R.drawable.ic_flag_qatar));
        countries.add(new CountryModel(191, getString(R.string.Saudi_Arabia_ar), getString(R.string.Saudi_Arabia), getString(R.string.Saudi_Arabia_shortname), 191, "SAR", Constants.two, R.drawable.ic_flag_saudi_arabia));
        countries.add(new CountryModel(229, getString(R.string.United_Arab_Emirates_ar), getString(R.string.United_Arab_Emirates), getString(R.string.United_Arab_Emirates_shotname), 971, "AED", Constants.two, R.drawable.ic_flag_uae));

        initAdapter();


        getCityList(countryId, getActiviy());


        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {
            getCityList(countryId, getActiviy());
        });


        binding.saveBut.setOnClickListener(view1 -> {
            localModel.setCountryId(countryId);
            localModel.setCityId(String.valueOf(city_id));
            localModel.setCountryName(newCountryName);
            UtilityApp.setLocalData(localModel);
            FirebaseMessaging.getInstance().subscribeToTopic(newCountryName)

                    .addOnCompleteListener(task -> {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d("TAG", msg);
                    });


            FirebaseMessaging.getInstance().unsubscribeFromTopic(oldCountryName)

                    .addOnCompleteListener(task -> {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d("TAG", msg);
                    });


            Toasty.success(getActiviy(), R.string.change_success, Toast.LENGTH_SHORT, true).show();
            Intent intent = new Intent(getActiviy(), SplashScreenActivity.class);
            startActivity(intent);
            finish();


        });


    }

    public void initAdapter() {

        countriesAdapter = new CountriesAdapter(getActiviy(), this, countries, countryId);
        binding.countryRecycler.setAdapter(countriesAdapter);
    }


    public void initCityAdapter() {

        cityAdapter = new BranchAdapter(getActiviy(), cityModelArrayList, city_id, this::onCitySelected);
        binding.branchRecycler.setAdapter(cityAdapter);


    }

    public void onCitySelected(int position, CityModel cityModel) {
        city_id = cityModel.getId();
        localModel.setCityId(String.valueOf(cityModel.getId()));
        localModel.setCountryName(newCountryName);
        UtilityApp.setLocalData(localModel);


    }

    private void getCityList(int country_id, Activity activity) {

        cityModelArrayList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.dataLY.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);


        Log.i("TAG", "Log country_id" + country_id);
        new DataFeacher(false, (obj, func, IsSuccess) -> {

            String message = getString(R.string.fail_to_get_data);
            binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);
            CityModelResult result = (CityModelResult) obj;
            if (func.equals(Constants.ERROR)) {

                if (result != null && result.getMessage() != null) {
                    message = result.getMessage();
                }
                binding.dataLY.setVisibility(View.GONE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(message);

            } else if (func.equals(Constants.FAIL)) {

                binding.dataLY.setVisibility(View.GONE);
                binding.noDataLY.noDataLY.setVisibility(View.GONE);
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(message);


            } else if (func.equals(Constants.NO_CONNECTION)) {
                binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
                binding.dataLY.setVisibility(View.GONE);

            } else {
                if (IsSuccess) {
                    binding.dataLY.setVisibility(View.VISIBLE);

                    if (result.getData() != null && result.getData().size() > 0) {
                        cityModelArrayList = result.getData();
                        if (city_id == 0) {
                            city_id = cityModelArrayList.get(0).getId();
                            localModel.setCityId(String.valueOf(city_id));
                            localModel.setCountryName(newCountryName);
                            UtilityApp.setLocalData(localModel);
                        }

                        initCityAdapter();
                        cityAdapter.notifyDataSetChanged();

                    }
                }
            }


        }).CityHandle(country_id, activity);

    }

    @Override
    public void onCountryClicked(int position, CountryModel countryModel) {
        countryId = countryModel.getId();
        oldCountryName = UtilityApp.getLocalData().getShortname();
        newCountryName = countryModel.getShortname();
        localModel.setShortname(countryModel.getShortname());
        localModel.setCurrencyCode(countryModel.getCurrencyCode());
        localModel.setFractional(countryModel.getFractional());
        localModel.setPhonecode(countryModel.getPhonecode());
        localModel.setCountryNameAr(countryModel.getCountryNameAr());
        localModel.setCountryNameEn(countryModel.getCountryNameEn());
        UtilityApp.setLocalData(localModel);
        Log.i("tag", "Log click countryId" + countryId);
        Log.i("tag", "Log click oldCountry" + oldCountryName);
        Log.i("tag", "Log click newCountryName" + newCountryName);
        Log.i("tag", "Log click ShortName" + countryModel.getShortname());
        getCityList(countryId, getActiviy());

        toggleLangButton = !toggleLangButton;
        toggleButton = !toggleButton;

        binding.countryContainer.setVisibility(View.GONE);
        binding.countryLY.setBackground(ContextCompat.getDrawable(getActiviy(), R.drawable.spinner_style));

        binding.branchContainer.setVisibility(View.VISIBLE);
        binding.branchLY.setBackground(ContextCompat.getDrawable(getActiviy(), R.drawable.lang_style));


    }
}