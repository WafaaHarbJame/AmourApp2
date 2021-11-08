package com.ramez.shopp.fragments


import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramez.shopp.Models.ProductModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.*
import com.karumi.dexter.listener.single.PermissionListener
import com.ramez.shopp.ApiHandler.DataFeacher
import com.ramez.shopp.ApiHandler.DataFetcherCallBack
import com.ramez.shopp.Classes.AnalyticsHandler
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.MessageEvent
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.*
import com.ramez.shopp.R
import com.ramez.shopp.Utils.ActivityHandler
import com.ramez.shopp.activities.FullScannerActivity
import com.ramez.shopp.activities.ProductDetailsActivity
import com.ramez.shopp.adapter.MostSearchAdapter.OnTagClick
import com.ramez.shopp.adapter.OfferProductAdapter
import com.ramez.shopp.adapter.ProductAdapter
import com.ramez.shopp.adapter.SearchProductAdapter
import com.ramez.shopp.databinding.SearchFagmentBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import java.lang.Exception
import java.util.*

class SearchFragment : FragmentBase(), SearchProductAdapter.OnItemClick, OnTagClick,
        ProductAdapter.OnItemClick, OfferProductAdapter.OnItemClick {
    lateinit var binding: SearchFagmentBinding
    var productList: ArrayList<ProductModel>? = null
    var offerList: ArrayList<ProductModel>? = null
    var gridLayoutManager: GridLayoutManager? = null
    var searchByCode = false
    var numColumn = 2
    var productOffersList: ArrayList<ProductModel>? = null
    private var getOfferWithUi = true
    private var isvisible = false
    private var data: ArrayList<AutoCompleteModel>? = null
    private var autoCompleteList: ArrayList<String>? = null
    private var adapter: SearchProductAdapter? = null
    private var countryId = 0
    private var cityId = 0
    private var userId = "0"
    private val filter: String? = null
    private var result: String? = null
    private var searchText: String? = null
    private var searchQuery: String? = null
    private var user: MemberModel? = null
    private var localModel: LocalModel? = null
    private var searchCall: Call<*>? = null
    private var runnable: Runnable? = null
    private var handler: Handler? = null
    private val toggleButton = false
    private val searchCode = 2000
    private var searchLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchFagmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productList = ArrayList()
        offerList = ArrayList()
        data = ArrayList()
        autoCompleteList = ArrayList()
        productOffersList = ArrayList()
        binding.searchEt.requestFocus()

        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchEt, InputMethodManager.SHOW_IMPLICIT)

        binding.searchEt.isFocusable = true
        binding.searchEt.requestFocusFromTouch()
        binding.searchEt.threshold = 1

        val bestOfferGridLayoutManager = GridLayoutManager(activityy, 2, RecyclerView.VERTICAL, false)
        binding.offerRecycler.layoutManager = bestOfferGridLayoutManager
        binding.offerRecycler.setHasFixedSize(true)

        gridLayoutManager = GridLayoutManager(activityy, numColumn)
        binding.recycler.layoutManager = gridLayoutManager
        binding.recycler.setHasFixedSize(false)
        binding.recycler.itemAnimator = null

        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activityy
            )
        countryId = localModel?.countryId ?: Constants.default_country_id

        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()

        if (UtilityApp.isLogin() && UtilityApp.getUserData() != null && UtilityApp.getUserData().id != null) {
            user = UtilityApp.getUserData()
            userId = user?.id.toString()
        }

        intentExtra

        initListeners()

        handler = Handler(Looper.getMainLooper())
        runnable = Runnable { autoComplete(countryId, cityId, userId, searchQuery, 0, 10) }

        binding.searchEt.requestFocus()


        searchLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {
                val bundle = result.data?.extras
                val searchByCode = bundle?.getBoolean(Constants.SEARCH_BY_CODE_byCode, false)
                val code = bundle?.getString(Constants.CODE)

                searchBarcode(countryId, cityId, userId, code, 0, 10)
            }

        }

    }

    private fun initListeners() {

        binding.failGetDataLY.refreshBtn.setOnClickListener {
            val text = binding.searchEt.text.toString()
            searchTxt(countryId, cityId, userId, text, 0, 10)
        }
        binding.barcodeBut.setOnClickListener { view1 ->
            hideSoftKeyboard(activity)
            checkCameraPermission()
        }
        binding.searchEt.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val text = v.text.toString()
                searchTxt(countryId, cityId, userId, text, 0, 10)
                ActivityHandler.hideKeyboard(activity)
                binding.searchEt.dismissDropDown()
                return@setOnEditorActionListener true
            }
            false
        }

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    binding.closeBtn.setText(R.string.fal_search)
                } else {
                    binding.closeBtn.setText(R.string.fal_times)
                    searchQuery = s.toString()
                    handler!!.postDelayed(runnable!!, 500)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (searchCall != null && searchCall!!.isExecuted) searchCall!!.cancel()
                handler!!.removeCallbacks(runnable!!)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        binding.searchEt.setOnItemClickListener { adapterView, view12, position, l ->
            binding.offerLy.visibility = View.GONE
            val text = autoCompleteList!![position]
            searchTxt(countryId, cityId, userId, text, 0, 10)
        }
        binding.closeBtn.setOnClickListener {
            if (binding.closeBtn.text.equals(getString(R.string.fal_times))) {
                binding.offerLy.visibility = View.VISIBLE
                binding.noDataLY.noDataLY.visibility = View.GONE
                productList!!.clear()
                binding.searchEt.setText("")
            }
        }

    }

    fun initAdapter() {
        adapter = SearchProductAdapter(
            activityy,
            productList,
            countryId,
            cityId,
            userId,
            binding.recycler,
            binding.searchEt.text.toString(),
            this
        )
        binding.recycler.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        isvisible = true
    }

    override fun onPause() {
        isvisible = false
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        if (searchCall?.isExecuted == true) searchCall?.cancel()
    }



    private fun searchBarcode(
        country_id: Int,
        city_id: Int,
        user_id: String?,
        filter: String?,
        page_number: Int,
        page_size: Int
    ) {
        productList?.clear()

        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE

        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        binding.closeBtn.visibility = View.VISIBLE
                        binding.closeBtn.setText(R.string.fal_times)
                        val result = obj as FavouriteResultModel?
                        var message: String? = activityy.getString(R.string.fail_to_get_data)
                        binding.offerLy.visibility = View.GONE
                        binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                        if (func == Constants.ERROR) {
                            if (result != null && result.message != null) {
                                message = result.message
                            }
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
                                if (result?.data?.size ?: 0 > 0) {
                                    binding.offerLy.visibility = View.GONE
                                    binding.dataLY.visibility = View.VISIBLE
                                    binding.noDataLY.noDataLY.visibility = View.GONE
                                    binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                    productList = result?.data
                                    AnalyticsHandler.ViewSearchResult(filter)
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
                        }
                    }
                }

            }).barcodeSearch(country_id, city_id, user_id, filter, page_number, page_size)
    }

    private fun searchTxt(
        country_id: Int,
        city_id: Int,
        user_id: String?,
        filter: String?,
        page_number: Int,
        page_size: Int
    ) {
        AnalyticsHandler.searchEvent(filter)
        productList?.clear()
        offerList?.clear()
        binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
        binding.dataLY.visibility = View.GONE
        binding.noDataLY.noDataLY.visibility = View.GONE
        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        val result = obj as FavouriteResultModel?
                        var message: String? =
                            activityy.resources.getString(R.string.fail_to_get_data)
                        binding.offerLy.visibility = View.GONE
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
                                binding.offerLy.visibility = View.GONE
                                Log.i(
                                    ContentValues.TAG,
                                    "Log productList Search " + result!!.data
                                )
                                if (result.data != null && result.data.size > 0) {
                                    binding.dataLY.visibility = View.VISIBLE
                                    binding.noDataLY.noDataLY.visibility = View.GONE
                                    binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                    productList = result.data
                                    AnalyticsHandler.ViewSearchResult(filter)
                                    initAdapter()
                                } else {
                                    binding.dataLY.visibility = View.GONE
                                    binding.noDataLY.noDataLY.visibility = View.VISIBLE
                                    binding.failGetDataLY.failGetDataLY.visibility = View.GONE
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
            }).searchTxt(country_id, city_id, user_id, filter, page_number, page_size)
    }

    private val intentExtra: Unit
        get() {
            getOfferWithUi = true
            val bundle = arguments
            if (bundle != null) {
                result = bundle.getString(Constants.CODE)
                searchText = bundle.getString(Constants.inputType_text)
                searchByCode = bundle.getBoolean(Constants.SEARCH_BY_CODE_byCode, false)
                if (searchByCode) {
                    Log.i("tag", "Log searchByCode $result)")

                    searchBarcode(countryId, cityId, userId, result, 0, 10)
                } else if (searchText != null) {
                    searchTxt(countryId, cityId, userId, searchText, 0, 10)
                }
                getOfferWithUi = false
            }

            getHomePage()

        }

    private fun autoComplete(
        country_id: Int,
        city_id: Int,
        user_id: String?,
        text: String?,
        page_number: Int,
        page_size: Int
    ) {
        data!!.clear()
        val dataFeacher =
            DataFeacher(false, object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    val result = obj as AutoCompeteResult?
                    if (IsSuccess) {
                        if (result != null && result.data.size > 0) {
                            binding.dataLY.visibility = View.VISIBLE
                            binding.noDataLY.noDataLY.visibility = View.GONE
                            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                            data = result.data
                            autoNames
                        } else {
                            binding.dataLY.visibility = View.GONE
                            binding.noDataLY.noDataLY.visibility = View.VISIBLE
                        }
                    }
                }

            })
        searchCall = dataFeacher.autocomplete(country_id, city_id, user_id, text, page_number, page_size)
    }

    private val autoNames: Unit
        get() {
            autoCompleteList!!.clear()
            for (i in data!!.indices) {
                autoCompleteList!!.add(data!![i].dataName)
            }
            if (activity != null) {
                val adapter =
                    ArrayAdapter(
                        requireActivity(), android.R.layout.simple_dropdown_item_1line,
                        autoCompleteList!!
                    )
                binding.searchEt.setAdapter(adapter)
                if (isvisible) binding.searchEt.showDropDown()
            }
        }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent) {
        when (event.type) {
            MessageEvent.TYPE_view -> {
                numColumn = event.data as Int
                initAdapter()
                try {
                    gridLayoutManager!!.spanCount = numColumn
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                adapter?.notifyDataSetChanged()


            }
            MessageEvent.TYPE_SORT -> {
                productList?.sortWith(compareBy { it.firstProductBarcodes.price })
                initAdapter()

            }
            MessageEvent.TYPE_SORT_OLD -> {
                productList?.reverse()
                initAdapter()

            }
//            MessageEvent.TYPE_search -> {
//                searchByCode = true
//                result = event.data as String
//                searchBarcode(countryId, cityId, userId, result, 0, 10)
//            }
        }
    }

    private fun checkCameraPermission() {
        Dexter.withContext(activity).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                @RequiresApi(api = Build.VERSION_CODES.M)
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    startScan()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        activityy,
                        "" + activity!!.getString(R.string.permission_camera_rationale),
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

    override fun onTagClicked(position: Int, mostSearchModel: MostSearchModel) {
        searchTxt(countryId, cityId, userId, mostSearchModel.tagName, 0, 10)
    }

    fun initOffer() {
        //productOfferAdapter = new OfferProductAdapter(getActivityy(), productOffersList, this, 10);
        val productOfferAdapter = OfferProductAdapter(
            activityy, productOffersList, 0, 0, countryId, cityId, userId,
            productOffersList!!.size, binding.offerRecycler, Constants.offered_filter, this,
            { obj, func, IsSuccess -> }, 2
        )
        binding.offerRecycler.adapter = productOfferAdapter
    }

    private fun getHomePage() {
        if (getOfferWithUi) {
            binding.loadingProgressLY.loadingProgressLY.visibility = View.VISIBLE
            binding.dataLY.visibility = View.GONE
            binding.noDataLY.noDataLY.visibility = View.GONE
            binding.failGetDataLY.failGetDataLY.visibility = View.GONE
            binding.searchLY.visibility = View.GONE
        }

        DataFeacher(false,
            object : DataFetcherCallBack {
                override fun Result(obj: Any?, func: String?, IsSuccess: Boolean) {
                    if (isVisible) {
                        val result = obj as MainModel?
                        var message: String? = getString(R.string.fail_to_get_data)
                        if (getOfferWithUi)
                            binding.loadingProgressLY.loadingProgressLY.visibility = View.GONE
                        if (func == Constants.ERROR) {
                            if (result != null && result.message != null) {
                                message = result.message
                            }
                            if (getOfferWithUi) {
                                binding.dataLY.visibility = View.GONE
                                binding.noDataLY.noDataLY.visibility = View.GONE
                                binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                            }
                            binding.failGetDataLY.failTxt.text = message
                        } else if (func == Constants.FAIL) {
                            if (getOfferWithUi) {
                                binding.dataLY.visibility = View.GONE
                                binding.noDataLY.noDataLY.visibility = View.GONE
                                binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                            }
                            binding.failGetDataLY.failTxt.text = message
                        } else if (func == Constants.NO_CONNECTION) {
                            if (getOfferWithUi) {
                                binding.failGetDataLY.failGetDataLY.visibility = View.VISIBLE
                                binding.failGetDataLY.noInternetIv.visibility = View.VISIBLE
                                binding.dataLY.visibility = View.GONE
                            }
                            binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection)
                        } else {
                            if (IsSuccess) {
                                if (result?.offeredProducts?.size ?: 0 > 0) {
                                    if (getOfferWithUi) {
                                        binding.searchLY.visibility = View.VISIBLE
                                        binding.dataLY.visibility = View.VISIBLE
                                        binding.searchLY.visibility = View.VISIBLE
                                        binding.noDataLY.noDataLY.visibility = View.GONE
                                        binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                        binding.offerRecycler.visibility = View.VISIBLE
                                    }
                                    Log.i("tag", "Log Offer List" + offerList?.size)
                                    productOffersList = result?.offeredProducts
                                    initOffer()
                                } else {
                                    if (getOfferWithUi) {
                                        binding.noOffers.visibility = View.VISIBLE
                                        binding.offerRecycler.visibility = View.GONE
                                    }
                                }
                            }
                        }
                    }
                }
            }).GetMainPage(0, countryId, cityId, userId)
    }


    companion object {
        private const val ZBAR_CAMERA_PERMISSION = 1
    }

    override fun onItemClicked(position: Int, productModel: ProductModel?) {
        AnalyticsHandler.selectItem(productModel?.name)
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra(Constants.DB_productModel, productModel)
        startActivity(intent)    }
}