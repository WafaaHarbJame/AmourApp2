package com.ramez.shopp.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Models.BrandModel
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.Models.ResultAPIModel
import com.ramez.shopp.R
import com.ramez.shopp.adapter.BrandsFilterAdapter
import com.ramez.shopp.classes.Constants
import com.ramez.shopp.classes.MessageEvent
import com.ramez.shopp.classes.UtilityApp
import com.ramez.shopp.databinding.ActivityBrandsBinding
import org.greenrobot.eventbus.EventBus

class BrandsActivity : ActivityBase(), BrandsFilterAdapter.OnBrandClick {
    private lateinit var binding: ActivityBrandsBinding
    var brandsList: ArrayList<BrandModel>? = null
    var localModel: LocalModel? = null
    var storeId = 0
    var brandsStrList: MutableSet<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrandsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTitle(R.string.filter)
        binding.toolBar.mainTitleTv.visibility=visible
        binding.toolBar.logoImg.visibility=gone

        brandsList = ArrayList()

        val brandManger = LinearLayoutManager(activity)
        binding.brandsRecycler.layoutManager = brandManger

        binding.brandsRecycler.itemAnimator = null
        binding.brandsRecycler.setHasFixedSize(true)

        localModel = UtilityApp.getLocalData()

        if (localModel?.cityId != null) {
            storeId = localModel?.cityId?.toIntOrNull()
                ?: Constants.default_storeId.toInt()
        }

        initData()
        initListener()
    }

    private fun initData() {
        if (UtilityApp.getBrandsData() != null && UtilityApp.getBrandsData().size > 0) {

            initBrandsAdapter(UtilityApp.getBrandsData())
        } else {
            getAllBrands(storeId)

        }
    }

    private fun initListener() {

        binding.toolBar.backBtn.setOnClickListener { onBackPressed() }

        binding.applyBut.setOnClickListener {
            val brandsArr = arrayListOf<String>()
            brandsArr.addAll(brandsStrList!!)

            val intent = Intent()
            intent.putStringArrayListExtra(Constants.KEY_BRANDS_LIST, brandsArr)
            setResult(RESULT_OK, intent)
            finish()

        }

    }

    private fun getAllBrands(storeId: Int) {
        brandsList?.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE

                    var message: String? = getString(R.string.fail_to_get_data)
                    if (func == Constants.ERROR) {
                        val result = obj as ResultAPIModel<ArrayList<BrandModel>?>?

                        if (result != null) {
                            message = result.message
                        }
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                    } else if (func == Constants.FAIL) {
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                    } else if (func == Constants.NO_CONNECTION) {
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection)
                        binding.failGetDataLY.noInternetIv.visibility = View.VISIBLE
                    } else {
                        if (IsSuccess) {
                            val result = obj as ResultAPIModel<ArrayList<BrandModel>?>?
                            if(result?.status==200){
                                if (result.data != null && result.data?.size ?: 0 > 0) {
                                    Log.i(ContentValues.TAG, "Log getBrands" + result.data?.size)
                                    brandsList = result.data
                                    UtilityApp.setBrandsData(brandsList)
                                    initBrandsAdapter(brandsList)
                                } else {
                                    binding.noDataLY.noDataLY.visibility = visible
                                }
                            }

                        }
                    }
                }

            }).GetAllBrands(storeId)
    }


    fun initBrandsAdapter(allBrandList: ArrayList<BrandModel>?) {

        var i = 0
        while (i < allBrandList!!.size) {
            val brandModel = allBrandList[i]
            if (brandModel.image != null || brandModel.image2 != null) {
                brandsList!!.add(brandModel)
                allBrandList.removeAt(i)
                i--
            }
            i++
        }
        if (brandsList?.size == 0) {
            binding.noDataLY.noDataLY.visibility = visible
        }
        val brandsAdapter = BrandsFilterAdapter(activity, brandsList, this, 10)
        binding.brandsRecycler.adapter = brandsAdapter
    }

    override fun onBrandClicked(position: Int, brandsSets: MutableSet<String>?) {
        brandsStrList = brandsSets
    }


}

