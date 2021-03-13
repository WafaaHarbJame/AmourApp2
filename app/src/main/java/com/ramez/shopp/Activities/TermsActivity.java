package com.ramez.shopp.Activities;

import android.os.Bundle;
import android.view.View;

import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.SettingModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityTermsBinding;

public class TermsActivity extends ActivityBase {
    private ActivityTermsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTermsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setTitle(R.string.terms2);

        if(UtilityApp.getSettingData()!=null){

            SettingModel settingModel=UtilityApp.getSettingData();
            binding.textTv.setText(settingModel.getPrivacy());
        }
        else {
            getSetting();
        }

    }

    public void getSetting() {

        new DataFeacher(false, (obj, func, IsSuccess) -> {

            ResultAPIModel<SettingModel> result = (ResultAPIModel<SettingModel>) obj;

            if (IsSuccess) {
                SettingModel settingModel = new SettingModel();
                settingModel.setAbout(result.data.getAbout());
                settingModel.setConditions(result.data.getConditions());
                settingModel.setPrivacy(result.data.getPrivacy());
                UtilityApp.setSetting(settingModel);
                binding.textTv.setText(settingModel.getPrivacy());

            }

        }).getSetting();
    }
}