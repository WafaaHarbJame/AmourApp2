package com.amour.shop.fragments


import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.kcode.permissionslib.main.OnRequestPermissionsCallBack
import com.kcode.permissionslib.main.PermissionCompat
import com.amour.shop.ApiHandler.DataFeacher
import com.amour.shop.ApiHandler.DataFetcherCallBack
import com.amour.shop.Models.CategoryModel
import com.amour.shop.Models.CategoryResultModel
import com.amour.shop.Models.LocalModel
import com.amour.shop.R
import com.amour.shop.activities.FullScannerActivity
import com.amour.shop.adapter.CategoryNewAdapter
import com.amour.shop.classes.Constants
import com.amour.shop.classes.MessageEvent
import com.amour.shop.classes.UtilityApp
import com.amour.shop.databinding.FragmentCategoryBinding
import org.greenrobot.eventbus.EventBus


class CategoryFragment : FragmentBase(), CategoryNewAdapter.OnItemClick {
    var categoryModelList: ArrayList<CategoryModel>? = null
    var layoutManager: LinearLayoutManager? = null
    var localModel: LocalModel? = null
    lateinit var binding: FragmentCategoryBinding
    private var categoryAdapter: CategoryNewAdapter? = null
    private var activity: Activity? = null
    var cityId = 0
    private var scanLauncher: ActivityResultLauncher<Intent>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        val view: View = binding.root
        activity = getActivity()
        layoutManager = LinearLayoutManager(activityy)
        binding.catRecycler.setHasFixedSize(true)
        binding.catRecycler.layoutManager = layoutManager
        localModel = UtilityApp.getLocalData()
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        if (UtilityApp.getCategories()?.isNotEmpty() == true) {
            categoryModelList = arrayListOf()
            categoryModelList?.addAll(UtilityApp.getCategories() ?: mutableListOf())
            initAdapter()
        } else {
            getCategories(cityId)
        }
        binding.swipeDataContainer.setOnRefreshListener {
            binding.swipeDataContainer.isRefreshing = false
            getCategories(cityId)
        }
        binding.searchBut.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.searchFragment)
            EventBus.getDefault()
                .post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
        }
        binding.barcodeBut.setOnClickListener { checkCameraPermission() }
        binding.failGetDataLY.refreshBtn.setOnClickListener {
            getCategories(
                localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
            )
        }
        scanLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result: ActivityResult? ->
            if (result != null && result.data != null) {
                val bundle = result.data!!.extras
                val searchByCode =
                    bundle!!.getBoolean(Constants.SEARCH_BY_CODE_byCode, false)
                val code = bundle.getString(Constants.CODE)
                val fragmentManager = parentFragmentManager
                val searchFragment = SearchFragment()
                val data = Bundle()
                data.putString(Constants.CODE, code)
                data.putBoolean(
                    Constants.SEARCH_BY_CODE_byCode,
                    searchByCode
                )
                searchFragment.arguments = data
                fragmentManager.beginTransaction()
                    .replace(R.id.mainContainer, searchFragment, "searchFragment")
                    .commitAllowingStateLoss()
            }
        }
        return view
    }

    private fun initAdapter() {
        categoryAdapter = CategoryNewAdapter(activity, categoryModelList, 0, this, false)
        binding.catRecycler.adapter = categoryAdapter
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
                        var message: String? = requireActivity().getString(R.string.fail_to_get_data)
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
                                if (result?.data != null && result.data.size > 0) {
                                    binding.dataLY.visibility = View.VISIBLE
                                    binding.noDataLY.noDataLY.visibility = View.GONE
                                    binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                    categoryModelList = arrayListOf()
                                    categoryModelList?.addAll(result.data)
                                    Log.i(
                                        ContentValues.TAG,
                                        "Log productBestList" + categoryModelList?.size
                                    )
                                    UtilityApp.setCategoriesData(categoryModelList)
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
            }).GetAllCategories(storeId)
    }

    private fun checkCameraPermission() {
        val builder = PermissionCompat.Builder(activity)
        builder.addPermissions(
            arrayOf(
                Manifest.permission.CAMERA
            )
        )
        builder.addPermissionRationale(getString(R.string.should_allow_permission))
        builder.addRequestPermissionsCallBack(object : OnRequestPermissionsCallBack {
            override fun onGrant() {
                startScan()
            }

            override fun onDenied(permission: String) {
                Toast.makeText(
                    getActivity(),
                    "" + getString(R.string.permission_camera_rationale),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        builder.build().request()


//        Dexter.withContext(getActivity()).withPermission(Manifest.permission.CAMERA)
//            .withListener(object : PermissionListener {
//                override fun onPermissionGranted(response: PermissionGrantedResponse) {
//                    startScan()
//                }
//
//                override fun onPermissionDenied(response: PermissionDeniedResponse) {
//                    Toast.makeText(
//                        getActivity(),
//                        "" + getString(R.string.permission_camera_rationale),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    permission: PermissionRequest,
//                    token: PermissionToken
//                ) {
//                    token.continuePermissionRequest()
//                }
//            }).withErrorListener {
//                Toast.makeText(
//                    activityy,
//                    "" + getString(R.string.error_in_data),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }.onSameThread().check()
    }

    private fun startScan() {

        try {
            val builder = PermissionCompat.Builder(activity)
            builder.addPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                )
            )
            builder.addPermissionRationale(getString(R.string.should_allow_permission))
            builder.addRequestPermissionsCallBack(object : OnRequestPermissionsCallBack {
                override fun onGrant() {

                    Thread {
                        val intent = Intent(activityy, FullScannerActivity::class.java)
                        scanLauncher!!.launch(intent)
                    }.start()
                }

                override fun onDenied(permission: String) {
                    Toast(R.string.some_permission_denied)
                }
            })
            builder.build().request()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onItemClicked(position: Int, categoryModel: CategoryModel?) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.CAT_LIST, categoryModelList)
        bundle.putInt(Constants.MAIN_CAT_ID, categoryModelList!![position].id)
        bundle.putInt(Constants.position, position)
        bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
        EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
    }


}