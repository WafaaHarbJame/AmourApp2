package com.amour.shop.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.jaeger.library.StatusBarUtil
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.Models.CityModelResult
import com.amour.shop.classes.Constants
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.CityModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.R

import com.amour.shop.databinding.ActivityFastQactivityBinding
import java.util.ArrayList
import com.amour.shop.BuildConfig


class FastqActivity : ActivityBase() {

    private lateinit var binding: ActivityFastQactivityBinding
    var localModel: LocalModel? = null
    var branchName: String = ""
    private var countryId = 0
    private var cityId = 0
    var cityModelArrayList: ArrayList<CityModel>? = null
    private var selectedCityModel: CityModel? = null
    var cartCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFastQactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkCart()

        changeToolBarColor()

        StatusBarUtil.setColor(this, ContextCompat.getColor(activity, R.color.fastq_color), 0)

        localModel = UtilityApp.getLocalData()


        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()

        getCityList(countryId)

        initListeners()


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

            }).cityHandle(countryId, activity)
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
        binding.toolBar.toolbarBack.setBackgroundColor(ContextCompat.getColor(activity, R.color.fastq_color))
        binding.toolBar.logoImg.visibility = gone
        binding.toolBar.mainTitleTv.visibility = visible

    }

    private fun initListeners() {
        binding.scanBut.setOnClickListener {
            val intent = Intent(activity, FastqScanActivity::class.java)
            activity.startActivity(intent)

        }

        binding.settingBut.setOnClickListener {
            val intent = Intent(activity, PagesActivity::class.java)
            intent.putExtra(Constants.KEY_FRAGMENT_TYPE, Constants.FRAG_FASTQ_SETTINGS)
            activity.startActivity(intent)

        }

        binding.historyBut.setOnClickListener {
            val intent = Intent(activity, PagesActivity::class.java)
            intent.putExtra(Constants.KEY_FRAGMENT_TYPE, Constants.FRAG_FASTQ_HISTORY)
            activity.startActivity(intent)

        }


        binding.toolBar.cartFastBtn.setOnClickListener {

            val intent = Intent(activity, FastqCartActivity::class.java)
            startActivity(intent)

        }
    }


    private fun checkCart() {

        cartCount = UtilityApp.getFastQCartCount()

        if (cartCount > 0) {
            binding.toolBar.cartFastBtn.visibility=visible
            binding.toolBar.cartsCountTv.text=cartCount.toString()

        } else {
            binding.toolBar.cartFastBtn.visibility=gone
        }
    }

    override fun onResume() {
        super.onResume()
        checkCart()
    }


}