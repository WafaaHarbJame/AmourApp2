package com.amour.shop.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.classes.Constants
import com.amour.shop.classes.Constants.MAIN_ACTIVITY_CLASS
import com.amour.shop.classes.MessageEvent
import com.amour.shop.classes.UtilityApp
import com.amour.shop.Models.*
import com.amour.shop.R
import com.amour.shop.adapter.BookletAdapter
import com.amour.shop.adapter.BookletAdapter.OnBookletClick
import com.amour.shop.adapter.BrandsAdapter

import com.amour.shop.adapter.KitchenAdapter
import com.amour.shop.adapter.KitchenAdapter.OnKitchenClick
import com.amour.shop.databinding.ActivityAllBookleteBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import com.amour.shop.BuildConfig


class AllBookleteActivity : ActivityBase(), OnBookletClick, BrandsAdapter.OnBrandClick, OnKitchenClick {
    lateinit var binding: ActivityAllBookleteBinding
    var list: ArrayList<BookletsModel>? = null
    var gridLayoutManager: GridLayoutManager? = null
    private var adapter: BookletAdapter? = null
    private var cityId = 0
    private var user: MemberModel? = null
    private var localModel: LocalModel? = null
    private var type: String? = ""
    private var brandsAdapter: BrandsAdapter? = null
    var brandsList: MutableList<BrandModel>? = null
    private var dinnerModelList: MutableList<DinnerModel>? = null
    private var lang: String? = null
    private var isNotify = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllBookleteBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)
        lang =
            if (UtilityApp.getLanguage() == null) Locale.getDefault().language else UtilityApp.getLanguage()
        list = ArrayList()
        dinnerModelList = ArrayList()
        brandsList = ArrayList()
        gridLayoutManager = GridLayoutManager(activity, 2)
        binding.recycler.layoutManager = gridLayoutManager
        binding.recycler.setHasFixedSize(true)
        localModel = UtilityApp.getLocalData()
        user = UtilityApp.getUserData()
        binding.recycler.itemAnimator = null
        if (localModel != null) {
            cityId = localModel?.cityId?.toInt()?:Constants.default_storeId.toInt()
        }
        title = ""
        intentExtra
        binding.swipeDataContainer.setOnRefreshListener {
            binding.swipeDataContainer.isRefreshing = false
            if (type == Constants.BOOKLETS) {
                getBooklets(cityId)
            } else if (type == Constants.DINNERS) {
                getDinners(lang)
            } else {
                try {
                    gridLayoutManager?.spanCount = 3
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                getAllBrands(cityId)
            }
        }
        binding.failGetDataLY.refreshBtn.setOnClickListener {
            if (type == Constants.BOOKLETS) {
                getBooklets(cityId)
            } else {
                getAllBrands(cityId)
            }
        }
    }

    override fun onBackPressed() {
        println("Log onBackPressed $isNotify")
        if (isNotify) {
            val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            super.onBackPressed()
        }
    }

    private fun getBooklets(city_id: Int) {
        list?.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result =
                    obj as ResultAPIModel<ArrayList<BookletsModel>?>
                var message: String? = getString(R.string.fail_to_get_data)
                binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                if (func == Constants.ERROR) {
                    if (result.message != null) {
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
                        if (result.data != null && result.data?.size?:0 > 0) {
                            binding.dataLY.visibility = View.VISIBLE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                            list = result.data
                            initAdapter()
                        } else {
                            binding.dataLY.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.VISIBLE
                        }
                    } else {
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                    }
                }            }

        }).getBookletsList(city_id)
    }

    private fun getAllBrands(city_id: Int) {
        brandsList?.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result =
                    obj as ResultAPIModel<ArrayList<BrandModel>?>
                var message: String? = getString(R.string.fail_to_get_data)
                binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                if (func == Constants.ERROR) {
                    if (result.message != null) {
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
                        if (result.data != null && result.data?.size ?:0> 0) {
                            binding.dataLY.visibility = View.VISIBLE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                            val allBrandList = result.data
                            //                            while (allBrandList.size() > 0) {
//
//                            }
                            var i = 0
                            while (i < allBrandList?.size?:0) {
                                val brandModel = result.data!![i]
                                if (brandModel.image != null || brandModel.image2 != null) {
                                    brandsList?.add(brandModel)
                                    allBrandList?.removeAt(i)
                                    i--
                                }
                                i++
                            }
                            initBrandsAdapter()
                        } else {
                            binding.dataLY.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.VISIBLE
                        }
                    } else {
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                    }
                }            }

        }).GetAllBrands(city_id)
    }

    private fun getDinners(lang: String?) {
        dinnerModelList?.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false, object :DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result =
                    obj as ResultAPIModel<ArrayList<DinnerModel>?>
                var message: String? = getString(R.string.fail_to_get_data)
                binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                if (func == Constants.ERROR) {
                    if (result.message != null) {
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
                        if (result.data != null && result.data?.size ?:0> 0) {
                            binding.dataLY.visibility = View.VISIBLE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                            dinnerModelList = result.data
                            initKitchenAdapter()
                        } else {
                            binding.dataLY.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.VISIBLE
                        }
                    } else {
                        binding.dataLY.visibility = View.GONE
                        binding.noDataLY.noDataLY.visibility = View.GONE
                        binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                        binding.failGetDataLY.failTxt.text = message
                    }
                }            }

        }).getDinnersList(lang?:"")
    }

    override fun onBookletClicked(position: Int, bookletsModel: BookletsModel) {

//        EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_BROUSHERS, true));
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        SpecialOfferFragment specialOfferFragment = new SpecialOfferFragment();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(Constants.bookletsModel, bookletsModel);
//        specialOfferFragment.setArguments(bundle);
//        fragmentManager.beginTransaction().replace(R.id.mainContainer, specialOfferFragment, "specialOfferFragment").commit();
        val intent = Intent(activity, MAIN_ACTIVITY_CLASS)
        intent.putExtra(Constants.KEY_OPEN_FRAGMENT, Constants.FRAG_BROSHORE)
        bookletsModel.storeID = cityId
        intent.putExtra(Constants.bookletsModel, bookletsModel)
        intent.putExtra(Constants.Inside_app, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private val intentExtra: Unit
        private get() {
            val bundle = intent.extras
            if (bundle != null) {
                type = bundle.getString(Constants.Activity_type)
                isNotify = bundle.getBoolean(Constants.isNotify, false)
                if (type == Constants.BOOKLETS) {
                    getBooklets(cityId)
                } else if (type == Constants.DINNERS) {
                    getDinners(lang)
                } else {
                    gridLayoutManager?.spanCount = 3
                    getAllBrands(cityId)
                }
            }
        }



    private fun initKitchenAdapter() {
        val kitchenAdapter = KitchenAdapter(activity, dinnerModelList, this, true, 0)
        binding.recycler.adapter = kitchenAdapter
    }

    fun initAdapter() {
        adapter = BookletAdapter(activity, list, list?.size?:0, this)
        binding.recycler.adapter = adapter
    }

    fun initBrandsAdapter() {
        brandsAdapter = BrandsAdapter(activity, brandsList, this, 0)
        binding.recycler.adapter = brandsAdapter
    }

    override fun onKitchenClicked(position: Int, dinnerModel: DinnerModel) {
        val intent = Intent(activity, RamezKitchenActivity::class.java)
        intent.putExtra(Constants.DB_DINNER_MODEL, dinnerModel)
        startActivity(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onBrandClicked(position: Int, brandModel: BrandModel?) {
        val intent = Intent(activity, AllListActivity::class.java)
        intent.putExtra(Constants.LIST_MODEL_NAME, getString(R.string.Brands))
        intent.putExtra(Constants.FILTER_NAME, Constants.brand_filter)
        intent.putExtra(Constants.brand_id, brandModel?.id)
        startActivity(intent)    }
}