package com.amour.shop.activities


import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.BuildConfig
import com.amour.shop.Models.SettingModel
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.ResultAPIModel
import com.amour.shop.R
import com.amour.shop.classes.Constants
import com.amour.shop.databinding.ActivityConditionBinding


class ConditionActivity : ActivityBase() {
    lateinit var binding: ActivityConditionBinding
    var conditionStr: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConditionBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        setTitle(R.string.conditions)

            conditionStr = getString(R.string.amour_condition)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.textTv.text = Html.fromHtml(conditionStr, Html.FROM_HTML_MODE_COMPACT);

        } else {
            binding.textTv.text = Html.fromHtml(conditionStr)
        }
    }

    private val setting: Unit
        get() {
            DataFeacher(false, object :DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        val result = obj as ResultAPIModel<SettingModel?>?
                        if(result?.status==200){
                            val settingModel = SettingModel()
                            settingModel.about = result.data?.about ?: ""
                            settingModel.conditions = result.data?.conditions ?: ""
                            settingModel.privacy = result.data?.privacy ?: ""
                            UtilityApp.setSetting(settingModel)
                            binding.textTv.text = settingModel.conditions
                        }
                        else{
                            binding.textTv.text =getString(R.string.no_data)

                        }


                    }                }

            }).getSettings()
        }
}