package com.ramez.shopp.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.android.material.appbar.AppBarLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.single.PermissionListener
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.*
import com.ramez.shopp.Dialogs.WhatsUpDialog
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.ActivityHandler
import com.ramez.shopp.Utils.DateHandler
import com.ramez.shopp.activities.*
import com.ramez.shopp.adapter.*
import com.ramez.shopp.adapter.BannersAdapter.OnBannersClick
import com.ramez.shopp.adapter.BookletAdapter.OnBookletClick
import com.ramez.shopp.adapter.BrandsAdapter.OnBrandClick
import com.ramez.shopp.adapter.KitchenAdapter.OnKitchenClick
import com.ramez.shopp.databinding.FragmentHomeBinding
import es.dmoral.toasty.Toasty
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.greenrobot.eventbus.EventBus
import java.util.*

class HomeFragment : FragmentBase(), ProductAdapter.OnItemClick, CategoryAdapter.OnItemClick,
        OnBookletClick, AutomateSlider.OnSliderClick, OnBrandClick, OnBannersClick,
        MainSliderAdapter.OnSliderClick,
        OnKitchenClick {
    var countryCode = ""
    var productBestList: ArrayList<ProductModel>? = null
    var productSellerList: ArrayList<ProductModel>? = null
    var productOffersList: ArrayList<ProductModel>? = null
    var productRecentsList: ArrayList<ProductModel>? = null
    var bestProductGridLayoutManager: LinearLayoutManager? = null
    var bestSellerLayoutManager: LinearLayoutManager? = null
    var bestOfferGridLayoutManager: LinearLayoutManager? = null
    var recentLayoutManager: LinearLayoutManager? = null
    var userId = "0"
    var categoryModelList: ArrayList<CategoryModel>? = null
    var sliderList: ArrayList<Slider>? = null
    var bannersList: ArrayList<Slider>? = null
    var brandsList: ArrayList<BrandModel>? = null
    var bookletManger: GridLayoutManager? = null
    var brandManger: GridLayoutManager? = null
    var cityModelArrayList: ArrayList<CityModel>? = null
    private val searchCode = 2000
    lateinit var binding: FragmentHomeBinding
    private var productBestAdapter: ProductAdapter? = null
    private var productSellerAdapter: ProductAdapter? = null
    private var productOfferAdapter: ProductAdapter? = null
    private var productRecentAdapter: ProductAdapter? = null
    private val categoryId = 0
    private var countryId = 0
    private var cityId = 0

    private var mScannerView: ZXingScannerView? = null
    private var mFlash = false
    private var mAutoFocus = false
    private var mSelectedIndices: ArrayList<Int>? = null
    private var mCameraId = -1
    private var categoryAdapter: CategoryAdapter? = null
    private var bookletAdapter: BookletAdapter? = null
    private var activity: Activity? = null
    private var bookletsList: ArrayList<BookletsModel>? = null
    private var sliderAdapter: MainSliderAdapter? = null
    private var bannerAdapter: BannersAdapter? = null
    private var brandsAdapter: BrandsAdapter? = null
    private var list: ArrayList<DinnerModel>? = null
    private var lang: String? = null
    private var localModel: LocalModel? = null
    private var selectedCityModel: CityModel? = null
    private var totalPointModel: TotalPointModel? = null
    private var countryDetailsModel: CountryDetailsModel? = null
    private var changeBranchLauncher: ActivityResultLauncher<Intent>? = null
    private var searchLauncher: ActivityResultLauncher<Intent>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view: View = binding.root
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activityy
            )
        productBestList = ArrayList()
        bookletsList = ArrayList()
        sliderList = ArrayList()
        bannersList = ArrayList()
        productRecentsList = ArrayList()
        list = ArrayList()
        categoryModelList = ArrayList()
        productSellerList = ArrayList()
        productOffersList = ArrayList()
        brandsList = ArrayList()
        mScannerView = ZXingScannerView(getActivity())
        lang =
            if (UtilityApp.getLanguage() == null) Locale.getDefault().language else UtilityApp.getLanguage()
        activity = getActivity()
        if (UtilityApp.isLogin()) {
            if (UtilityApp.getUserData() != null) {
                val memberModel = UtilityApp.getUserData()
                userId = memberModel.id.toString()
                totalPoint
            }
        }
        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        getCityList(countryId)
        getDeliveryTimeListNew(cityId)
        checkLoyal(localModel?.shortname ?: Constants.default_short_name)
        bestProductGridLayoutManager = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        bestOfferGridLayoutManager = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        bestSellerLayoutManager = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        recentLayoutManager = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        bookletManger = GridLayoutManager(activityy, 2, RecyclerView.HORIZONTAL, false)
        brandManger = GridLayoutManager(activityy, 2, RecyclerView.HORIZONTAL, false)
        val bannersManger = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        val categoryManger = GridLayoutManager(activityy, 2, RecyclerView.HORIZONTAL, false)
        val kitchenLy = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        binding.kitchenRecycler.layoutManager = kitchenLy
        binding.kitchenRecycler.itemAnimator = null
        binding.kitchenRecycler.setHasFixedSize(true)
        binding.recentlyRecycler.itemAnimator = null
        binding.recentlyRecycler.setHasFixedSize(true)
        binding.recentlyRecycler.layoutManager = recentLayoutManager
        binding.offerRecycler.itemAnimator = null
        binding.bestProductRecycler.itemAnimator = null
        binding.bestSellerRecycler.itemAnimator = null
        binding.BookletRecycler.itemAnimator = null
        binding.catRecycler.itemAnimator = null
        binding.brandsRecycler.itemAnimator = null
        binding.brandsRecycler.setHasFixedSize(true)
        binding.catRecycler.setHasFixedSize(true)
        binding.bannersRv.itemAnimator = null
        binding.bannersRv.setHasFixedSize(true)
        binding.bestSellerRecycler.layoutManager = bestSellerLayoutManager
        binding.bestProductRecycler.layoutManager = bestProductGridLayoutManager
        binding.offerRecycler.layoutManager = bestOfferGridLayoutManager
        binding.BookletRecycler.layoutManager = bookletManger
        binding.catRecycler.layoutManager = categoryManger
        binding.brandsRecycler.layoutManager = brandManger
        binding.bannersRv.layoutManager = bannersManger
        binding.offerRecycler.setHasFixedSize(true)
        binding.bestProductRecycler.setHasFixedSize(true)
        binding.bestSellerRecycler.setHasFixedSize(true)
        binding.BookletRecycler.setHasFixedSize(true)
        binding.brandsRecycler.setHasFixedSize(true)
        getHomePage()
        initListener()
        binding.branchLY.setOnClickListener {

            val intent = Intent(activityy, ChooseNearCity::class.java)
            intent.putExtra(Constants.COUNTRY_ID, localModel?.countryId)
            changeBranchLauncher?.launch(intent)


        }
        binding.totalPointLY.setOnClickListener { view1 ->
            if (UtilityApp.isLogin()) {
                val intent = Intent(activityy, RewardsActivity::class.java)
                startActivity(intent)
            } else {
                Toasty.warning(activityy,R.string.you_not_signin, Toast.LENGTH_SHORT, true).show()
            }
        }
        if (UtilityApp.isLogin()) {
            val isFirstLogin = UtilityApp.isFirstLogin()
            if (isFirstLogin) {
                UtilityApp.setIsFirstLogin(false)
                val whatsUpDialog = WhatsUpDialog(
                    activityy,
                    R.string.Whatsapp_Live_Support,
                    R.string.is_Active,
                    R.string.ok,
                    R.string.cancel,
                    null,
                    null
                )
                whatsUpDialog.show()
            }
        }
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) { /*empty*/
            }

            override fun onPageSelected(position: Int) {
                binding.pager.selection = position
            }

            override fun onPageScrollStateChanged(state: Int) { /*empty*/
            }
        })
        if (savedInstanceState != null) {
            mFlash = savedInstanceState.getBoolean(FLASH_STATE, false)
            mAutoFocus = savedInstanceState.getBoolean(AUTO_FOCUS_STATE, true)
            mSelectedIndices = savedInstanceState.getIntegerArrayList(SELECTED_FORMATS)
            mCameraId = savedInstanceState.getInt(CAMERA_ID, -1)
        } else {
            mFlash = false
            mAutoFocus = true
            mSelectedIndices = null
            mCameraId = -1
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeBranchLauncher = registerForActivityResult(
            StartActivityForResult()
        ) {
            // Handle the returned Uri
            if (getActivity() != null) requireActivity().recreate()
        }


        searchLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result != null && result.data != null) {
                if (result.data != null) {
                    val searchByCode = result.data?.getBooleanExtra(Constants.SEARCH_BY_CODE_byCode, false)
                    val code = result.data?.getStringExtra(Constants.CODE)
                    val fragmentManager = parentFragmentManager
                    val searchFragment = SearchFragment()
                    val bundle = Bundle()
                    bundle.putString(Constants.CODE, code)
                    bundle.putBoolean(Constants.SEARCH_BY_CODE_byCode, searchByCode!!)
                    searchFragment.arguments = bundle
                    fragmentManager.beginTransaction()
                        .replace(R.id.mainContainer, searchFragment, "searchFragment")
                        .commitAllowingStateLoss()
                }
            }
        }


    }

    private fun initListener() {



        binding.searchBut.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.searchFragment)
            EventBus.getDefault()
                .post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
        }
        binding.failGetDataLY.refreshBtn.setOnClickListener { getHomePage() }
        binding.barcodeBut.setOnClickListener { checkCameraPermission() }
        binding.moreBestBut.setOnClickListener {
            val intent = Intent(activityy, AllListActivity::class.java)
            intent.putExtra(
                Constants.LIST_MODEL_NAME,
                requireActivity().getString(R.string.best_products)
            )
            intent.putExtra(
                Constants.FILTER_NAME,
                Constants.featured_filter
            )
            startActivity(intent)
        }
        binding.moreRecentBut.setOnClickListener {
            val intent = Intent(activityy, AllListActivity::class.java)
            intent.putExtra(
                Constants.LIST_MODEL_NAME,
                requireActivity().getString(R.string.best_products)
            )
            intent.putExtra(
                Constants.FILTER_NAME,
                Constants.new_filter
            )
            startActivity(intent)
        }
        binding.moreCategoryBut.setOnClickListener { view ->
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryButton)
            EventBus.getDefault()
                .post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
        }
        binding.moreBoughtBut.setOnClickListener {
            val intent = Intent(activityy, AllListActivity::class.java)
            intent.putExtra(
                Constants.LIST_MODEL_NAME,
                requireActivity().getString(R.string.best_sell)
            )
            intent.putExtra(
                Constants.FILTER_NAME,
                Constants.quick_filter
            )
            startActivity(intent)
        }
        binding.moreOfferBut.setOnClickListener { view1 ->
            val intent = Intent(activityy, AllListActivity::class.java)
            intent.putExtra(
                Constants.LIST_MODEL_NAME,
                requireActivity().getString(R.string.offers)
            )
            intent.putExtra(
                Constants.FILTER_NAME,
                Constants.offered_filter
            )
            startActivity(intent)
        }
        binding.moreBooklett.setOnClickListener {
            val intent = Intent(activityy, AllBookleteActivity::class.java)
            intent.putExtra(
                Constants.Activity_type,
                Constants.BOOKLETS
            )
            startActivity(intent)
        }
        binding.moreKitchenBut.setOnClickListener { view ->
            val intent = Intent(activityy, AllBookleteActivity::class.java)
            intent.putExtra(
                Constants.Activity_type,
                Constants.DINNERS
            )
            startActivity(intent)
        }
        binding.moreBrandBut.setOnClickListener {
            val intent = Intent(activityy, AllBookleteActivity::class.java)
            intent.putExtra(
                Constants.Activity_type,
                Constants.BRANDS
            )
            startActivity(intent)
        }
    }

    fun initAdapter() {
        productBestAdapter = ProductAdapter(activityy, productBestList, this, 10)
        productSellerAdapter = ProductAdapter(activityy, productSellerList, this, 10)
        productOfferAdapter = ProductAdapter(activityy, productOffersList, this, 10)
        binding.bestProductRecycler.adapter = productBestAdapter
        binding.bestSellerRecycler.adapter = productSellerAdapter
        binding.offerRecycler.adapter = productOfferAdapter
    }

    fun initRecentAdapter() {
        productRecentAdapter = ProductAdapter(activityy, productRecentsList, this, 10)
        binding.recentlyRecycler.adapter = productRecentAdapter
    }

    override fun onItemClicked(position: Int, productModel: ProductModel) {
        val intent = Intent(activityy, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.DB_productModel, productModel)
        startActivity(intent)
    }

    private fun getHomePage() {
        Log.i(ContentValues.TAG, "Log getSliders ")
        Log.i(ContentValues.TAG, "Log getSliders country_id $countryId")
        Log.i(ContentValues.TAG, "Log getSliders  city_id $cityId")
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        binding.searchLY.visibility = View.GONE
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    val result = obj as MainModel?
                    var message: String? = getString(R.string.fail_to_get_data)
                    binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
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
//                        binding.searchLY.setVisibility(View.VISIBLE);
                            if (UtilityApp.getDinners() != null && UtilityApp.getDinners().size > 0) {
                                list = UtilityApp.getDinners()
                                initKitchenAdapter()
                            } else {
                                getDinners(lang)
                            }
                            if (UtilityApp.getSliders() != null && UtilityApp.getSliders().size > 0 && UtilityApp.getBanners() != null && UtilityApp.getBanners().size > 0
                            ) {
                                sliderList = UtilityApp.getSliders()
                                bannersList = UtilityApp.getBanners()
                            } else {
                                UtilityApp.setSliderData(null)
                                UtilityApp.setBannerData(null)
                                if (result!!.sliders.size > 0) {
                                    for (i in result.sliders.indices) {
                                        val slider =
                                            result.sliders[i]
                                        if (slider.type == 0) {
                                            sliderList!!.add(slider)
                                        } else {
                                            bannersList!!.add(slider)
                                        }
                                    }
                                    if (sliderList!!.size > 0) {
                                        UtilityApp.setSliderData(sliderList)
                                    }
                                    if (bannersList!!.size > 0) {
                                        UtilityApp.setBannerData(bannersList)
                                    }
                                }
                            }
                            initSliderAdapter()
                            initBannersAdapter()
                            getBooklets(cityId)
                            getAllBrands(cityId)
                            getProductList(
                                categoryId,
                                countryId,
                                cityId,
                                userId,
                                Constants.new_filter,
                                0,
                                0,
                                10
                            )
                            if (result!!.featured != null && result.featured.size > 0 || result.quickProducts != null && result.quickProducts.size > 0 || result.offeredProducts != null && result.offeredProducts.size > 0
                            ) {
                                binding.dataLY.visibility = View.VISIBLE
                                //                            binding.searchLY.setVisibility(View.VISIBLE);
                                binding.noDataLY.noDataLY.visibility = View.GONE
                                binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                productBestList = result.featured
                                productSellerList = result.quickProducts
                                productOffersList = result.offeredProducts
                                if (productOffersList?.size == 0) {
                                    binding.offerLy.visibility = View.GONE
                                }
                                if (productSellerList?.size == 0) {
                                    binding.bestSellerLy.visibility = View.GONE
                                }
                                if (productBestList?.size == 0) {
                                    binding.bestProductLy.visibility = View.GONE
                                }
                                Log.i(
                                    ContentValues.TAG,
                                    "Log productBestList" + productOffersList?.size
                                )
                                Log.i(
                                    ContentValues.TAG,
                                    "Log productSellerList" + productSellerList?.size
                                )
                                Log.i(
                                    ContentValues.TAG,
                                    "Log productOffersList" + productOffersList?.size
                                )
                                if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size > 0) {
                                    categoryModelList = UtilityApp.getCategories()
                                    initCatAdapter()
                                } else {
                                    getCategories(cityId)
                                }
                                initAdapter()
                            } else {
                                if (productOffersList!!.size == 0) {
                                    binding.offerLy.visibility = View.GONE
                                }
                                if (productSellerList!!.size == 0) {
                                    binding.bestSellerLy.visibility = View.GONE
                                }
                                if (productBestList!!.size == 0) {
                                    binding.bestProductLy.visibility = View.GONE
                                }
                                binding.dataLY.visibility = View.VISIBLE
                                binding.noDataLY.noDataLY.visibility = View.GONE
                            }
                        } else {
                            binding.dataLY.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                            binding.failGetDataLY.failTxt.text = message
                        }
                    }
                }
            }

        }).GetMainPage(0, countryId, cityId, userId)
    }

    private fun checkCameraPermission() {
        Dexter.withContext(getActivity()).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                @RequiresApi(api = Build.VERSION_CODES.M)
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    startScan()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        activityy,
                        "" + requireActivity().getString(R.string.permission_camera_rationale),
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
                    "" + requireActivity().getString(R.string.error_in_data),
                    Toast.LENGTH_SHORT
                ).show()
            }.onSameThread().check()
    }

    private fun startScan() {
        if (ContextCompat.checkSelfPermission(
                    activityy,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activityy,
                arrayOf(Manifest.permission.CAMERA),
                ZBAR_CAMERA_PERMISSION
            )
        } else {
            val intent = Intent(activityy, FullScannerActivity::class.java)
            searchLauncher!!.launch(intent)
        }
    }

    private fun getDeliveryTimeListNew(storeId: Int) {
        var countryCode = ""
        countryCode = if (localModel?.shortname != null)
            localModel?.shortname!! else GlobalData.COUNTRY
        val url = GlobalData.BaseURL + countryCode + "/GroceryStoreApi/api/v8/Orders/nextDeliveryTime?"
        Log.d(ContentValues.TAG, "Log Get first $url")
        Log.d(ContentValues.TAG, "Log  store_id $storeId")
        val token = if (UtilityApp.getToken() != null) UtilityApp.getToken() else "token"
        AndroidNetworking.get(url).setTag("test").setPriority(Priority.HIGH)
            .addHeaders("ApiKey", Constants.api_key)
            .addHeaders("device_type", Constants.deviceType)
            .addHeaders("app_version", UtilityApp.getAppVersionStr())
            .addHeaders("token", token).addQueryParameter("store_id", storeId.toString()).build()
            .getAsObject(
                DeliveryResultModel::class.java,
                object : ParsedRequestListener<DeliveryResultModel?> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(result: DeliveryResultModel?) {
                        if (isVisible) {
                            if (result != null && result.data != null) {
                                val firstTime = result.data
                                val deliveryDay = DateHandler.FormatDate4(
                                    firstTime.date,
                                    "yyyy-MM-dd",
                                    "EEE",
                                    UtilityApp.getLanguage()
                                )
                                if (UtilityApp.getLanguage() == Constants.Arabic) binding.deliveryTimeTv.text =
                                    firstTime.time + " " + deliveryDay else binding.deliveryTimeTv.text =
                                    deliveryDay + " " + firstTime.time
                            }
                        }
                    }

                    override fun onError(anError: ANError) {
                        GlobalData.Toast(activityy,anError.message)
                    }
                })
    }

    private val totalPoint: Unit
        get() {
            totalPointModel = DBFunction.getTotalPoints()
            if (totalPointModel == null) {
                callGetTotalPoints()
            } else {
                callGetTotalPoints()
                setTotalPointsData()
            }
        }

    private fun checkLoyal(shortName: String) {
        countryDetailsModel = DBFunction.getLoyal()
        if (countryDetailsModel == null) {
            getCountryDetail(shortName)
        } else {
            val hasLoyal = countryDetailsModel!!.hasLoyal
            showLoyalLy(hasLoyal)
        }
    }

    private fun showLoyalLy(hasLoyal: Boolean) {
        if (hasLoyal) {
            binding.totalPointLY.visibility = View.VISIBLE
        } else {
            binding.totalPointLY.visibility = View.GONE
        }
    }

    private fun getCountryDetail(shortName: String) {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {

                val result = obj as ResultAPIModel<CountryDetailsModel?>?
                if (result != null && result.isSuccessful) {
                    if (result.data != null) {
                        val countryDetailsModel = result.data
                        Log.i(
                            javaClass.simpleName,
                            "Log  getCountryDetail call hasLoyal " + countryDetailsModel!!.hasLoyal
                        )
                        DBFunction.setLoyal(countryDetailsModel)
                        showLoyalLy(countryDetailsModel.hasLoyal)
                    }
                }
            }


        }).getCountryDetail(shortName)
    }

    private fun callGetTotalPoints() {
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result = obj as ResultAPIModel<TotalPointModel?>?
                if (result != null && result.isSuccessful && result.data != null) {
                    totalPointModel = result.data
                    DBFunction.setTotalPoints(totalPointModel)
                    setTotalPointsData()
                }
            }

        }).getTotalPoint(userId.toInt())
    }

    private fun setTotalPointsData() {
        binding.totalPointTv.text = totalPointModel!!.points.toString()
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

            }).CityHandle(countryId, getActivity())
    }

    private fun getCityList(country_id: Int) {
        Log.i("TAG", "Log country_id$country_id")
        callCityListApi()
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
            binding.branchNameTv.text = selectedCityModel!!.cityName
            UtilityApp.setBranchName(selectedCityModel!!.cityName)
        }
    }

    fun getCategories(storeId: Int) {
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        val result = obj as CategoryResultModel?
                        var message: String? = getString(R.string.fail_to_get_data)
                        binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
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
                                if (result != null && result.data != null && result.data.size > 0) {
                                    binding.dataLY.visibility = View.VISIBLE
                                    binding.noDataLY.noDataLY.visibility = View.GONE
                                    binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                    categoryModelList = result.data
                                    Log.i(
                                        ContentValues.TAG,
                                        "Log productBestList" + categoryModelList?.size
                                    )
                                    UtilityApp.setCategoriesData(categoryModelList)
                                    initCatAdapter()
                                } else {
                                    binding.dataLY.visibility = View.VISIBLE
                                    binding.categoryLy.visibility = View.GONE
                                }
                            } else {
                                binding.dataLY.visibility = View.GONE
                                binding.noDataLY.noDataLY.visibility = View.GONE
                                binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                                binding.failGetDataLY.failTxt.text = message
                            }
                        }
                    }
                }

            }).GetAllCategories(storeId)
    }

    fun getAllBrands(storeId: Int) {
        brandsList!!.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        val result =
                            obj as ResultAPIModel<ArrayList<BrandModel>?>?
                        var message: String? = getString(R.string.fail_to_get_data)
                        binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                        if (func == Constants.ERROR) {
                            if (result != null) {
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
                                if (result!!.data != null && result.data!!.size > 0) {
                                    Log.i(ContentValues.TAG, "Log getBrands" + result.data!!.size)
                                    val allBrandList = result.data

                                    var i = 0
                                    while (i < allBrandList!!.size) {
                                        val brandModel = result.data!![i]
                                        if (brandModel.image != null || brandModel.image2 != null) {
                                            brandsList!!.add(brandModel)
                                            allBrandList.removeAt(i)
                                            i--
                                        }
                                        i++
                                    }
                                    if (brandsList!!.size == 0) {
                                        binding.brandLy.visibility = View.GONE
                                    }
                                    initBrandsAdapter()
                                } else {
                                    binding.brandLy.visibility = View.GONE
                                }
                            } else {
                                binding.brandLy.visibility = View.GONE
                            }
                        }
                    }
                }
            }).GetAllBrands(storeId)
    }

    private fun initCatAdapter() {
        categoryAdapter = CategoryAdapter(activityy, categoryModelList, 10, this, false)
        binding.catRecycler.adapter = categoryAdapter
    }

    private fun initBookletAdapter() {
        if (bookletsList!!.size >= 3) {
            bookletManger!!.orientation = RecyclerView.HORIZONTAL
        } else {
            bookletManger!!.orientation = RecyclerView.VERTICAL
        }
        bookletAdapter = BookletAdapter(activityy, bookletsList, 4, this)
        binding.BookletRecycler.adapter = bookletAdapter
    }

    private fun initKitchenAdapter() {
        val kitchenAdapter = KitchenAdapter(activityy, list, this, false, 10)
        binding.kitchenRecycler.adapter = kitchenAdapter
    }

    fun getBooklets(storeId: Int) {
        bookletsList?.clear()
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result =
                    obj as ResultAPIModel<ArrayList<BookletsModel>?>
                if (IsSuccess) {
                    if (result.data != null && result.data!!.size > 0) {
                        Log.i(ContentValues.TAG, "Log getBooklets" + result.data!!.size)
                        binding.BookletRecycler.visibility = View.VISIBLE
                        bookletsList = result.data
                        initBookletAdapter()
                    } else {
                        binding.bookletLyLy.visibility = View.GONE
                        binding.speLy.visibility = View.GONE
                    }
                } else {
                    binding.bookletLyLy.visibility = View.GONE
                    binding.speLy.visibility = View.GONE
                }
            }

        }).getBookletsList(storeId)
    }

    fun getDinners(lang: String?) {
        list?.clear()
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                val result =
                    obj as ResultAPIModel<ArrayList<DinnerModel>?>
                if (IsSuccess) {
                    if (result.data != null && result.data!!.size > 0) {
                        Log.i(ContentValues.TAG, "Log getDinners size " + result.data?.size)
                        binding.kitchenRecycler.visibility = View.VISIBLE
                        list = result.data
                        UtilityApp.setDinnersData(list)
                        initKitchenAdapter()
                    } else {
                        binding.ramezKitchenLy.visibility = View.GONE
                        binding.dataLY.visibility = View.VISIBLE
                    }
                } else {
                    binding.ramezKitchenLy.visibility = View.GONE
                }
            }

        }).getDinnersList(lang)
    }

    override fun onBookletClicked(position: Int, bookletsModel: BookletsModel) {
        val bundle = Bundle()
        bookletsModel.storeID = cityId
        bundle.putSerializable(Constants.bookletsModel, bookletsModel)
        bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.specialOffersFragment)
        EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
    }

    fun initSliderAdapter() {
        sliderAdapter = MainSliderAdapter(activityy, sliderList, this)
        binding.viewPager.adapter = sliderAdapter
    }

    fun initBannersAdapter() {
        bannerAdapter = BannersAdapter(activityy, bannersList, this)
        binding.bannersRv.adapter = bannerAdapter
    }

    fun initBrandsAdapter() {
        brandsAdapter = BrandsAdapter(activityy, brandsList, this, 10)
        binding.brandsRecycler.adapter = brandsAdapter
    }

    override fun onBrandClicked(position: Int, brandModel: BrandModel) {
        val intent = Intent(activityy, AllListActivity::class.java)
        intent.putExtra(Constants.LIST_MODEL_NAME, requireActivity().getString(R.string.Brands))
        intent.putExtra(Constants.FILTER_NAME, Constants.brand_filter)
        intent.putExtra(Constants.brand_id, brandModel.id)
        startActivity(intent)
    }

    override fun onItemClicked(position: Int, categoryModel: CategoryModel) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.CAT_LIST, categoryModelList)
        bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
        bundle.putInt(Constants.MAIN_CAT_ID, categoryModel.id);
        bundle.putInt(Constants.position, position)
        EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
    }

    override fun onBannersClicked(position: Int, slider: Slider) {
        when (slider.reffrenceType) {
            1 -> {
                val intent = Intent(activityy, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.product_id, slider.reffrence)
                intent.putExtra(Constants.FROM_BROSHER, true)
                startActivity(intent)
            }
            2 -> {
                val bundle = Bundle()
                bundle.putSerializable(Constants.CAT_LIST, categoryModelList)
                bundle.putInt(Constants.SUB_CAT_ID, slider.reffrence.toInt())
                bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
            }
            3 -> {
                val url = slider.reffrence
                ActivityHandler.OpenBrowser(activityy, url)
            }
            5 -> {
                val bundle = Bundle()
                val bookletsModel = BookletsModel()
                bookletsModel.id = slider.reffrence.toInt()
                bookletsModel.storeID = cityId
                bundle.putSerializable(Constants.bookletsModel, bookletsModel)
                bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.specialOffersFragment)
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
            }
            6 -> {
                val bundle = Bundle()
                bundle.putSerializable(Constants.CAT_LIST, categoryModelList)
                bundle.putInt(Constants.SUB_CAT_ID, slider.reffrence.toInt())
                bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
            }
        }
    }

    override fun onSliderClicked(position: Int, slider: Slider) {
        Log.i("tag", "Log Reffrence" + slider.reffrence)
        Log.i("tag", "Log ReffrenceType" + slider.reffrenceType)
        when (slider.reffrenceType) {
            1 -> {
                val intent = Intent(activityy, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.product_id, slider.reffrence)
                intent.putExtra(Constants.FROM_BROSHER, true)
                startActivity(intent)
            }
            2 -> {

                val bundle = Bundle()
                bundle.putSerializable(Constants.CAT_LIST, categoryModelList)
                val subCatId = if (slider.reffrence != null) slider.reffrence else "0"
                bundle.putInt(Constants.SUB_CAT_ID, subCatId.toInt())
                bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
            }
            3 -> {
                Log.i("tag", "Log getReffrence" + slider.reffrence)
                val url = slider.reffrence
                ActivityHandler.OpenBrowser(activityy, url)
            }
            5 -> {
                val bundle = Bundle()
                val bookletsModel = BookletsModel()
                bookletsModel.id = slider.reffrence.toInt()
                bundle.putSerializable(Constants.bookletsModel, bookletsModel)
                bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.specialOffersFragment)
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
            }
            6 -> {
                val bundle = Bundle()
                bundle.putSerializable(Constants.CAT_LIST, categoryModelList)
                bundle.putInt(Constants.SUB_CAT_ID, slider.reffrence.toInt())
                bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
            }
        }
    }


    override fun onKitchenClicked(position: Int, dinnerModel: DinnerModel) {
        val intent = Intent(activityy, RamezKitchenActivity::class.java)
        intent.putExtra(Constants.DB_DINNER_MODEL, dinnerModel)
        startActivity(intent)
    }

    fun getProductList(
        category_id: Int,
        country_id: Int,
        city_id: Int,
        user_id: String?,
        filter: String?,
        brand_id: Int,
        page_number: Int,
        page_size: Int
    ) {
        productRecentsList?.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
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
                                if (result!!.data != null && result.data.size > 0) {
                                    binding.recentlyRecycler.visibility = View.VISIBLE
                                    binding.dataLY.visibility = View.VISIBLE
                                    binding.noDataLY.noDataLY.visibility = View.GONE
                                    binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                    productRecentsList = result.data
                                    Log.i(
                                        ContentValues.TAG,
                                        "Log productList new " + productRecentsList?.size
                                    )
                                    initRecentAdapter()
                                } else {
                                    binding.recentlyRecycler.visibility = View.GONE
                                    binding.recentlyLy.visibility = View.GONE
                                }
                            } else {
                                binding.recentlyRecycler.visibility = View.GONE
                                binding.recentlyLy.visibility = View.GONE
                            }
                        }
                    }
                }

            }).getFavorite(
            category_id,
            country_id,
            city_id,
            user_id,
            filter,
            brand_id,
            page_number,
            page_size
        )
    }

    companion object {
        private const val FLASH_STATE = "FLASH_STATE"
        private const val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
        private const val SELECTED_FORMATS = "SELECTED_FORMATS"
        private const val CAMERA_ID = "CAMERA_ID"
        private const val ZBAR_CAMERA_PERMISSION = 1
    }
}