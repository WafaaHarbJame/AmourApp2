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
import com.amour.shop.databinding.ActivityTermsBinding


class TermsActivity : ActivityBase() {
    private lateinit var binding: ActivityTermsBinding
    var termsStr: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        setTitle(R.string.terms2)

            termsStr = getString(R.string.amour_privacy)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.textTv.text = Html.fromHtml(termsStr, Html.FROM_HTML_MODE_COMPACT);

        } else {
            binding.textTv.text = Html.fromHtml(termsStr)
        }
    }

    private val setting: Unit
        get() {
            DataFeacher(false,object :DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    val result = obj as ResultAPIModel<SettingModel?>?
                    if (IsSuccess) {
                        if(result?.status==200){
                            val settingModel = SettingModel()
                            settingModel.about = result.data?.about ?: ""
                            settingModel.conditions = result.data?.conditions ?: ""
                            settingModel.privacy = result.data?.privacy ?: ""
                            UtilityApp.setSetting(settingModel)
                            UtilityApp.setSetting(settingModel)
                            binding.textTv.text = settingModel.privacy
                        }
                        else{
                            binding.textTv.text =getString(R.string.no_data)

                        }


                    }                }

            }).getSettings()
        }
}