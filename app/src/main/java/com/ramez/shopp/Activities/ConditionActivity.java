package com.ramez.shopp.Activities;

import android.os.Bundle;
import android.view.View;

import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.SettingModel;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.ActivityConditionBinding;

public class ConditionActivity extends ActivityBase{
    ActivityConditionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityConditionBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        setTitle(R.string.conditions);
        if(UtilityApp.getSettingData()!=null){

            SettingModel settingModel=UtilityApp.getSettingData();
            binding.textTv.setText(settingModel.getConditions());
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
                binding.textTv.setText(settingModel.getConditions());

            }

        }).getSetting();
    }

}