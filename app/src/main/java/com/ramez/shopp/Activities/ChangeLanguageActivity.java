package com.ramez.shopp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.SettingModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.databinding.ActivityChangeLanguageBinding;

public class ChangeLanguageActivity extends ActivityBase {
    private ActivityChangeLanguageBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeLanguageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.chooseLangTv.setOnClickListener(view1 -> {
            binding.langContainer.setVisibility(View.VISIBLE);
            binding.chooseLangTv.setVisibility(View.GONE);

        });

        binding.containerArabic.setOnClickListener(view1 -> {
            binding.imgEnglishTick.setVisibility(View.INVISIBLE);
            binding.imgArabicTick.setVisibility(View.VISIBLE);
            UtilityApp.setLanguage(Constants.Arabic);
            UtilityApp.setAppLanguage();
            getSetting();

        });

        binding.containerEnglish.setOnClickListener(view1 -> {
            binding.imgEnglishTick.setVisibility(View.VISIBLE);
            binding.imgArabicTick.setVisibility(View.INVISIBLE);
            UtilityApp.setLanguage(Constants.English);
            UtilityApp.setAppLanguage();
            getSetting();

        });

        binding.toolBar.backBtn.setVisibility(View.GONE);

    }

    private void ChooseWelcomeActivity() {
//        Intent intent = new Intent(getActiviy(), WelcomeActivity.class);
//        startActivity(intent);
////        finish();

    }

    public void getSetting() {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<SettingModel> result = (ResultAPIModel<SettingModel>) obj;

            if (IsSuccess) {
                SettingModel settingModel = new SettingModel();
                if (result.data != null) {
                    settingModel.setAbout(result.data.getAbout());
                    settingModel.setConditions(result.data.getConditions());
                    settingModel.setPrivacy(result.data.getPrivacy());
                    UtilityApp.setSetting(settingModel);
                    navigateChooseCityActivity();
                }

            } else {

                navigateChooseCityActivity();

            }


        }).getSetting();
    }

    public void navigateChooseCityActivity() {
        startActivity(new Intent(ChangeLanguageActivity.this, ChooseCityActivity.class));
        finish();
    }

}