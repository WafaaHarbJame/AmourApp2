package com.amour.shop.activities

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.Models.BrandModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.Models.ResultAPIModel
import com.amour.shop.R
import com.amour.shop.adapter.BrandsFilterAdapter
import com.amour.shop.classes.Constants
import com.amour.shop.classes.MessageEvent
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.ActivityBrandsBinding
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
        binding.toolBar.mainTitleTv.visibility = visible
        binding.toolBar.logoImg.visibility = gone

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
        if (UtilityApp.getBrandsData().isNotEmpty()) {

            initBrandsAdapter(UtilityApp.getBrandsData())
        } else {
            getAllBrands(storeId)

        }
    }

    private fun initListener() {

        binding.toolBar.backBtn.setOnClickListener { onBackPressed() }

        binding.applyBut.setOnClickListener {
            val brandsArr = arrayListOf<String>()
            brandsStrList?.let { it1 -> brandsArr.addAll(it1) }
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
                            if (result?.status == 200) {
                                if (result.data != null && result.data?.isNotEmpty()==true) {
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


    fun initBrandsAdapter(allBrandList: MutableList<BrandModel>?) {

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

