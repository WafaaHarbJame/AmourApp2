package com.amour.shop.activities


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.Models.CityModelResult
import com.amour.shop.classes.Constants
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.CityModel
import com.amour.shop.Models.CountryModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.R
import com.amour.shop.SplashScreenActivity
import com.amour.shop.adapter.BranchAdapter
import com.amour.shop.adapter.CountriesAdapter
import com.amour.shop.adapter.CountriesAdapter.OnCountryClick
import com.amour.shop.databinding.ActivityChangeCityBranchBinding
import es.dmoral.toasty.Toasty
import java.util.ArrayList

class ChangeCityBranchActivity : ActivityBase(), OnCountryClick {

    lateinit var binding: ActivityChangeCityBranchBinding
    var cityModelArrayList: MutableList<CityModel>? = null
    var countries: ArrayList<CountryModel>? = null
    var userId = 0
    var cityId = 0
    var countryId = 0
    var localModel: LocalModel? = null
    var oldCountryName: String? = ""
    var newCountryName: String? = ""

    private var linearLayoutManager: LinearLayoutManager? = null
    private var cityAdapter: BranchAdapter? = null
    private var countriesAdapter: CountriesAdapter? = null
    private var toggleButton = false
    private var toggleLangButton = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeCityBranchBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        setTitle(R.string.change_city_branch)
        cityModelArrayList = ArrayList()
        countries = ArrayList()
        localModel = UtilityApp.getLocalData()
        if (localModel != null && localModel?.cityId?.isNotEmpty() == true) {
            cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        }

//        Log.i("tag", "Log cityId" + localModel.getCityId());
        if (UtilityApp.isLogin()) {
            binding.countryLY.visibility = View.GONE
        } else {
            binding.countryLY.visibility = View.VISIBLE
        }

        binding.chooseBranchTv.setOnClickListener {
            toggleButton = !toggleButton
            if (toggleButton) {
                binding.branchContainer.visibility = View.VISIBLE
                binding.divider.visibility = View.GONE
                binding.branchLY.background = ContextCompat.getDrawable(
                    activity,
                    R.color.white
                )
            } else {
                binding.branchContainer.visibility = View.GONE
                binding.divider.visibility = View.VISIBLE
                binding.branchLY.background = ContextCompat.getDrawable(
                    activity,
                    R.drawable.spinner_style
                )
            }
        }

        binding.chooseCountryTv.setOnClickListener {
            toggleLangButton = !toggleLangButton
            if (toggleLangButton) {
                binding.countryContainer.visibility = View.VISIBLE
                binding.countryLY.background = ContextCompat.getDrawable(
                    activity,
                    R.drawable.lang_style
                )
            } else {
                binding.countryContainer.visibility = View.GONE
                binding.countryLY.background = ContextCompat.getDrawable(
                    activity,
                    R.drawable.spinner_style
                )
            }
        }

        linearLayoutManager = LinearLayoutManager(activity)
        binding.branchRecycler.layoutManager = linearLayoutManager
        countryId = localModel?.countryId ?: Constants.default_country_id
//        countries?.add(
//            CountryModel(
//                4,
//                getString(R.string.Oman_ar),
//                getString(R.string.Oman),
//                getString(R.string.oman_shotname),
//                968,
//                "OMR",
//                Constants.three,
//                R.drawable.ic_flag_oman
//            )
//        )
//        countries?.add(
//            CountryModel(
//                Constants.default_country_id,
//                getString(R.string.Bahrain_ar),
//                getString(R.string.Bahrain),
//                getString(R.string.bahrain_shotname),
//                973,
//                "BHD",
//                Constants.three,
//                R.drawable.ic_flag_behrain
//            )
//        )
//        countries?.add(
//            CountryModel(
//                117,
//                getString(R.string.Kuwait_ar),
//                getString(R.string.Kuwait),
//                getString(R.string.Kuwait_shotname),
//                965,
//                "KWD",
//                Constants.three,
//                R.drawable.ic_flag_kuwait
//            )
//        )
//        countries?.add(
//            CountryModel(
//                178,
//                getString(R.string.Qatar_ar),
//                getString(R.string.Qatar),
//                getString(R.string.Qatar_shotname),
//                974,
//                "QAR",
//                Constants.two,
//                R.drawable.ic_flag_qatar
//            )
//        )
//        countries?.add(
//            CountryModel(
//                191,
//                getString(R.string.Saudi_Arabia_ar),
//                getString(R.string.Saudi_Arabia),
//                getString(R.string.Saudi_Arabia_shortname),
//                966,
//                "SAR",
//                Constants.two,
//                R.drawable.ic_flag_saudi_arabia
//            )
//        )
//        countries?.add(
//            CountryModel(
//                229,
//                getString(R.string.United_Arab_Emirates_ar),
//                getString(R.string.United_Arab_Emirates),
//                getString(R.string.United_Arab_Emirates_shotname),
//                971,
//                "AED",
//                Constants.two,
//                R.drawable.ic_flag_uae
//            )
//        )
//        initAdapter()
        getCityList(countryId, activity)
        binding.failGetDataLY.refreshBtn.setOnClickListener { view1 ->
            getCityList(
                countryId,
                activity
            )
        }
        binding.saveBut.setOnClickListener {
            localModel?.countryId = countryId
            localModel?.cityId = cityId.toString()
            localModel?.countryName = newCountryName
            saveLocalData(localModel)
            UtilityApp.setFastQCartCount(0)
            UtilityApp.setFastQCartTotal(0f)
            UtilityApp.setContinuousScan(false)
            UtilityApp.setScanSound(false)
            FirebaseMessaging.getInstance()
                .subscribeToTopic(newCountryName ?: oldCountryName ?: Constants.default_short_name)
                .addOnCompleteListener { task: Task<Void?> ->
                    var msg = getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_failed)
                    }
                    Log.d("TAG", msg)
                }
            FirebaseMessaging.getInstance()
                .unsubscribeFromTopic(oldCountryName ?: Constants.default_short_name)
                .addOnCompleteListener { task: Task<Void?> ->
                    var msg = getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_failed)
                    }
                    Log.d("TAG", msg)
                }
            Toasty.success(activity, R.string.change_success, Toast.LENGTH_SHORT, true).show()
            val intent = Intent(activity, SplashScreenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveLocalData(localModel: LocalModel?) {
        UtilityApp.setLocalData(localModel)
    }

    fun initAdapter() {
        countriesAdapter = CountriesAdapter(activity, this, countries, countryId)
        binding.countryRecycler.adapter = countriesAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initCityAdapter() {
        if (cityId == 0) {
            cityId = cityModelArrayList?.get(0)?.id ?: Constants.default_storeId.toInt()
            localModel?.cityId = cityId.toString()
            localModel?.countryName = newCountryName
            saveLocalData(localModel)

        }
        cityAdapter?.notifyDataSetChanged()

        cityAdapter = BranchAdapter(
            activity, cityModelArrayList, cityId
        ) { position: Int, cityModel: CityModel ->
            onCitySelected(
                position,
                cityModel
            )
        }
        binding.branchRecycler.adapter = cityAdapter
    }

    private fun onCitySelected(position: Int, cityModel: CityModel) {
        cityId = cityModel.id
        localModel?.cityId = cityModel.id.toString()
        localModel?.countryName = newCountryName
        var branchName: String? = ""
        branchName = if (UtilityApp.getLanguage() == Constants.English) {
            cityModel.name
        } else {
            cityModel.nameAr
        }
        localModel?.branchName = branchName
        UtilityApp.setBranchName(branchName)
        saveLocalData(localModel)


        localModel?.countryId = countryId
        localModel?.cityId = cityId.toString()
        localModel?.countryName = newCountryName
        saveLocalData(localModel)
        UtilityApp.setFastQCartCount(0)
        UtilityApp.setFastQCartTotal(0f)
        UtilityApp.setContinuousScan(false)
        UtilityApp.setScanSound(false)
        FirebaseMessaging.getInstance()
            .subscribeToTopic(newCountryName ?: oldCountryName ?: Constants.default_short_name)
            .addOnCompleteListener { task: Task<Void?> ->
                var msg = getString(R.string.msg_subscribed)
                if (!task.isSuccessful) {
                    msg = getString(R.string.msg_subscribe_failed)
                }
                Log.d("TAG", msg)
            }
        FirebaseMessaging.getInstance()
            .unsubscribeFromTopic(oldCountryName ?: Constants.default_short_name)
            .addOnCompleteListener { task: Task<Void?> ->
                var msg = getString(R.string.msg_subscribed)
                if (!task.isSuccessful) {
                    msg = getString(R.string.msg_subscribe_failed)
                }
                Log.d("TAG", msg)
            }
        Toasty.success(activity, R.string.change_success, Toast.LENGTH_SHORT, true).show()
        val intent = Intent(activity, SplashScreenActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getCityList(country_id: Int, activity: Activity) {
        cityModelArrayList = UtilityApp.getCountryBranches(country_id)
        Log.i("TAG", "Log country_id$country_id")
        if (cityModelArrayList.isNullOrEmpty()) {
            binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
            binding.dataLY.visibility = View.GONE
            binding.noDataLY.noDataLY.visibility = View.GONE
            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
            Log.i("TAG", "Log country_id$country_id")
            DataFeacher(false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    var message = getString(R.string.fail_to_get_data)
                    binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                    val result = obj as CityModelResult?
                    if (func == Constants.ERROR) {
                        if (result != null && result.message != null) {
                            message = result.message
                        }
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                    } else if (func == Constants.FAIL) {
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                    } else if (func == Constants.NO_CONNECTION) {
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection)
                        binding.failGetDataLY.noInternetIv.visibility = View.VISIBLE
                        binding.dataLY.visibility = View.GONE
                    } else {
                        if (IsSuccess) {
                            binding.dataLY.visibility = View.VISIBLE
                            if (result?.data != null && result.data.size > 0) {
                                cityModelArrayList = result.data
                                UtilityApp.saveCountryBranches(country_id, cityModelArrayList)
                                initCityAdapter()
                            }
                        }
                    }
                }

            }).cityHandle(country_id, activity)
        } else {
            initCityAdapter()

        }


    }

    override fun onCountryClicked(position: Int, countryModel: CountryModel) {
        countryId = countryModel.id
        oldCountryName = localModel?.shortname
        newCountryName = countryModel.shortname
        localModel?.shortname = countryModel.shortname
        localModel?.currencyCode = countryModel.currencyCode
        localModel?.fractional = countryModel.fractional
        localModel?.phonecode = countryModel.phonecode
        localModel?.countryNameAr = countryModel.countryNameAr
        localModel?.countryNameEn = countryModel.countryNameEn
        saveLocalData(localModel)
        Log.i("tag", "Log click countryId $countryId")
        Log.i("tag", "Log click oldCountry $oldCountryName")
        Log.i("tag", "Log click newCountryName $newCountryName")
        Log.i("tag", "Log click ShortName " + countryModel.shortname)
        getCityList(countryId, activity)
        toggleLangButton = !toggleLangButton
        toggleButton = !toggleButton
        binding.countryContainer.visibility = View.GONE
        binding.countryLY.background = ContextCompat.getDrawable(activity, R.drawable.spinner_style)
        binding.branchContainer.visibility = View.VISIBLE
        binding.branchLY.background = ContextCompat.getDrawable(activity, R.drawable.lang_style)
    }
}