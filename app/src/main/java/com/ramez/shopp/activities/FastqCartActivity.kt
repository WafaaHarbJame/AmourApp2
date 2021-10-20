package com.ramez.shopp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaeger.library.StatusBarUtil
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.CityModelResult
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.adapter.FastqCartAdapter
import com.ramez.shopp.databinding.ActivityFastqCartActivityBinding
import java.util.*


class FastqCartActivity : ActivityBase() {

    var localModel: LocalModel? = null
    var currency = "BHD"
    var fraction = 2
    var branchName: String = ""
    private var countryId = 0
    private var cityId = 0
    var cityModelArrayList: ArrayList<CityModel>? = null
    private var selectedCityModel: CityModel? = null
    private var list: MutableList<CartFastQModel>? = null
    private lateinit var binding: ActivityFastqCartActivityBinding
    var userId = 0
    var cartCount = 0
    var total: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFastqCartActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        list = mutableListOf()

        if (UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) {
            userId = UtilityApp.getUserData()?.id ?: 0
        }



        changeToolBarColor()

        binding.rv.layoutManager = LinearLayoutManager(activiy)


        StatusBarUtil.setColor(this, ContextCompat.getColor(activiy, R.color.fastq_color), 0)

        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activiy
            )


        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()

        currency = localModel?.currencyCode ?: Constants.BHD
        fraction = localModel?.fractional ?: Constants.three

        getFastQCarts(cityId, userId)

        if (UtilityApp.getBranchName() != null) {
            setBranchData()

        } else {
            getCityList(countryId)

        }

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
        } else {
            branchName = UtilityApp.getBranchName()
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
        binding.continueBtn.setOnClickListener {

            val intent = Intent(activiy, FastqCheckoutActivity::class.java)
            startActivity(intent)

        }

    }

    fun initAdapter() {

        val adapter = FastqCartAdapter(
            activiy,true, list
        ) { obj, func, IsSuccess ->
            val cartProcessModel = obj as CartProcessModel
            if(cartProcessModel.cartCount>0){
                total = cartProcessModel.total
                binding.totalPriceTv.text =
                    NumberHandler.formatDouble(total, fraction).plus(" $currency")

                val itemsCount = "( ${cartProcessModel.cartCount} ${getString(R.string.items)} )"
                binding.itemsCountTv.text = itemsCount

            }
            else{

                binding.dataLY.visibility = View.GONE
                binding.noDataLY.noDataLY.visibility =
                    View.VISIBLE
                binding.failGetDataLY.failGetDataLY.visibility =
                    View.GONE
            }


        }
        binding.rv.adapter = adapter

    }

    private fun getFastQCarts(storeId: Int, userId: Int) {
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE


        DataFeacher(false, object : DataFetcherCallBack {
            @SuppressLint("SetTextI18n")
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                binding.loadingProgressLY.loadingProgressLY.visibility =
                    View.GONE

                when (func) {
                    Constants.ERROR -> {
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility =
                            View.VISIBLE
                        binding.failGetDataLY.failTxt.text = getString(R.string.fail_to_get_data)
                    }
                    Constants.FAIL -> {
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility =
                            View.VISIBLE
                        binding.failGetDataLY.failTxt.text = getString(R.string.fail_to_get_data)
                    }
                    Constants.NO_CONNECTION -> {
                        binding.failGetDataLY.failGetDataLY.visibility =
                            View.VISIBLE
                        binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection)
                        binding.failGetDataLY.noInternetIv.visibility =
                            View.VISIBLE
                        binding.dataLY.visibility = View.GONE
                    }
                }

                if (IsSuccess) {

                    val cartFastQModel = obj as ResultAPIModel<MutableList<CartFastQModel>?>?

                    if (cartFastQModel?.status == 200 && cartFastQModel.data?.isNotEmpty() == true) {

                        binding.dataLY.visibility = View.VISIBLE
                        binding.noDataLY.noDataLY.visibility =
                            View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility =
                            View.GONE

                        val data = cartFastQModel.data
                        val fastCartNumber = data?.size ?: 0
                        list = data
                        initAdapter()
                        UtilityApp.setFastQCartCount(fastCartNumber)
                        val total = calculateSubTotalPrice(data)

                        binding.totalPriceTv.text =
                            NumberHandler.formatDouble(total, fraction).plus(" $currency")

                        Log.i(javaClass.name, "Log fast Cart count $fastCartNumber")
                        binding.itemsCountTv.text =
                            "(".plus(" " + list?.size + " ").plus(getString(R.string.items).plus(")"))


                    } else {
                        Log.i(javaClass.name, "Log fast Cart count 0")

                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility =
                            View.VISIBLE
                        binding.failGetDataLY.failGetDataLY.visibility =
                            View.GONE
                    }


                }
            }

        }).getFastQCarts(storeId, userId.toString())
    }

    fun calculateSubTotalPrice(cartList: MutableList<CartFastQModel>?): Double {
        var subTotal = 0.0
        for (i in cartList!!.indices) {
            var price = 0.0
            val cartFastQModel = cartList[i]
            if (cartFastQModel.price > 0) {
                price = cartFastQModel.price
                subTotal += price * cartFastQModel.qty
            }

        }
        UtilityApp.setFastQCartTotal(subTotal.toFloat())
        Log.i(javaClass.name, "Log subTotal result$subTotal")

        return subTotal
    }
}