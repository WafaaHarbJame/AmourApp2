package com.ramez.shopp.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaeger.library.StatusBarUtil
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.CartModel
import com.ramez.shopp.Classes.CityModelResult
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.CityModel
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.R
import com.ramez.shopp.adapter.FastqCartAdapter

import com.ramez.shopp.databinding.ActivityFastQactivityBinding
import com.ramez.shopp.databinding.ActivityFastqCartActivityBinding
import com.ramez.shopp.databinding.ActivityFastqSummaryActivityBinding
import java.util.ArrayList


class FastqSummaryActivity : ActivityBase() {

    var localModel: LocalModel? = null
    var branchName: String = ""
    private var countryId = 0
    private var cityId = 0
    var cityModelArrayList: ArrayList<CityModel>? = null
    private var selectedCityModel: CityModel? = null


    private lateinit var binding: ActivityFastqSummaryActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFastqSummaryActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeToolBarColor()

        binding.rv.layoutManager = LinearLayoutManager(activiy)


        StatusBarUtil.setColor(this, ContextCompat.getColor(activiy, R.color.fastq_color), 0)

        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activiy
            )


        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()

        getCityList(countryId)

        initListeners()

        initAdapter()

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

    private fun initListeners() {
        binding.closeBtn.setOnClickListener {

            onBackPressed()

        }

    }

    fun initAdapter() {

        val list = mutableListOf(
            CartModel(),
            CartModel(),
            CartModel(),
            CartModel(),
        )

        val adapter = FastqCartAdapter(activiy, list)
        binding.rv.adapter = adapter

    }

    override fun onBackPressed() {

        val intent = Intent(activiy, FastqActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)

    }

}