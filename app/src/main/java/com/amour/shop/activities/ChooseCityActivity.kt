package com.amour.shop.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.amour.shop.classes.Constants
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.CountryModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.R
import com.amour.shop.Utils.SharedPManger
import com.amour.shop.adapter.CountriesAdapter
import com.amour.shop.adapter.CountriesAdapter.OnCountryClick
import com.amour.shop.databinding.ActivityChooseCityBinding
import java.util.ArrayList


class ChooseCityActivity : ActivityBase(), OnCountryClick {
    var countries: ArrayList<CountryModel>? = null
    var sharedPManger: SharedPManger? = null
    var linearLayoutManager: LinearLayoutManager? = null
    private lateinit var binding: ActivityChooseCityBinding
    private var countriesAdapter: CountriesAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCityBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        countries = ArrayList()
        sharedPManger = SharedPManger(this)
        linearLayoutManager = LinearLayoutManager(activity)
        binding.recycler.layoutManager = linearLayoutManager
        binding.chooseCityTv.setOnClickListener { view1 ->
            binding.cityContainer.visibility = View.VISIBLE
            binding.chooseCityTv.visibility = View.GONE
        }
        countries?.add(
            CountryModel(
                4,
                getString(R.string.Oman_ar),
                getString(R.string.Oman),
                getString(R.string.oman_shotname),
                968,
                "OMR",
                Constants.three,
                R.drawable.ic_flag_oman
            )
        )
        countries?.add(
            CountryModel(
                Constants.default_country_id,
                getString(R.string.Bahrain_ar),
                getString(R.string.Bahrain),
                getString(R.string.bahrain_shotname),
                973,
                "BHD",
                Constants.three,
                R.drawable.ic_flag_behrain
            )
        )
        countries?.add(
            CountryModel(
                117,
                getString(R.string.Kuwait_ar),
                getString(R.string.Kuwait),
                getString(R.string.Kuwait_shotname),
                965,
                "KWD",
                Constants.three,
                R.drawable.ic_flag_kuwait
            )
        )
        countries?.add(
            CountryModel(
                178,
                getString(R.string.Qatar_ar),
                getString(R.string.Qatar),
                getString(R.string.Qatar_shotname),
                974,
                "QAR",
                Constants.two,
                R.drawable.ic_flag_qatar
            )
        )
        countries?.add(
            CountryModel(
                191,
                getString(R.string.Saudi_Arabia_ar),
                getString(R.string.Saudi_Arabia),
                getString(R.string.Saudi_Arabia_shortname),
                966,
                "SAR",
                Constants.two,
                R.drawable.ic_flag_saudi_arabia
            )
        )
        countries?.add(
            CountryModel(
                229,
                getString(R.string.United_Arab_Emirates_ar),
                getString(R.string.United_Arab_Emirates),
                getString(R.string.United_Arab_Emirates_shotname),
                971,
                "AED",
                Constants.two,
                R.drawable.ic_flag_uae
            )
        )
        UtilityApp.setCountriesData(countries)
        initAdapter()
        binding.toolBar.backBtn.visibility = View.GONE
    }

    private fun goToChooseNearCity(countryModel: CountryModel) {
        val localModel = LocalModel()
        localModel.countryId = countryModel.id
        localModel.shortname = countryModel.shortname
        localModel.phonecode = countryModel.phonecode
        localModel.countryNameAr = countryModel.countryNameAr
        localModel.countryNameEn = countryModel.countryNameEn
        localModel.currencyCode = countryModel.currencyCode
        localModel.fractional = countryModel.fractional
        UtilityApp.setLocalData(localModel)
        val intent = Intent(activity, ChooseNearCity::class.java)
        intent.putExtra(Constants.COUNTRY_ID, countryModel.id)
        startActivity(intent)
    }

    fun initAdapter() {
        countriesAdapter = CountriesAdapter(activity, this, countries, 0)
        binding.recycler.adapter = countriesAdapter
    }

    override fun onCountryClicked(position: Int, countryModel: CountryModel) {
        sharedPManger?.SetData(Constants.CURRENCY, countryModel.currencyCode)
        sharedPManger?.SetData(Constants.Fractional, countryModel.fractional)
        goToChooseNearCity(countryModel)
    }
}