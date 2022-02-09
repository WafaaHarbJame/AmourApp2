package com.ramez.shopp.fragments


import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack
import com.kcode.permissionslib.main.PermissionCompat
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.classes.*
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.activities.FullScannerActivity
import com.ramez.shopp.activities.ProductDetailsActivity
import com.ramez.shopp.adapter.MainCategoryAdapter
import com.ramez.shopp.adapter.MainCategoryAdapter.OnMainCategoryItemClicked
import com.ramez.shopp.adapter.ProductCategoryAdapter
import com.ramez.shopp.adapter.SubCategoryAdapter
import com.ramez.shopp.databinding.FragmentCategoryProductsBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


class CategoryProductsFragment : FragmentBase(), ProductCategoryAdapter.OnItemClick,
        OnMainCategoryItemClicked {
    var productList: ArrayList<ProductModel>? = null
    var data: ArrayList<AutoCompleteModel>? = null
    var autoCompleteList: ArrayList<String>? = null
    var mainCategoryDMS: ArrayList<CategoryModel>? = null
    var subCategoryDMS: ArrayList<ChildCat>? = null
    var gridLayoutManager: GridLayoutManager? = null
    var numColumn = 2
    var selectedSubCat = 0
    var categoryId = 0
    var countryId = 0
    var cityId = 0
    private var userId = "0"
    private var catPosition = 0
    private val filter = ""
    private var user: MemberModel? = null
    private var localModel: LocalModel? = null
    private var adapter: ProductCategoryAdapter? = null
    private val searchCode = 2000
    lateinit var binding: FragmentCategoryProductsBinding
    private var scanLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryProductsBinding.inflate(inflater, container, false)
        productList = ArrayList()
        mainCategoryDMS = ArrayList()
        gridLayoutManager = GridLayoutManager(activityy, numColumn)
        binding.productsRv.layoutManager = gridLayoutManager
        binding.listShopCategories.layoutManager =
            LinearLayoutManager(activityy, LinearLayoutManager.HORIZONTAL, false)
        binding.listSubCategory.layoutManager =
            LinearLayoutManager(activityy, LinearLayoutManager.HORIZONTAL, false)
        binding.productsRv.setHasFixedSize(true)
        binding.productsRv.itemAnimator = null
        data = ArrayList()
        autoCompleteList = ArrayList()
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activityy
            )
        countryId = localModel?.countryId ?: Constants.default_country_id
        if (UtilityApp.isLogin() && UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) {
            user = UtilityApp.getUserData()
            userId = user?.id.toString()
        }
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()


        intentExtra
        initListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scanLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {
                val intentBundle = result.data?.extras
                val searchByCode = intentBundle?.getBoolean(Constants.SEARCH_BY_CODE_byCode, false)
                val code = intentBundle?.getString(Constants.CODE)

                if (!EventBus.getDefault().isRegistered(requireActivity()))
                    EventBus.getDefault().register(requireActivity())

                val bundle = Bundle()
                bundle.putString(Constants.CODE, code)
                bundle.putBoolean(Constants.SEARCH_BY_CODE_byCode, searchByCode!!)
                bundle.putInt(
                    Constants.KEY_FRAGMENT_ID,
                    R.id.searchFragment
                )
                EventBus.getDefault()
                    .post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))

//                val searchFragment = SearchFragment()
//                searchFragment.arguments = bundle
//                fragmentManager.beginTransaction()
//                    .replace(R.id.mainContainer, searchFragment, "searchFragment")
//                    .commitAllowingStateLoss()
            }

        }


    }


    private fun initListeners() {
        binding.headerAppBarLY.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                if (state == State.COLLAPSED) {
                    EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_SHOW_SEARCH, true))
                } else {
                    EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_SHOW_SEARCH, false))
                }
            }

            override fun onBarOffsetChanged(appBarLayout: AppBarLayout, offset: Int) {}
        })
        binding.failGetDataLY.refreshBtn.setOnClickListener {
            getProductList(
                selectedSubCat,
                countryId,
                cityId,
                userId,
                filter,
                0,
                10
            )
        }
        binding.searchBut.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.searchFragment)
            EventBus.getDefault()
                .post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
        }
        binding.barcodeBut.setOnClickListener { checkCameraPermission() }
    }

    fun initAdapter() {
        adapter = ProductCategoryAdapter(
            activityy,
            binding.productsRv,
            productList,
            selectedSubCat,
            countryId,
            cityId,
            userId,
            0,
            "",
            this,
            numColumn,
            0
        )
        binding.productsRv.adapter = adapter
    }

    override fun onItemClicked(position: Int, productModel: ProductModel) {
        val intent = Intent(activityy, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.DB_productModel, productModel)
        startActivity(intent)
    }

    private fun cancelAPiCall() {
        if (adapter != null && adapter!!.apiCall != null && adapter!!.apiCall.isExecuted) {
            adapter!!.isCanceled = true
            adapter!!.apiCall.cancel()
        }
    }

    private val intentExtra: Unit
        get() {
            val bundle = arguments
            Log.i(javaClass.simpleName, "Log getIntentExtra ")
            if (bundle != null) {
                mainCategoryDMS = bundle.getSerializable(Constants.CAT_LIST) as ArrayList<CategoryModel>?
                //            boolean subCat = bundle.getBoolean(Constants.SUB_CAT_ID);

                catPosition = bundle.getInt(Constants.position, 0)

                if (mainCategoryDMS == null) {
                    if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size > 0) {
                        mainCategoryDMS = UtilityApp.getCategories()

                    } else {
                        getCategories(localModel!!.cityId.toInt())
                    }
                }

                Log.i(javaClass.simpleName, "Log catPosition $catPosition")

                categoryId = bundle.getInt(Constants.MAIN_CAT_ID, 0)
                selectedSubCat = bundle.getInt(Constants.SUB_CAT_ID, 0)

                Log.i(javaClass.simpleName, "Log mainCategoryDMS " + mainCategoryDMS!!.size)
                Log.i(javaClass.simpleName, "Log category_id $categoryId")
                Log.i(javaClass.simpleName, "Log sub_cat_id $selectedSubCat")
                if (categoryId == 0 && selectedSubCat != 0) {
                    getCatIdFromSub(selectedSubCat)
                } else {
                    initData()
                }
            }
        }

    private fun initData() {
        initMainCategoryAdapter()
        initSubCatList()
        getProductList(selectedSubCat, countryId, cityId, userId, filter, 0, 10)
    }

    private fun initSubCatList() {
        val childCat = ChildCat()
        childCat.id = categoryId
        childCat.hName = getString(R.string.all)
        childCat.name = getString(R.string.all)
        if (selectedSubCat == 0) selectedSubCat = categoryId
        if (subCategoryDMS == null) searchSubCatList()
        subCategoryDMS!!.add(0, childCat)
        initSubCategoryAdapter()
    }

    private fun initSubCategoryAdapter() {
        val subCategoryAdapter = SubCategoryAdapter(
            activityy, subCategoryDMS, selectedSubCat
        ) { `object`: ChildCat ->
            selectedSubCat = `object`.id
            cancelAPiCall()
            getProductList(selectedSubCat, countryId, cityId, userId, "", 0, 10)
        }
        binding.listSubCategory.adapter = subCategoryAdapter
    }

    private fun initMainCategoryAdapter() {
        val mainCategoryShopAdapter = MainCategoryAdapter(activityy, mainCategoryDMS, this, categoryId)
        binding.listShopCategories.adapter = mainCategoryShopAdapter
        getCatPosition()

    }

    private fun getProductList(
        category_id: Int,
        country_id: Int,
        city_id: Int,
        user_id: String?,
        filter: String?,
        page_number: Int,
        page_size: Int
    ) {
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.productsRv.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        val result = obj as FavouriteResultModel?
                        var message: String? = getString(R.string.fail_to_get_data)
                        binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                        if (func == Constants.ERROR) {
                            if (result != null && result.message != null) {
                                message = result.message
                            }
                            binding.productsRv.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                            binding.failGetDataLY.failTxt.text = message
                        } else if (func == Constants.NO_CONNECTION) {
                            binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                            binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection)
                            binding.failGetDataLY.noInternetIv.visibility = View.VISIBLE
                            binding.productsRv.visibility = View.GONE
                        } else {
                            if (IsSuccess) {
                                if (result!!.data != null && result.data.size > 0) {
                                    binding.productsRv.visibility = View.VISIBLE
                                    binding.noDataLY.noDataLY.visibility = View.GONE
                                    binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                    productList = ArrayList(result.data)
                                    initAdapter()
                                } else {
                                    binding.productsRv.visibility = View.GONE
                                    binding.noDataLY.noDataLY.visibility = View.VISIBLE
                                }
                            } else {
                                binding.productsRv.visibility = View.GONE
                                binding.noDataLY.noDataLY.visibility = View.GONE
                                binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                                binding.failGetDataLY.failTxt.text = message
                            }
                        }
                    }
                }

            }).getFavorite(category_id, country_id, city_id, user_id, filter, 0, page_number, page_size)
    }

    override fun OnMainCategoryItemClicked(mainCategoryDM: CategoryModel, position: Int) {
        categoryId = mainCategoryDM.id
        selectedSubCat = categoryId
        subCategoryDMS = ArrayList(mainCategoryDM.childCat)
        initSubCatList()
        cancelAPiCall()
        getProductList(selectedSubCat, countryId, cityId, userId, filter, 0, 10)
    }

    private fun checkCameraPermission() {
        Dexter.withContext(activityy).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                @RequiresApi(api = Build.VERSION_CODES.M)
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    startScan()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        activityy,
                        "" + getString(R.string.permission_camera_rationale),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(
                    activityy,
                    "" + getString(R.string.error_in_data),
                    Toast.LENGTH_SHORT
                ).show()
            }.onSameThread().check()
    }



    private fun startScan() {
        try {
            val builder = PermissionCompat.Builder(activityy)
            builder.addPermissions(arrayOf(Manifest.permission.CAMERA))
            builder.addPermissionRationale(getString(R.string.should_allow_permission))
            builder.addRequestPermissionsCallBack(object : OnRequestPermissionsCallBack {
                override fun onGrant() {
                    val intent = Intent(activityy, FullScannerActivity::class.java)
                    scanLauncher?.launch(intent)

                }

                override fun onDenied(permission: String) {
                    Toast(R.string.some_permission_denied)
                }
            })
            builder.build().request()
        } catch (var2: Exception) {
            var2.printStackTrace()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        if (event.type == MessageEvent.TYPE_view) {
            numColumn = event.data as Int
            initAdapter()
            gridLayoutManager!!.spanCount = numColumn
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }


    fun getCategories(storeId: Int) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as CategoryResultModel
                if (IsSuccess) {
                    if (result.data != null && result.data.size > 0) {
                        mainCategoryDMS = result.data
                        Log.i(ContentValues.TAG, "Log productBestList" + mainCategoryDMS?.size)
                        UtilityApp.setCategoriesData(mainCategoryDMS)
                    }
                }
            }

        }).GetAllCategories(storeId)
    }

    private fun getCatIdFromSub(subId: Int) {
        for (i in mainCategoryDMS!!.indices) {
            if (subId == mainCategoryDMS!![i].id) {
                categoryId = mainCategoryDMS!![i].id
                selectedSubCat = categoryId // this for all cat
                subCategoryDMS = ArrayList(mainCategoryDMS!![i].childCat)
                initData()
                break
            }
            val subArrayList = mainCategoryDMS!![i].childCat
            for (j in subArrayList.indices) {
                if (subId == subArrayList[j].id) {
                    categoryId = mainCategoryDMS!![i].id
                    Log.i(javaClass.simpleName, "Log subCat category_id $categoryId")
                    selectedSubCat = subId
                    subCategoryDMS = ArrayList(mainCategoryDMS!![i].childCat)
                    initData()
                    break
                }
            }
        }
    }

    private fun searchSubCatList() {

        if (mainCategoryDMS != null)
            for (i in mainCategoryDMS!!.indices) {
                if (categoryId == mainCategoryDMS!![i].id) {
                    subCategoryDMS = ArrayList(mainCategoryDMS!![i].childCat)
                    break
                }
            }
        if (subCategoryDMS == null) subCategoryDMS = ArrayList()
    }

    companion object {
        private const val ZBAR_CAMERA_PERMISSION = 1
    }

    private fun getCatPosition() {

        Log.i(javaClass.simpleName, "Log catPosition categoryId$categoryId")

        for ((i, categoryModel) in mainCategoryDMS?.withIndex() ?: mutableListOf()) {
            if (categoryId == categoryModel.id) {
                catPosition = i
                Log.i(javaClass.simpleName, "Log catPosition getCatPosition$catPosition")
                break
            }
        }

        binding.listShopCategories.scrollToPosition(catPosition)


    }


}