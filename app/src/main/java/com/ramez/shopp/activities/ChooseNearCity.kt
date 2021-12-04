package com.ramez.shopp.activities


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.CityModelResult
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.GlobalData.Toast
import com.ramez.shopp.Classes.GlobalData.errorDialog
import com.ramez.shopp.Classes.GlobalData.hideProgressDialog
import com.ramez.shopp.Classes.GlobalData.progressDialog
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.CityModel
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.R
import com.ramez.shopp.adapter.BranchAdapter
import com.ramez.shopp.databinding.ActivityChooseNearstCityBinding
import java.util.*


class ChooseNearCity : ActivityBase() {
  lateinit  var binding: ActivityChooseNearstCityBinding
    var list: ArrayList<CityModel>? = null
    var city_id = 0
    var localModel: LocalModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseNearstCityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        list = ArrayList()
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activity
            )
        title = getString(R.string.change_city_branch)
        binding.recycler.layoutManager = LinearLayoutManager(activity)
        binding.toolBar.mainTitleTv.visibility = View.VISIBLE
        binding.toolBar.logoImg.visibility = View.GONE
        binding.toolBar.backBtn.visibility = View.VISIBLE
        extraIntent
    }

    private val extraIntent: Unit
        get() {
            val bundle = intent.extras
            if (bundle != null) {
                val countryId = intent.getIntExtra(Constants.COUNTRY_ID, 0)
                Log.i("TAG", "Log ShortName" + localModel?.shortname)
                Log.i("TAG", "Log country_id" + localModel?.countryId)
                Log.i("TAG", "Log country_id Intent$countryId")
                Log.i("TAG", "Log country_id model " + localModel?.countryId)
                getCityList(countryId, activity)
            }
        }

    fun initAdapter() {
        if (localModel != null && localModel?.cityId != null)
            city_id = localModel?.cityId!!.toInt()
        val cityAdapter = BranchAdapter(
            activity, list, city_id
        ) { _: Int, cityModel: CityModel -> onCitySelected(cityModel) }
        binding.recycler.adapter = cityAdapter
    }

    private fun getCityList(country_id: Int, activity: Activity) {
        list?.clear()
        Log.i("TAG", "Log country_id$country_id")
        progressDialog(this.activity, R.string.upload_date, R.string.please_wait_upload)
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false, object :DataFetcherCallBack {
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
                            if (list != null && list?.size?:0> 0) {
                                Log.i("TAG", "Log country size " + list?.size)
                                initAdapter()
                            }
                        } else {
                            binding.noDataLY.noDataLY.visibility = View.VISIBLE
                            binding.recycler.visibility = View.GONE
                            Toast(getString(R.string.no_cities))
                        }
                    } else {
                        Toast(getString(R.string.fail_to_get_data))
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.recycler.visibility = View.GONE
                    }
                }            }

        }).CityHandle(country_id, activity)
    }

    private fun onCitySelected(cityModel: CityModel) {
        UtilityApp.setIsFirstRun(false)
        city_id = cityModel.id
        localModel?.cityId = city_id.toString()
        var branchName: String? = ""
        branchName = if (UtilityApp.getLanguage() == Constants.English) {
            cityModel.name
        } else {
            cityModel.nameAr
        }
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
}