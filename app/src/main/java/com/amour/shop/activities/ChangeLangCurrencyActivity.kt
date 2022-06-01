package com.amour.shop.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amour.shop.BuildConfig
import com.amour.shop.classes.Constants
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.CountryModel
import com.amour.shop.Models.CurrencyModel
import com.amour.shop.Models.LanguageModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.R
import com.amour.shop.SplashScreenActivity
import com.amour.shop.adapter.CurrencyAdapter
import com.amour.shop.adapter.CurrencyAdapter.OnCurrencyClick
import com.amour.shop.adapter.LangAdapter
import com.amour.shop.adapter.LangAdapter.OnLangClick
import com.amour.shop.databinding.ActivityChangeLangAndCurrencyBinding
import java.util.ArrayList


class ChangeLangCurrencyActivity : ActivityBase(), OnCurrencyClick, OnLangClick {
    lateinit var binding: ActivityChangeLangAndCurrencyBinding
    var langList: ArrayList<LanguageModel>? = null
    private var langAdapter: LangAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    var currencyList: ArrayList<CurrencyModel>? = null
    private val currencyAdapter: CurrencyAdapter? = null
    private var currencyLinearLayoutManager: LinearLayoutManager? = null
    var countries: ArrayList<CountryModel>? = null
    var selectedLangId = 0
    var selectedCurrency = 0
    var localModel: LocalModel? = null
    private var toggleButton = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeLangAndCurrencyBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        setTitle(R.string.change_city_currency)
        langList = ArrayList()
        currencyList = ArrayList()
        countries = ArrayList()
        localModel = UtilityApp.getLocalData()
        langList?.add(
            LanguageModel(
                1,
                getString(R.string.text_language_arabic),
                getString(R.string.ar_lang)
            )
        )
        langList?.add(
            LanguageModel(
                2,
                getString(R.string.text_langiage_english),
                getString(R.string.en_lang)
            )
        )
        selectedLangId = if (UtilityApp.getLanguage() == Constants.Arabic) {
            1
        } else {
            2
        }
        selectedCurrency = localModel?.countryId ?:Constants.default_country_id
        linearLayoutManager = LinearLayoutManager(activity)
        currencyLinearLayoutManager = LinearLayoutManager(activity)
        binding.recycler.layoutManager = linearLayoutManager
        binding.recycler.layoutManager = currencyLinearLayoutManager
        binding.chooseLangTv.setOnClickListener {
            binding.langContainer.visibility = View.VISIBLE
            binding.chooseLangTv.visibility = View.GONE
        }
        binding.chooseLangTv.setOnClickListener {
            toggleButton = !toggleButton
            if (toggleButton) {
                binding.langContainer.visibility = View.VISIBLE
                binding.langLY.background = ContextCompat.getDrawable(
                    activity,
                    R.drawable.lang_style
                )
            } else {
                binding.langContainer.visibility = View.GONE
                binding.langLY.background = ContextCompat.getDrawable(
                    activity,
                    R.drawable.spinner_style
                )
            }
        }
        binding.saveBut.setOnClickListener { view1 ->
            if (selectedLangId == 2) {
                UtilityApp.setLanguage(Constants.English)
            } else {
                UtilityApp.setLanguage(Constants.Arabic)
            }
            UtilityApp.setAppLanguage()
            Toast(R.string.change_success)
            val intent = Intent(activity, SplashScreenActivity::class.java)
            startActivity(intent)
        }


//        if(UtilityApp.getCountriesData().size()>0)
//        {
//            countries=UtilityApp.getCountriesData();
//            for (int i = 0; i <countries.size() ; i++) {
//                CountryModel countryModel=countries.get(i);
//                currencyList.add(new CurrencyModel(countryModel.getId(),countryModel.getCurrencyCode(),countryModel.getCurrencyCode()));
//            }
//
//        }
//        else {
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
        for (i in countries!!.indices) {
            val countryModel = countries!![i]
            currencyList?.add(
                CurrencyModel(
                    countryModel.id,
                    countryModel.currencyCode,
                    countryModel.currencyCode
                )
            )
        }
        // }
        initAdapter()
        binding.toolBar.backBtn.setOnClickListener { view1 -> onBackPressed() }
    }

    fun initAdapter() {
        langAdapter = LangAdapter(activity, langList, this, selectedLangId)
        binding.recycler.adapter = langAdapter
    }

    override fun onLangClicked(position: Int, languageModel: LanguageModel) {
        selectedLangId = languageModel.id
    }

    override fun onCurrencyClicked(position: Int, currencyModel: CurrencyModel) {
        selectedCurrency = currencyModel.id
    }
}