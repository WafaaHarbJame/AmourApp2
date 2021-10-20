package com.ramez.shopp.fragments


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
import androidx.recyclerview.widget.GridLayoutManager
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
import com.ramez.shopp.Classes.CategoryModel
import com.ramez.shopp.Classes.Constants
import com.ramez.shopp.Classes.MessageEvent
import com.ramez.shopp.Classes.UtilityApp
import com.ramez.shopp.Models.CategoryResultModel
import com.ramez.shopp.Models.LocalModel
import com.ramez.shopp.R
import com.ramez.shopp.activities.FullScannerActivity
import com.ramez.shopp.adapter.CategoryAdapter
import com.ramez.shopp.databinding.FragmentCategoryBinding
import org.greenrobot.eventbus.EventBus
import java.util.*


class CategoryFragment : FragmentBase(), CategoryAdapter.OnItemClick {
    var categoryModelList: ArrayList<CategoryModel>? = null
    var gridLayoutManager: GridLayoutManager? = null
    var localModel: LocalModel? = null
    lateinit var binding: FragmentCategoryBinding
    private var categoryAdapter: CategoryAdapter? = null
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
        gridLayoutManager = GridLayoutManager(activityy, 3)
        binding.catRecycler.setHasFixedSize(true)
        binding.catRecycler.layoutManager = gridLayoutManager
        localModel =
            if (UtilityApp.getLocalData() != null) UtilityApp.getLocalData() else UtilityApp.getDefaultLocalData(
                activityy
            )
        cityId = localModel?.cityId?.toInt() ?: Constants.default_storeId.toInt()
        if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size > 0) {
            categoryModelList = UtilityApp.getCategories()
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
        categoryAdapter = CategoryAdapter(activity, categoryModelList, 0, this, false)
        binding.catRecycler.adapter = categoryAdapter
    }

    override fun onItemClicked(position: Int, categoryModel: CategoryModel) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.CAT_LIST, categoryModelList)
        bundle.putInt(Constants.MAIN_CAT_ID, categoryModelList!![position].id)
        bundle.putInt(Constants.position, position)
        bundle.putInt(Constants.KEY_FRAGMENT_ID, R.id.categoryProductsFragment)
        EventBus.getDefault().post(MessageEvent(MessageEvent.TYPE_FRAGMENT, bundle))
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
                                if (result!!.data != null && result.data.size > 0) {
                                    binding.dataLY.visibility = View.VISIBLE
                                    binding.noDataLY.noDataLY.visibility = View.GONE
                                    binding.failGetDataLY.failGetDataLY.visibility = View.GONE
                                    categoryModelList = result.data
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
        Dexter.withContext(getActivity()).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    startScan()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        getActivity(),
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
                    scanLauncher!!.launch(intent)
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



}