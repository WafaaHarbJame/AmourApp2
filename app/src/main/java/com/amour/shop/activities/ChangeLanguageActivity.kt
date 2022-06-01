package com.amour.shop.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.BuildConfig
import com.amour.shop.classes.Constants
import com.amour.shop.Models.SettingModel
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.ResultAPIModel
import com.amour.shop.databinding.ActivityChangeLanguageBinding


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

            navigateChooseCityActivity(Constants.Arabic)


        }
        binding.containerEnglish.setOnClickListener {
            binding.imgEnglishTick.visibility = View.VISIBLE
            binding.imgArabicTick.visibility = View.INVISIBLE

            navigateChooseCityActivity(Constants.English)

        }
        binding.toolBar.backBtn.visibility = View.GONE
    }


    val setting: Unit
        get() {
            DataFeacher(false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    val result = obj as ResultAPIModel<SettingModel?>?
                    if (IsSuccess) {
                        if (result?.data != null) {
                            val settingModel = SettingModel()
                            settingModel.about = result.data?.about + ""
                            settingModel.conditions = result.data?.conditions + ""
                            settingModel.privacy = result.data?.privacy + ""
                            UtilityApp.setSetting(settingModel)
                        }
                    }
                }

            }).getSettings()
        }

    private fun navigateChooseCityActivity(lang: String) {
        UtilityApp.setLanguage(lang)
        UtilityApp.setAppLanguage()
        setting

        val intent = Intent(activity, ChooseNearCity::class.java)
        intent.putExtra(Constants.COUNTRY_ID, Constants.default_amour_country_id)
        startActivity(intent)


        finish()
    }
}