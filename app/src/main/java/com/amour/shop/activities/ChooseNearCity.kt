package com.amour.shop.activities


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.BuildConfig
import com.amour.shop.Models.*
import com.amour.shop.classes.GlobalData.Toast
import com.amour.shop.classes.GlobalData.errorDialog
import com.amour.shop.classes.GlobalData.hideProgressDialog
import com.amour.shop.classes.GlobalData.progressDialog
import com.amour.shop.R
import com.amour.shop.adapter.BranchAdapter
import com.amour.shop.classes.*
import com.amour.shop.databinding.ActivityChooseNearstCityBinding
import java.util.*


class ChooseNearCity : ActivityBase() {
    lateinit var binding: ActivityChooseNearstCityBinding
    var list: MutableList<CityModel>? = null
    var cityId = 0
    var countryId = 0
    var localModel: LocalModel? = null
    var shortName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseNearstCityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        list = ArrayList()
        localModel = UtilityApp.getLocalData()
        title = getString(R.string.change_city_branch)
        binding.recycler.layoutManager = LinearLayoutManager(activity)
        binding.toolBar.mainTitleTv.visibility = View.VISIBLE
        binding.toolBar.logoImg.visibility = View.GONE
        binding.toolBar.backBtn.visibility = View.VISIBLE
        extraIntent
        shortName = localModel?.shortname ?: Constants.default_short_name

    }

    private val extraIntent: Unit
        get() {
            val bundle = intent.extras
            if (bundle != null) {
                countryId = intent.getIntExtra(Constants.COUNTRY_ID, 0)
                Log.i("TAG", "Log ShortName" + localModel?.shortname)
                Log.i("TAG", "Log country_id" + localModel?.countryId)
                Log.i("TAG", "Log country_id Intent$countryId")
                Log.i("TAG", "Log country_id model " + localModel?.countryId)
                getCityList(countryId, activity)
            }
        }

    fun initAdapter() {
        if (localModel != null && localModel?.cityId != null)
            cityId = localModel?.cityId!!.toInt()
        val cityAdapter = BranchAdapter(
            activity, list, cityId
        ) { _: Int, cityModel: CityModel -> onCitySelected(cityModel) }
        binding.recycler.adapter = cityAdapter
    }

    private fun getCityList(country_id: Int, activity: Activity) {
        list = UtilityApp.getCountryBranches(country_id)
        Log.i("TAG", "Log country_id$country_id")
        if (list.isNullOrEmpty()) {
            progressDialog(this.activity, R.string.upload_date, R.string.please_wait_upload)
            binding.noDataLY.noDataLY.visibility = View.GONE
            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
            DataFeacher(false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    hideProgressDialog()
                    val result = obj as CityModelResult?
                    if (func == Constants.ERROR) {
                        var message: String? = getString(R.string.fail_to_get_data)
                        if (result != null && result.message != null) {
                            message = result.message
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                            binding.recycler.visibility = View.GONE
                        }
                        errorDialog(this@ChooseNearCity.activity, R.string.fail_to_get_data, message)
                    } else if (func == Constants.NO_CONNECTION) {
                        Toast(this@ChooseNearCity.activity, getString(R.string.no_internet_connection))
                    } else {
                        if (IsSuccess) {
                            if (result != null) {
                                list = result.data
                                if (list?.isNotEmpty() == true) {
                                    Log.i("TAG", "Log country size " + list?.size)
                                    initAdapter()
                                    UtilityApp.saveCountryBranches(country_id, list)
                                } else {
                                    binding.noDataLY.noDataLY.visibility = View.VISIBLE
                                    binding.recycler.visibility = View.GONE
                                    Toast(getString(R.string.no_cities))
                                }
                            }
                        } else {
                            Toast(getString(R.string.fail_to_get_data))
                            binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                            binding.recycler.visibility = View.GONE
                        }
                    }
                }

            }).cityHandle(country_id, activity)
        } else {
            initAdapter()
        }


    }

    private fun onCitySelected(cityModel: CityModel) {
        UtilityApp.setIsFirstRun(false)
        cityId = cityModel.id
        localModel?.cityId = cityId.toString()
        var branchName: String? = ""
        branchName = if (UtilityApp.getLanguage() == Constants.English) {
            cityModel.name
        } else {
            cityModel.nameAr
        }
        getCacheData()
        localModel?.branchName = branchName
        UtilityApp.setBranchName(branchName)
        UtilityApp.setLocalData(localModel)
        if (callingActivity != null) {
            setResult(RESULT_OK)
        } else {
            val intent = Intent(activity, RegisterLoginActivity::class.java)
            intent.putExtra(Constants.REGISTER, true)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        finish()
    }

    private fun getCacheData() {
        HandleApiCache().callApis(countryId,cityId,shortName)

    }


}