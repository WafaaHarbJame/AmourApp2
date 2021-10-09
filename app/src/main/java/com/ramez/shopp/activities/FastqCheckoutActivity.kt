package com.ramez.shopp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.jaeger.library.StatusBarUtil
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.CityModelResult
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.CityModel
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.R
import com.ramez.shopp.databinding.ActivityFastqCheckoutActivityBinding
import java.util.*


class FastqCheckoutActivity : ActivityBase() {

    private lateinit var binding: ActivityFastqCheckoutActivityBinding
    var localModel: LocalModel? = null
    var branchName: String = ""
    private var countryId = 0
    private var cityId = 0
    var cityModelArrayList: ArrayList<CityModel>? = null
    private var selectedCityModel: CityModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFastqCheckoutActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeToolBarColor()


        StatusBarUtil.setColor(this, ContextCompat.getColor(activiy, R.color.fastq_color), 0)

        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activiy
            )


        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()

        getCityList(countryId)

        initListeners()

    }

    fun initListeners() {

        binding.inStoreLabel.setOnClickListener {

            val intent = Intent(activiy, FastqSummaryActivity::class.java)
            startActivity(intent)

        }

    }

    private fun getCityList(country_id: Int) {
        Log.i(javaClass.name, "Log country_id$country_id")
        callCityListApi()
    }

    private fun callCityListApi() {
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        val result = obj as CityModelResult
                        if (result.data != null && result.data.size > 0) {
                            cityModelArrayList = ArrayList(result.data)
                            searchSelectedCity()
                            setBranchData()
                        }
                    }
                }

            }).CityHandle(countryId, activiy)
    }


    private fun searchSelectedCity() {
        for (cityModel in cityModelArrayList!!) {
            if (cityId == cityModel.id) {
                selectedCityModel = cityModel
                break
            }
        }
    }

    private fun setBranchData() {
        if (selectedCityModel != null) {
            branchName = selectedCityModel!!.cityName
            Log.i(javaClass.name, "Log branchName $branchName")
            title = branchName
        }
    }

    private fun changeToolBarColor() {
        binding.toolBar.toolbarBack.setBackgroundColor(ContextCompat.getColor(activiy, R.color.fastq_color))
        binding.toolBar.logoImg.visibility = gone
        binding.toolBar.mainTitleTv.visibility = visible

    }

}