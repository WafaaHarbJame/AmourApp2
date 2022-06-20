package com.amour.shop.fragments


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
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.single.PermissionListener
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.BuildConfig
import com.amour.shop.Dialogs.WhatsUpDialog
import com.amour.shop.Models.*
import com.amour.shop.Models.request.ProductRequest
import com.amour.shop.R
import com.amour.shop.Utils.ActivityHandler
import com.amour.shop.Utils.DateHandler
import com.amour.shop.activities.*
import com.amour.shop.adapter.*
import com.amour.shop.adapter.BannersAdapter.OnBannersClick
import com.amour.shop.adapter.BookletAdapter.OnBookletClick
import com.amour.shop.adapter.BrandsAdapter.OnBrandClick
import com.amour.shop.adapter.KitchenAdapter.OnKitchenClick
import com.amour.shop.classes.*
import com.amour.shop.databinding.FragmentHomeBinding
import es.dmoral.toasty.Toasty
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : FragmentBase(), ProductAdapter.OnItemClick, CategoryAdapter.OnItemClick,
        OnBookletClick, AutomateSlider.OnSliderClick, OnBrandClick, OnBannersClick, KindAdapter.OnKindClick,
        MainSliderAdapter.OnSliderClick,
        OnKitchenClick {
    var countryCode = ""
    var productBestList: MutableList<ProductModel?>? = null
    var productSellerList: MutableList<ProductModel?>? = null
    var productOffersList: MutableList<ProductModel?>? = null
    var productRecentsList: MutableList<ProductModel?>? = null
    var bestProductGridLayoutManager: LinearLayoutManager? = null
    var bestSellerLayoutManager: LinearLayoutManager? = null
    var bestOfferGridLayoutManager: LinearLayoutManager? = null
    var recentLayoutManager: LinearLayoutManager? = null
    var userId = "0"
    var categoryModelList: ArrayList<CategoryModel?>? = null
    var kindModelList: MutableList<KindCategoryModel>? = null
    var sliderList: MutableList<Slider?>? = null
    var bannersList: MutableList<Slider?>? = null
    var brandsList: MutableList<BrandModel>? = null
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
    private var kindId = 0
    private var sortType: String = ""
    private var mScannerView: ZXingScannerView? = null
    private var mFlash = false
    private var mAutoFocus = false
    private var mSelectedIndices: ArrayList<Int>? = null
    private var mCameraId = -1
    private var categoryAdapter: CategoryAdapter? = null
    private var kindAdapter: KindAdapter? = null
    private var bookletAdapter: BookletAdapter? = null
    private var activity: Activity? = null
    private var bookletsList: ArrayList<BookletsModel>? = null
    private var sliderAdapter: MainSliderAdapter? = null
    private var bannerAdapter: BannersAdapter? = null
    private var brandsAdapter: BrandsAdapter? = null
    private var dinnerList: MutableList<DinnerModel?>? = null
    private var lang: String? = null
    private var localModel: LocalModel? = null
    private var selectedCityModel: CityModel? = null
    private var totalPointModel: TotalPointModel? = null
    private var countryDetailsModel: CountryDetailsModel? = null
    private var changeBranchLauncher: ActivityResultLauncher<Intent>? = null
    private var searchLauncher: ActivityResultLauncher<Intent>? = null
    var accessToken: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view: View = binding.root

        println("Log HomeFragment onCreateView")
        localModel = UtilityApp.getLocalData()
        productBestList = ArrayList()
        bookletsList = ArrayList()
        kindModelList = ArrayList()
        sliderList = ArrayList()
        bannersList = ArrayList()
        productRecentsList = ArrayList()
        dinnerList = ArrayList()
        categoryModelList = ArrayList()
        productSellerList = ArrayList()
        productOffersList = ArrayList()
        brandsList = ArrayList()
        mScannerView = ZXingScannerView(getActivity())

        lang =
            if (UtilityApp.getLanguage() == null) Locale.getDefault().language else UtilityApp.getLanguage()
        accessToken = UtilityApp.getUserToken()

        activity = getActivity()
        if (UtilityApp.isLogin()) {
            val memberModel = UtilityApp.getUserData()
            userId = memberModel?.id.toString()
            totalPoint
        }
        countryId = localModel?.countryId ?: Constants.default_country_id
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        getCityList(countryId)
        getDeliveryTimeListNew(cityId)

//        if (BuildConfig.FLAVOR == Constants.FLAVOUR_RAMEZ) {
//            getDeliveryTimeListNew(cityId)
//
//        } else {
//            binding.deliverLy.visibility = gone
//        }

        checkLoyal(localModel?.shortname ?: Constants.default_short_name)

        bestProductGridLayoutManager = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        bestOfferGridLayoutManager = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        bestSellerLayoutManager = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        recentLayoutManager = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        bookletManger = GridLayoutManager(activityy, 2, RecyclerView.HORIZONTAL, false)
        brandManger = GridLayoutManager(activityy, 2, RecyclerView.HORIZONTAL, false)

        val bannersManger = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)
        val kindManger = LinearLayoutManager(activityy, RecyclerView.VERTICAL, false)
        val categoryManger = GridLayoutManager(activityy, 2, RecyclerView.HORIZONTAL, false)
        val kitchenLy = LinearLayoutManager(activityy, RecyclerView.HORIZONTAL, false)

        binding.kitchenRecycler.layoutManager = kitchenLy
        binding.kitchenRecycler.itemAnimator = null
        binding.kitchenRecycler.setHasFixedSize(true)
        binding.recentlyRecycler.itemAnimator = null
        binding.recentlyRecycler.setHasFixedSize(true)
        binding.recentlyRecycler.layoutManager = recentLayoutManager
        binding.kindRecycler.layoutManager = kindManger
        binding.offerRecycler.itemAnimator = null
        binding.bestProductRecycler.itemAnimator = null
        binding.bestSellerRecycler.itemAnimator = null
        binding.BookletRecycler.itemAnimator = null
        binding.catRecycler.itemAnimator = null
        binding.brandsRecycler.itemAnimator = null
        binding.brandsRecycler.setHasFixedSize(true)
        binding.catRecycler.setHasFixedSize(true)
        binding.kindRecycler.setHasFixedSize(true)
        binding.bannersRv.itemAnimator = null
        binding.bannersRv.setHasFixedSize(true)
//        binding.kindRecycler.isNestedScrollingEnabled = true

        binding.kindRecycler.isNestedScrollingEnabled = false
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

        getAllHomeApi()

        initListener()


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
            if (it.resultCode == Activity.RESULT_OK)
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
                Toasty.warning(activityy, R.string.you_not_signin, Toast.LENGTH_SHORT, true).show()
            }
        }

//        binding.test.setOnClickListener {
//            throw RuntimeException("Test Crash") // Force a crash
//        }
        binding.searchBut.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.searchFragment)
            EventBus.getDefault()
                .post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
        }
//        binding.failGetDataLY.refreshBtn.setOnClickListener { getAllHomeApi() }
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


    private fun getHomeSlider() {
        Log.i(ContentValues.TAG, "Log getSliders country_id $countryId city_id $cityId")

        sliderList = UtilityApp.getSliders()
        bannersList = UtilityApp.getBanners()
        Log.i(ContentValues.TAG, "Log getSliders sliderList ${sliderList?.size}")

        if (sliderList.isNullOrEmpty()) {
            getSlider()

        } else {
            initSliderBannerAdapter()

        }

    }

    private fun getSlider() {
        Log.i(ContentValues.TAG, "Log getSliders")

        binding.loadingLYSlider.visibility = View.VISIBLE
        binding.searchLY.visibility = View.GONE
        binding.sliderLy.visibility = View.GONE

        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    val message: String = getString(R.string.fail_to_get_data)
                    binding.loadingLYSlider.visibility = View.GONE
                    binding.sliderLy.visibility = View.VISIBLE

                    if (func == Constants.ERROR || func == Constants.FAIL) {
                        binding.failSliderTxt.visibility = visible
                        binding.failSliderTxt.text = message
                    } else if (func == Constants.NO_CONNECTION) {
                        binding.failSliderTxt.visibility = visible
                        binding.failSliderTxt.setText(R.string.no_internet_connection)

                    } else {
                        if (IsSuccess) {
                            val result = obj as ResultAPIModel<ArrayList<Slider>?>?
                            if (result?.status == 200) {
                                println("Log initSliderData")
                                sliderList = mutableListOf()
                                bannersList = mutableListOf()
                                result.data?.forEach { slider ->
                                    if (slider.type == 0) {
                                        sliderList?.add(slider)
                                    } else if (slider.type == 1) {
                                        bannersList?.add(slider)
                                    }
                                }
                                UtilityApp.setSliderData(sliderList)
                                UtilityApp.setBannerData(bannersList)
                                initSliderBannerAdapter()
                            }

                        } else {
                            binding.failSliderTxt.text = message
                        }
                    }
                }
            }

        }).getSliders(cityId)
    }

    private fun initSliderBannerAdapter() {
        if (sliderList?.isNotEmpty() == true)
            initSliderAdapter()
        if (bannersList?.isNotEmpty() == true)
            initBannersAdapter()
    }

    private fun getHomeProducts() {
        Log.i(ContentValues.TAG, "Log getSliders ")
        Log.i(ContentValues.TAG, "Log getSliders country_id $countryId")
        Log.i(ContentValues.TAG, "Log getSliders  city_id $cityId")

        sliderList = UtilityApp.getSliders()
        bannersList = UtilityApp.getBanners()

        binding.loadingLYBestProduct.visibility = View.VISIBLE
        binding.loadingLYBestSeller.visibility = View.VISIBLE
        binding.loadingLyOffer.visibility = View.VISIBLE

        binding.searchLY.visibility = View.GONE
        DataFeacher(false, object :
                DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    val message: String = getString(R.string.fail_to_get_data)
                    binding.loadingLYBestProduct.visibility = View.GONE
                    binding.loadingLYBestSeller.visibility = View.GONE
                    binding.loadingLyOffer.visibility = View.GONE
                    if (func == Constants.ERROR || func == Constants.FAIL) {

                        binding.failBestProductTxt.text = message
                    } else if (func == Constants.NO_CONNECTION) {

                        binding.failBestProductTxt.setText(R.string.no_internet_connection)

                    } else {
                        if (IsSuccess) {
                            val result = obj as MainModel?
                            if (result?.status == 200) {

                                if (result.featured?.isNotEmpty() == true || result.quickProducts?.isNotEmpty() == true || result.offeredProducts?.isNotEmpty() == true
                                ) {


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

                                    initAdapter()

                                } else {
                                    if (productOffersList?.size == 0) {
                                        binding.offerLy.visibility = View.GONE
                                    }
                                    if (productSellerList?.size == 0) {
                                        binding.bestSellerLy.visibility = View.GONE
                                    }
                                    if (productBestList?.size == 0) {
                                        binding.bestProductLy.visibility = View.GONE
                                    }

                                }
                            }

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
        binding.loadingLYDelivery.visibility = visible
        binding.deliverLy.visibility = gone
        countryCode = Constants.default_amour_short_name

        val url = UtilityApp.getUrl() + countryCode + GlobalData.amour + "api/v9/Orders/nextDeliveryTime?"
        Log.d(ContentValues.TAG, "Log  store_id $storeId")
        Log.d(ContentValues.TAG, "Log url $url")
        val token = UtilityApp.getToken()
        AndroidNetworking.get(url).setTag("test").setPriority(Priority.HIGH)
            .addHeaders("ApiKey", Constants.api_key)
            .addHeaders("device_type", Constants.deviceType)
            .addHeaders("app_version", UtilityApp.getAppVersionStr())
            .addHeaders("app_version", UtilityApp.getAppVersionStr())
            .addHeaders("Authorization", Constants.TOKEN_PREFIX + accessToken)
            .addHeaders("token", token).addQueryParameter("store_id", storeId.toString()).build()
            .getAsObject(
                DeliveryResultModel::class.java,
                object : ParsedRequestListener<DeliveryResultModel?> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(result: DeliveryResultModel?) {
                        if (isVisible) {
                            binding.loadingLYDelivery.visibility = gone
                            if (result?.status == 200) {
                                if (result.data != null) {
                                    binding.deliverLy.visibility = visible
                                    val firstTime = result.data
                                    val deliveryDay = DateHandler.FormatDate4(
                                        firstTime.date,
                                        "yyyy-MM-dd",
                                        "EEE",
                                        UtilityApp.getLanguage()
                                    )
                                    val normalDelivery = if (UtilityApp.getLanguage() == Constants.Arabic) {
                                        firstTime.time + " " + deliveryDay
                                    } else {
                                        deliveryDay + " " + firstTime.time
                                    }

                                    binding.deliveryTimeTv.text = normalDelivery
                                    UtilityApp.setNormalDelivery(normalDelivery)
                                    Log.i(javaClass.name, "Log normalDelivery $normalDelivery")
                                    Log.i(
                                        javaClass.name,
                                        "Log normalDelivery UtilityApp  ${UtilityApp.getNormalDelivery()}"
                                    )

//                                if (UtilityApp.getLanguage() == Constants.Arabic) binding.deliveryTimeTv.text =
//                                    firstTime.time + " " + deliveryDay else binding.deliveryTimeTv.text =
//                                    deliveryDay + " " + firstTime.time

                                } else {
                                    binding.loadingLYDelivery.visibility = gone

                                }

                            } else {
                                binding.loadingLYDelivery.visibility = gone

                            }
                        }
                    }

                    override fun onError(anError: ANError) {
                        binding.loadingLYDelivery.visibility = gone

                        if (anError.errorCode == 400 || anError.errorCode == 443) {

                            GlobalData.Toast(activityy, anError.message)


                        } else {
                            GlobalData.Toast(activityy, anError.message)

                        }


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
                val result = obj as ResultAPIModel<*>?
                if (result != null && result.isSuccessful && result.data != null) {
                    totalPointModel = result.data as TotalPointModel?
                    DBFunction.setTotalPoints(totalPointModel)
                    setTotalPointsData()
                }
            }

        }).getTotalPoint(userId.toInt())
    }

    private fun setTotalPointsData() {
        binding.totalPointTv.text = totalPointModel?.points.toString()
    }

    private fun callCityListApi() {
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (IsSuccess) {
                        val result = obj as CityModelResult?
                        if (result?.data?.isNotEmpty() == true) {
                            cityModelArrayList = ArrayList(result.data)
                            searchSelectedCity()
                            setBranchData()
                        }
                    }
                }

            }).cityHandle(countryId, getActivity())
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
//            UtilityApp.setBranchName(selectedCityModel!!.cityName)
//            binding.branchNameTv.text = selectedCityModel!!.cityName
            binding.branchNameTv.text = UtilityApp.getBranchName()
        }
    }

    fun getCategories(storeId: Int) {


        binding.loadingLYCategories.visibility = View.VISIBLE

        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        binding.loadingLYCategories.visibility = View.GONE
                        var message: String? = getString(R.string.fail_to_get_data)
                        if (func == Constants.ERROR) {
                            val result = obj as CategoryResultModel?

                            if (result != null && result.message != null) {
                                message = result.message
                            }

                            binding.failCategoriesTxt.text = message
                        } else if (func == Constants.FAIL) {


                            binding.failCategoriesTxt.text = message
                        } else if (func == Constants.NO_CONNECTION) {

                            binding.failCategoriesTxt.setText(R.string.no_internet_connection)
                            binding.failCategoriesTxt.visibility = View.VISIBLE

                        } else {
                            if (IsSuccess) {
                                val result = obj as CategoryResultModel?

                                if (result?.data?.isNotEmpty() == true) {

                                    UtilityApp.setCategoriesData(result.data)

                                    categoryModelList = arrayListOf()
                                    categoryModelList?.addAll(result.data)
                                    initCatAdapter()
                                } else {
                                    binding.failCategoriesTxt.text = getString(R.string.no_data)
                                    binding.failCategoriesTxt.visibility = visible
                                }
                            } else {


                                binding.failCategoriesTxt.text = message
                                binding.failCategoriesTxt.visibility = visible
                            }
                        }
                    }
                }

            }).GetAllCategories(storeId)
    }

    private fun getAllBrands(storeId: Int) {
        brandsList?.clear()
        binding.loadingLYBrands.visibility = View.VISIBLE

        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        binding.loadingLYBrands.visibility = View.GONE
                        var message: String? = getString(R.string.fail_to_get_data)
                        if (func == Constants.ERROR || func == Constants.FAIL) {
                            val result =
                                obj as ResultAPIModel<ArrayList<BrandModel>?>?

                            if (result != null) {
                                message = result.message
                            }
                            binding.failBrandsTxt.visibility = visible
                            binding.failBrandsTxt.text = message
                        } else if (func == Constants.NO_CONNECTION) {
                            binding.failBrandsTxt.visibility = visible
                            binding.failBrandsTxt.text = getString(R.string.no_internet_connection)


                        } else {
                            if (IsSuccess) {
                                val result = obj as ResultAPIModel<ArrayList<BrandModel>?>?

                                if (result?.data?.isNotEmpty() == true) {
                                    Log.i(ContentValues.TAG, "Log getBrands" + result.data?.size)
                                    brandsList = result.data
                                    UtilityApp.setBrandsData(brandsList)
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
        if (bookletsList?.size ?: 0 >= 3) {
            bookletManger!!.orientation = RecyclerView.HORIZONTAL
        } else {
            bookletManger!!.orientation = RecyclerView.VERTICAL
        }
        bookletAdapter = BookletAdapter(activityy, bookletsList, 4, this)
        binding.BookletRecycler.adapter = bookletAdapter
    }

    private fun initKitchenAdapter() {
        val kitchenAdapter = KitchenAdapter(activityy, dinnerList, this, false, 10)
        binding.kitchenRecycler.adapter = kitchenAdapter
    }

    fun getBooklets(storeId: Int) {
        bookletsList?.clear()
        binding.loadingLyBooklet.visibility = View.VISIBLE
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    binding.loadingLyBooklet.visibility = View.GONE

                    if (IsSuccess) {
                        val result =
                            obj as ResultAPIModel<ArrayList<BookletsModel>?>?
                        if (result?.data?.isNotEmpty() == true) {
                            Log.i(ContentValues.TAG, "Log getBooklets" + result.data?.size)
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


            }

        }).getBookletsList(storeId)
    }

    private fun getDinners(lang: String?) {
        binding.loadingLykitchen.visibility = View.VISIBLE
        dinnerList?.clear()
        DataFeacher(false, object : DataFetcherCallBack {
            override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                if (isVisible) {
                    binding.loadingLykitchen.visibility = View.GONE

                    if (IsSuccess) {
                        val result =
                            obj as ResultAPIModel<MutableList<DinnerModel?>?>?
                        if (result?.data?.isNotEmpty() == true) {
                            Log.i(ContentValues.TAG, "Log getDinners size " + result.data?.size)
                            binding.kitchenRecycler.visibility = View.VISIBLE
                            dinnerList = result.data
                            UtilityApp.setDinnersData(dinnerList)
                            initKitchenAdapter()
                        } else {
                            binding.ramezKitchenLy.visibility = View.GONE

                        }
                    } else {
                        binding.ramezKitchenLy.visibility = View.GONE
                    }
                }

            }

        }).getDinnersList(lang ?: "")
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

//        var i = 0
        if (brandsList?.size == 0) {
            binding.brandLy.visibility = gone
        } else {
            binding.brandLy.visibility = visible
            brandsAdapter = BrandsAdapter(activityy, brandsList, this, 10)
            binding.brandsRecycler.adapter = brandsAdapter
        }

    }

    override fun onItemClicked(position: Int, categoryModel: CategoryModel) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.CAT_LIST, categoryModelList)
        bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
        bundle.putInt(Constants.MAIN_CAT_ID, categoryModel.id)
        bundle.putInt(Constants.position, position)
        EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
    }

    override fun onKitchenClicked(position: Int, dinnerModel: DinnerModel) {
        val intent = Intent(activityy, RamezKitchenActivity::class.java)
        intent.putExtra(Constants.DB_DINNER_MODEL, dinnerModel)
        startActivity(intent)
    }

    fun getProductList(productRequest: ProductRequest?) {
//        productRecentsList?.clear()
        binding.loadingLYRecent.visibility = View.VISIBLE

        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        val message: String = getString(R.string.fail_to_get_data)
                        binding.loadingLYRecent.visibility = View.GONE
                        if (func == Constants.ERROR) {
                            binding.noRecentTv.visibility = View.VISIBLE
                            binding.noRecentTv.text = message
                        } else if (func == Constants.FAIL) {
                            binding.noRecentTv.visibility = View.VISIBLE
                            binding.noRecentTv.text = message
                        } else if (func == Constants.NO_CONNECTION) {
                            binding.noRecentTv.visibility = View.VISIBLE
                            binding.noRecentTv.visibility = View.VISIBLE
                        } else {
                            if (IsSuccess) {
                                val result = obj as FavouriteResultModel?
                                if (result?.status == 200) {
                                    if (result.data.isNotEmpty()) {

                                        binding.recentlyRecycler.visibility = View.VISIBLE
                                        binding.noRecentTv.visibility = View.GONE
                                        productRecentsList = result.data
                                        Log.i(
                                            ContentValues.TAG,
                                            "Log productList new " + productRecentsList?.size
                                        )
                                        initRecentAdapter()
                                    } else {
                                        binding.recentlyRecycler.visibility = View.GONE
                                        binding.recentlyLy.visibility = View.GONE
                                        //  binding.noRecentTv.text = getString(R.string.no_data)

                                    }
                                } else {
                                    binding.recentlyRecycler.visibility = View.GONE
                                    binding.recentlyLy.visibility = View.GONE
                                    binding.noRecentTv.visibility = View.VISIBLE
                                    binding.noRecentTv.text = getString(R.string.fail_to_get_data)

                                }


                            } else {
                                binding.recentlyRecycler.visibility = View.GONE
                                binding.recentlyLy.visibility = View.GONE
                                binding.noRecentTv.visibility = View.VISIBLE
                                binding.noRecentTv.text = getString(R.string.fail_to_get_data)
                            }
                        }
                    }
                }

            }).getProductList(productRequest)
    }

    companion object {
        private const val FLASH_STATE = "FLASH_STATE"
        private const val AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE"
        private const val SELECTED_FORMATS = "SELECTED_FORMATS"
        private const val CAMERA_ID = "CAMERA_ID"
        private const val ZBAR_CAMERA_PERMISSION = 1
    }

    private fun loginAgain() {
        val intent = Intent(activity, RegisterLoginActivity::class.java)
        intent.putExtra(Constants.LOGIN, true)
        startActivity(intent)

    }

    fun getKinds() {

        kindModelList = UtilityApp.getAllKinds()
        if (kindModelList?.isNotEmpty() == true) {
            binding.loadingLYkind.visibility = View.VISIBLE
            DataFeacher(false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        binding.loadingLYkind.visibility = View.GONE
                        if (IsSuccess) {
                            val result = obj as ResultAPIModel<ArrayList<KindCategoryModel>?>
                            kindModelList = result.data
                            if (kindModelList?.isNotEmpty() == true) {
                                UtilityApp.setAllKindsData(kindModelList)
                                initKindAdapter()
                            } else {
                                val message: String = getString(R.string.no_data)
                                binding.failkindTxt.visibility = visible
                                binding.failkindTxt.text = message
                            }
                        } else {
                            binding.failkindTxt.visibility = visible
                            binding.failkindTxt.setText(R.string.fail_to_get_data)
                        }
                    }


                }
            }).getAllKindsList()

        } else {
            initKindAdapter()
        }
    }


    fun changeUrl() {
        val url = UtilityApp.getUrl()
        if (url == GlobalData.BetaBaseRamezURL1) {
            UtilityApp.setUrl(GlobalData.BetaBaseRamezURL2)
        } else {
            UtilityApp.setUrl(GlobalData.BetaBaseRamezURL1)
        }
    }

    private fun initKindAdapter() {
        kindAdapter = KindAdapter(activityy, kindModelList, 10, this, false)
        binding.kindRecycler.adapter = kindAdapter
    }

    override fun onKindClicked(position: Int, categoryModel: KindCategoryModel?) {
        val intent = Intent(activityy, AllListActivity::class.java)
        intent.putExtra(Constants.LIST_MODEL_NAME, categoryModel?.categoryName)
        intent.putExtra(Constants.kind_id, categoryModel?.id)

        startActivity(intent)
    }

    private fun getAllHomeApi() {
        getHomeSlider()
        getHomeProducts()
        getKinds()
        getBrands()
        getBooklets(cityId)
        getDinner()
        getAllCategories()
        val productRequest = ProductRequest(
            categoryId,
            countryId,
            cityId,
            Constants.new_filter,
            0,
            0,
            10,
            kindId,
            null,
            null
        )

        getProductList(productRequest)

    }

    private fun getBrands() {
        brandsList = UtilityApp.getBrandsData()
        println("Log brandsList $brandsList")
        if (brandsList?.isNotEmpty() == true) {
            initBrandsAdapter()
        } else {
            getAllBrands(cityId)
        }

    }

    private fun getDinner() {
        dinnerList = UtilityApp.getDinners()
        if (dinnerList?.isNotEmpty() == true) {
            initKitchenAdapter()
        } else {
            getDinners(lang)

        }

    }

    fun getAllCategories() {
        categoryModelList = arrayListOf()
        categoryModelList?.addAll(UtilityApp.getCategories() ?: mutableListOf())
        if (categoryModelList?.isNotEmpty() == true) {
            initCatAdapter()

        } else {
            getCategories(cityId)

        }
    }

    override fun onBrandClicked(position: Int, brandModel: BrandModel?) {
        val intent = Intent(activityy, AllListActivity::class.java)
        intent.putExtra(Constants.LIST_MODEL_NAME, requireActivity().getString(R.string.Brands))
        intent.putExtra(Constants.FILTER_NAME, Constants.brand_filter)
        intent.putExtra(Constants.brand_id, brandModel?.id)
        startActivity(intent)
    }

    override fun onSliderClicked(position: Int, slider: Slider?) {
        Log.i("tag", "Log Reffrence" + slider?.reffrence)
        Log.i("tag", "Log ReffrenceType" + (slider?.reffrenceType ?: ""))
        when (slider?.reffrenceType) {
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
                bundle.putInt(Constants.SUB_CAT_ID, subCatId?.toInt() ?: 0)
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
                bundle.putInt(Constants.SUB_CAT_ID, slider.reffrence?.toInt() ?: 0)
                bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
            }
        }
    }

    override fun onBannersClicked(position: Int, slider: Slider?) {
        when (slider?.reffrenceType) {
            1 -> {
                val intent = Intent(activityy, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.product_id, slider.reffrence)
                intent.putExtra(Constants.FROM_BROSHER, true)
                startActivity(intent)
            }
            2 -> {
                val bundle = Bundle()
                bundle.putSerializable(Constants.CAT_LIST, categoryModelList)
                bundle.putInt(Constants.SUB_CAT_ID, slider.reffrence?.toInt() ?: 0)
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
                bookletsModel.id = slider.reffrence?.toInt() ?: 0
                bookletsModel.storeID = cityId
                bundle.putSerializable(Constants.bookletsModel, bookletsModel)
                bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.specialOffersFragment)
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
            }
            6 -> {
                val bundle = Bundle()
                bundle.putSerializable(Constants.CAT_LIST, categoryModelList)
                bundle.putInt(Constants.SUB_CAT_ID, slider.reffrence?.toInt() ?: 0)
                bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
                EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
            }
        }
    }

    override fun onItemClicked(position: Int, productModel: ProductModel?) {
        val intent = Intent(activityy, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.DB_productModel, productModel)
        startActivity(intent)
    }


}