package com.amour.shop.activities


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.core.text.HtmlCompat
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.BuildConfig
import com.amour.shop.Models.SettingModel
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.ResultAPIModel
import com.amour.shop.R
import com.amour.shop.classes.Constants
import com.amour.shop.classes.GlobalData
import com.amour.shop.databinding.ActivityAboutBinding


class AboutActivity : ActivityBase() {
    lateinit var binding: ActivityAboutBinding
    var aboutStr: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        setTitle(R.string.text_title_about_us)

            aboutStr = getString(R.string.amour_about_us)


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            binding.textTv.text = Html.fromHtml(aboutStr, Html.FROM_HTML_MODE_COMPACT);
//
//        } else {
//            binding.textTv.text = Html.fromHtml(aboutStr)
//        }

        binding.textTv.text=aboutStr
        binding.appVersionTv.text =
            getString(R.string.app_version) + UtilityApp.appVersion.toString() + ""
    }

    private val setting: Unit
        get() {
            DataFeacher(false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        val result = obj as ResultAPIModel<SettingModel?>?
                        if (result?.status == 200) {
                            val settingModel = SettingModel()
                            settingModel.about = result.data?.about ?: ""
                            settingModel.conditions = result.data?.conditions ?: ""
                            settingModel.privacy = result.data?.privacy ?: ""
                            UtilityApp.setSetting(settingModel)
                            binding.textTv.text = settingModel.about
                        } else {
                            binding.textTv.text = getString(R.string.no_data)

                        }

                    }
                }

            }).getSettings()
        }
}