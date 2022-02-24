package com.ramez.shopp.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.ramez.shopp.Models.FilterListModel
import com.ramez.shopp.R
import com.ramez.shopp.Utils.NumberHandler
import com.ramez.shopp.classes.Constants
import com.ramez.shopp.classes.FilterModel
import com.ramez.shopp.classes.MessageEvent
import com.ramez.shopp.databinding.ActivityFilterBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FilterActivity : ActivityBase() {
    private lateinit var binding: ActivityFilterBinding
    private var openBrandsLauncher: ActivityResultLauncher<Intent>? = null
    var brandsStrList: ArrayList<String>? = null
    var filterList: MutableList<FilterModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.filter)
        filterList = mutableListOf()

        brandsStrList = ArrayList()

        showButton()
        initListener()
    }

    private fun showButton() {
        binding.toolBar.mainTitleTv.visibility = visible
        binding.toolBar.logoImg.visibility = gone
        binding.toolBar.cleanBut.visibility = visible
    }

    private fun initListener() {

        binding.toolBar.backBtn.setOnClickListener { onBackPressed() }

        binding.brandly.setOnClickListener {
            val intent = Intent(activity, BrandsActivity::class.java)
            openBrandsLauncher?.launch(intent)
        }

        openBrandsLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result != null && result.data != null) {

                if (result.resultCode == RESULT_OK) {
                    if (result.data != null) {
                        brandsStrList = result.data?.getStringArrayListExtra(Constants.KEY_BRANDS_LIST)
                        Log.i(javaClass.name, "Log brandsStrList ${brandsStrList?.size} ")
                        initBrandsData()

                    }
                }


            }
        }

        binding.toolBar.cleanBut.setOnClickListener {
            cleanField()
        }

        binding.applyBut.setOnClickListener {
            getFilterList()
            val intent = Intent()
            val filterModel = FilterListModel()
            filterModel.list = filterList
            intent.putExtra(Constants.KEY_FILTER_LIST, filterModel)
            setResult(RESULT_OK, intent)
            finish()


        }
    }


    private fun initBrandsData() {
        binding.brandsCountTv.text = "(".plus(brandsStrList?.size).plus(")")
    }

    fun getFilterList() {

        val minPrice = NumberHandler.arabicToDecimal(binding.minPriceET.text.toString())
        val maxPrice = NumberHandler.arabicToDecimal(binding.maxPriceET.text.toString())

        if (minPrice.isNotEmpty()) {
            val filterModel = FilterModel()
            filterModel.key = "min_price"
            filterModel.value = minPrice
            filterList?.add(filterModel)
        }
        if (maxPrice.isNotEmpty()) {
            val filterModel = FilterModel()
            filterModel.key = "max_price"
            filterModel.value = maxPrice
            filterList?.add(filterModel)

        }
        if (brandsStrList?.size ?: 0 > 0) {
            val filterModel = FilterModel()
            filterModel.key = "brand"
            filterModel.value = brandsStrList?.joinToString { it ->
                it
            }?.replace(" ", "")
            Log.i(javaClass.name, "Log  brandsStrList_str ${filterModel.value} ")
            filterList?.add(filterModel)


        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.type == MessageEvent.TYPE_CLEAN) {
            cleanField()

        }


    }

    private fun cleanField() {
        binding.minPriceET.text = "0" as Editable
        binding.maxPriceET.text = "0" as Editable
        binding.brandsCountTv.text = "0"
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


}