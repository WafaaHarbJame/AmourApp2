package com.ramez.shopp.activities


import android.os.Bundle
import android.view.View
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Models.SettingModel
import com.ramez.shopp.classes.UtilityApp
import com.ramez.shopp.Models.ResultAPIModel
import com.ramez.shopp.R
import com.ramez.shopp.databinding.ActivityConditionBinding


class ConditionActivity : ActivityBase() {
    lateinit var binding: ActivityConditionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConditionBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        setTitle(R.string.conditions)
        if (UtilityApp.getSettingData() != null) {
            val settingModel = UtilityApp.getSettingData()
            binding.textTv.text = settingModel.conditions
        } else {
            setting
        }
    }

    val setting: Unit
        get() {
            DataFeacher(false, object :DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    val result = obj as ResultAPIModel<SettingModel>
                    if (IsSuccess) {
                        val settingModel = SettingModel()
                        settingModel.about = result.data.about
                        settingModel.conditions = result.data.conditions
                        settingModel.privacy = result.data.privacy
                        UtilityApp.setSetting(settingModel)
                        binding.textTv.text = settingModel.conditions
                    }                }

            }).getSetting()
        }
}