package com.amour.shop.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaeger.library.StatusBarUtil
import com.amour.shop.classes.Constants
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.CartFastQModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.R
import com.amour.shop.Utils.NumberHandler
import com.amour.shop.adapter.FastqCartAdapter

import com.amour.shop.databinding.ActivityFastqSummaryActivityBinding
import com.amour.shop.BuildConfig


class FastqSummaryActivity : ActivityBase() {

    var localModel: LocalModel? = null
    private var countryId = 0
    private var cityId = 0
    private var list: MutableList<CartFastQModel>? = null
    var userId = 0
    var currency = "BHD"
    var fraction = 2
    var total = 0.0

    private lateinit var binding: ActivityFastqSummaryActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFastqSummaryActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeToolBarColor()

        binding.rv.layoutManager = LinearLayoutManager(activity)

        StatusBarUtil.setColor(this, ContextCompat.getColor(activity, R.color.fastq_color), 0)

        localModel = UtilityApp.getLocalData()

        currency = localModel?.currencyCode ?: Constants.BHD
        fraction = localModel?.fractional ?: Constants.three


        list = mutableListOf()

        if (UtilityApp.getUserData() != null && UtilityApp.getUserData()?.id != null) {
            userId = UtilityApp.getUserData()?.id ?: 0
        }

        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        title = getString(R.string.shopping_summary)


        initListeners()

        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()

        if(intent!=null){
            val list = intent?.getSerializableExtra(Constants.CART_MODEL) as Array<CartFastQModel>
            val productsList = mutableListOf<CartFastQModel>()
            productsList.addAll(list)
            initAdapter(productsList)
        }




    }


    private fun calculateSubTotalPrice(cartList: MutableList<CartFastQModel>?): Double {
        var subTotal = 0.0
        for (i in cartList!!.indices) {
            val cartFastQModel = cartList[i]
            if (cartFastQModel.price > 0) {
                val price = cartFastQModel.price
                subTotal += price * cartFastQModel.qty
            }

        }
        UtilityApp.setFastQCartTotal(subTotal.toFloat())
        Log.i(javaClass.name, "Log subTotal result $subTotal")
        return subTotal
    }

    private fun changeToolBarColor() {
        binding.toolBar.toolbarBack.setBackgroundColor(ContextCompat.getColor(activity, R.color.fastq_color))
        binding.toolBar.logoImg.visibility = gone
        binding.toolBar.mainTitleTv.visibility = visible

    }

    private fun initListeners() {

        binding.closeBtn.setOnClickListener {
            UtilityApp.setFastQCartTotal(0.0F)
            UtilityApp.setFastQCartCount(0)
            onBackPressed()

        }

    }

    fun initAdapter(cartList: MutableList<CartFastQModel>?) {

        val adapter = FastqCartAdapter(
            activity,false, cartList
        ) { obj, func, IsSuccess ->

        }
        binding.rv.adapter = adapter

        binding.itemsCountTv.text =
            "(".plus(" " + cartList?.size + " ").plus(getString(R.string.items).plus(")"))

        binding.totalTv.text =
            NumberHandler.formatDouble(calculateSubTotalPrice(cartList), fraction).plus(" $currency")


    }

    override fun onBackPressed() {

        val intent = Intent(activity, FastqActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)

    }


}