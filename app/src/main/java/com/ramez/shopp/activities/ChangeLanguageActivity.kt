package com.ramez.shopp.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.classes.Constants
import com.ramez.shopp.Models.SettingModel
import com.ramez.shopp.classes.UtilityApp
import com.ramez.shopp.Models.ResultAPIModel
import com.ramez.shopp.databinding.ActivityChangeLanguageBinding


class ChangeLanguageActivity : ActivityBase() {
    private lateinit var binding: ActivityChangeLanguageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.chooseLangTv.setOnClickListener {
            binding.langContainer.visibility = View.VISIBLE
            binding.chooseLangTv.visibility = View.GONE
        }
        binding.containerArabic.setOnClickListener {
            binding.imgEnglishTick.visibility = View.INVISIBLE
            binding.imgArabicTick.visibility = View.VISIBLE
            UtilityApp.setLanguage(Constants.Arabic)
            UtilityApp.setAppLanguage()
            setting
        }
        binding.containerEnglish.setOnClickListener {
            binding.imgEnglishTick.visibility = View.VISIBLE
            binding.imgArabicTick.visibility = View.INVISIBLE
            UtilityApp.setLanguage(Constants.English)
            UtilityApp.setAppLanguage()
            setting
        }
        binding.toolBar.backBtn.visibility = View.GONE
    }


    val setting: Unit
        get() {
            DataFeacher(false, object :DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    val result = obj as ResultAPIModel<SettingModel?>?
                    if (IsSuccess) {
                        if (result?.data != null) {
                            val settingModel = SettingModel()
                            settingModel.about = result.data?.about + ""
                            settingModel.conditions = result.data?.conditions + ""
                            settingModel.privacy = result.data?.privacy + ""
                            UtilityApp.setSetting(settingModel)
                            navigateChooseCityActivity()
                        }
                    } else {
                        navigateChooseCityActivity()
                    }                }

            }).getSetting()
        }

    fun navigateChooseCityActivity() {
        startActivity(Intent(this@ChangeLanguageActivity, ChooseCityActivity::class.java))
        finish()
    }
}