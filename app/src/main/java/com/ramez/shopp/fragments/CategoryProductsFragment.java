package com.ramez.shopp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.appbar.AppBarLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.activities.FullScannerActivity;
import com.ramez.shopp.activities.ProductDetailsActivity;
import com.ramez.shopp.adapter.MainCategoryAdapter;
import com.ramez.shopp.adapter.ProductCategoryAdapter;
import com.ramez.shopp.adapter.SubCategoryAdapter;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.AppBarStateChangeListener;
import com.ramez.shopp.Classes.CategoryModel;
import com.ramez.shopp.Classes.MessageEvent;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.AutoCompleteModel;
import com.ramez.shopp.Models.CategoryResultModel;
import com.ramez.shopp.Models.ChildCat;
import com.ramez.shopp.Models.FavouriteResultModel;
import com.ramez.shopp.Models.LocalModel;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ProductModel;
import com.ramez.shopp.R;
import com.ramez.shopp.databinding.FragmentCategoryProductsBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

public class CategoryProductsFragment extends FragmentBase implements ProductCategoryAdapter.OnItemClick, MainCategoryAdapter.OnMainCategoryItemClicked {
    private static final int ZBAR_CAMERA_PERMISSION = 1;
    ArrayList<ProductModel> productList;
    ArrayList<AutoCompleteModel> data = null;
    ArrayList<String> autoCompleteList;
    ArrayList<CategoryModel> mainCategoryDMS;
    ArrayList<ChildCat> subCategoryDMS;
    GridLayoutManager gridLayoutManager;
    int numColumn = 2;
    int selectedSubCat = 0;
    int category_id = 0, country_id, city_id;
    private String user_id = "0";
    private final String filter = "";
    private MemberModel user;
    private LocalModel localModel;
    private ProductCategoryAdapter adapter;
    private final int SEARCH_CODE = 2000;

    private FragmentCategoryProductsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryProductsBinding.inflate(inflater, container, false);

        productList = new ArrayList<>();
        mainCategoryDMS = new ArrayList<>();


        gridLayoutManager = new GridLayoutManager(getActivityy(), numColumn);
        binding.productsRv.setLayoutManager(gridLayoutManager);


        binding.listShopCategories.setLayoutManager(new LinearLayoutManager(getActivityy(), LinearLayoutManager.HORIZONTAL, false));

        binding.listSubCategory.setLayoutManager(new LinearLayoutManager(getActivityy(), LinearLayoutManager.HORIZONTAL, false));


        binding.productsRv.setHasFixedSize(true);
        binding.productsRv.setItemAnimator(null);

        data = new ArrayList<>();
        autoCompleteList = new ArrayList<>();

        localModel = UtilityApp.getLocalData() != null ? UtilityApp.getLocalData() : UtilityApp.getDefaultLocalData(getActivityy());
        country_id = localModel.getCountryId();

        if (UtilityApp.isLogin() && UtilityApp.getUserData() != null && UtilityApp.getUserData().getId() != null) {

            user = UtilityApp.getUserData();
            user_id = String.valueOf(user.getId());

        }
        city_id = localModel.getCityId() != null ? Integer.parseInt(localModel.getCityId()) : Integer.parseInt(UtilityApp.getDefaultLocalData(getActivityy()).getCityId());


        getIntentExtra();

        initListeners();


        return binding.getRoot();

    }

    private void initListeners() {

        binding.headerAppBarLY.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                if (state == State.COLLAPSED) {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_SHOW_SEARCH, true));
                } else {
                    EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_SHOW_SEARCH, false));
                }
            }

            @Override
            public void onBarOffsetChanged(AppBarLayout appBarLayout, int offset) {

            }
        });
        binding.failGetDataLY.refreshBtn.setOnClickListener(view1 -> {

            getProductList(selectedSubCat, country_id, city_id, user_id, filter, 0, 10);

        });


        binding.searchBut.setOnClickListener(view1 -> {
            EventBus.getDefault().post(new MessageEvent(MessageEvent.TYPE_search,true));

        });


        binding.barcodeBut.setOnClickListener(view1 -> {

            checkCameraPermission();

        });
    }

    public void initAdapter() {
        adapter = new ProductCategoryAdapter(getActivityy(), binding.productsRv, productList, selectedSubCat, country_id, city_id, user_id, 0, "", this, numColumn, 0);
        binding.productsRv.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(int position, ProductModel productModel) {
        Intent intent = new Intent(getActivityy(), ProductDetailsActivity.class);
        intent.putExtra(Constants.DB_productModel, productModel);
        startActivity(intent);


    }

    private void cancelAPiCall() {
        if (adapter != null && adapter.apiCall != null && adapter.apiCall.isExecuted()) {
            adapter.isCanceled = true;
            adapter.apiCall.cancel();
        }
    }

    private void getIntentExtra() {
        Bundle bundle = getArguments();
        Log.i(getClass().getSimpleName(), "Log getIntentExtra ");

        if (bundle != null) {

            mainCategoryDMS = (ArrayList<CategoryModel>) bundle.getSerializable(Constants.CAT_LIST);
//            boolean subCat = bundle.getBoolean(Constants.SUB_CAT_ID);
            if (mainCategoryDMS == null) {
                if (UtilityApp.getCategories() != null && UtilityApp.getCategories().size() > 0) {
                    mainCategoryDMS = UtilityApp.getCategories();
                } else {
                    getCategories(Integer.parseInt(localModel.getCityId()));
                }
            }

            category_id = bundle.getInt(Constants.MAIN_CAT_ID, 0);
            selectedSubCat = bundle.getInt(Constants.SUB_CAT_ID, 0);
            Log.i(getClass().getSimpleName(), "Log mainCategoryDMS " + mainCategoryDMS.size());
            Log.i(getClass().getSimpleName(), "Log category_id " + category_id);
            Log.i(getClass().getSimpleName(), "Log sub_cat_id " + selectedSubCat);
            if (category_id == 0 && selectedSubCat != 0) {
                getCatIdFromSub(selectedSubCat);
            } else {
                initData();
            }

        }
    }

    private void initData() {
        initMainCategoryAdapter();

        initSubCatList();

        getProductList(selectedSubCat, country_id, city_id, user_id, filter, 0, 10);

    }

    private void initSubCatList() {

        ChildCat childCat = new ChildCat();
        childCat.setId(category_id);
        childCat.setHName(getString(R.string.all));
        childCat.setName(getString(R.string.all));

        if (selectedSubCat == 0)
            selectedSubCat = category_id;
//        if (subCategoryDMS != null)
//            subCategoryDMS.clear();
//        ArrayList<ChildCat> subCategoryDMS = new ArrayList<>(subCatList);

        if (subCategoryDMS == null)
            searchSubCatList();

        subCategoryDMS.add(0, childCat);

        initSubCategoryAdapter();
    }

    private void initSubCategoryAdapter() {

        SubCategoryAdapter subCategoryAdapter = new SubCategoryAdapter(getActivityy(), subCategoryDMS, selectedSubCat, object -> {

            selectedSubCat = object.getId();
            cancelAPiCall();
            getProductList(selectedSubCat, country_id, city_id, user_id, "", 0, 10);

        });

        binding.listSubCategory.setAdapter(subCategoryAdapter);

    }

    private void initMainCategoryAdapter() {
        MainCategoryAdapter mainCategoryShopAdapter = new MainCategoryAdapter(getActivityy(), mainCategoryDMS, this, category_id);
        binding.listShopCategories.setAdapter(mainCategoryShopAdapter);
    }


    public void getProductList(int category_id, int country_id, int city_id, String user_id, String filter, int page_number, int page_size) {
//        productList.clear();
        binding.loadingProgressLY.loadingProgressLY.setVisibility(View.VISIBLE);
        binding.productsRv.setVisibility(View.GONE);
        binding.noDataLY.noDataLY.setVisibility(View.GONE);
        binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            if (isVisible()) {

                FavouriteResultModel result = (FavouriteResultModel) obj;
                String message = getString(R.string.fail_to_get_data);

                binding.loadingProgressLY.loadingProgressLY.setVisibility(View.GONE);

                if (func.equals(Constants.ERROR)) {

                    if (result != null && result.getMessage() != null) {
                        message = result.getMessage();
                    }
                    binding.productsRv.setVisibility(View.GONE);
                    binding.noDataLY.noDataLY.setVisibility(View.GONE);
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(message);

                } else if (func.equals(Constants.NO_CONNECTION)) {
                    binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                    binding.failGetDataLY.failTxt.setText(R.string.no_internet_connection);
                    binding.failGetDataLY.noInternetIv.setVisibility(View.VISIBLE);
                    binding.productsRv.setVisibility(View.GONE);

                } else {
                    if (IsSuccess) {
                        if (result.getData() != null && result.getData().size() > 0) {

                            binding.productsRv.setVisibility(View.VISIBLE);
                            binding.noDataLY.noDataLY.setVisibility(View.GONE);
                            binding.failGetDataLY.failGetDataLY.setVisibility(View.GONE);
                            productList = new ArrayList<>(result.getData());
                            initAdapter();

                        } else {

                            binding.productsRv.setVisibility(View.GONE);
                            binding.noDataLY.noDataLY.setVisibility(View.VISIBLE);

                        }


                    } else {

                        binding.productsRv.setVisibility(View.GONE);
                        binding.noDataLY.noDataLY.setVisibility(View.GONE);
                        binding.failGetDataLY.failGetDataLY.setVisibility(View.VISIBLE);
                        binding.failGetDataLY.failTxt.setText(message);


                    }
                }

            }

        }).getFavorite(category_id, country_id, city_id, user_id, filter, 0, page_number, page_size);

    }

    @Override
    public void OnMainCategoryItemClicked(CategoryModel mainCategoryDM, int position) {

        category_id = mainCategoryDM.getId();
        selectedSubCat = category_id;
        subCategoryDMS = new ArrayList<>(mainCategoryDM.getChildCat());
        initSubCatList();

        cancelAPiCall();

        getProductList(selectedSubCat, country_id, city_id, user_id, filter, 0, 10);


    }


    private void checkCameraPermission() {
        Dexter.withContext(getActivityy()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                startScan();


            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(getActivityy(), "" + getString(R.string.permission_camera_rationale), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();

            }
        }).withErrorListener(error -> Toast.makeText(getActivityy(), "" + getString(R.string.error_in_data), Toast.LENGTH_SHORT).show()).onSameThread().check();
    }


    private void startScan() {

        if (ContextCompat.checkSelfPermission(getActivityy(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivityy(), new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {

            Intent intent = new Intent(getActivityy(), FullScannerActivity.class);
            startActivityForResult(intent, SEARCH_CODE);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(@NotNull MessageEvent event) {
        if (event.type.equals(MessageEvent.TYPE_view)) {
            numColumn = (int) event.data;
            initAdapter();
            gridLayoutManager.setSpanCount(numColumn);
            adapter.notifyDataSetChanged();


        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }


    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SEARCH_CODE) {

                if (data != null) {
                    boolean SEARCH_BY_CODE_byCode = data.getBooleanExtra(Constants.SEARCH_BY_CODE_byCode, false);
                    String CODE = data.getStringExtra(Constants.CODE);
                    FragmentManager fragmentManager = getParentFragmentManager();
                    SearchFragment searchFragment = new SearchFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.CODE, CODE);
                    bundle.putBoolean(Constants.SEARCH_BY_CODE_byCode, SEARCH_BY_CODE_byCode);
                    searchFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.mainContainer, searchFragment, "searchFragment").commitAllowingStateLoss();

                }


            }

        }
    }


    public void getCategories(int storeId) {


        new DataFeacher(false, (obj, func, IsSuccess) -> {

            CategoryResultModel result = (CategoryResultModel) obj;

            if (IsSuccess) {
                if (result.getData() != null && result.getData().size() > 0) {

                    mainCategoryDMS = result.getData();

                    Log.i(TAG, "Log productBestList" + mainCategoryDMS.size());
                    UtilityApp.setCategoriesData(mainCategoryDMS);

                }


            }


        }).GetAllCategories(storeId);
    }

    private void getCatIdFromSub(int subId) {
        // get main category id form sub cat
        // this used when deep link
        for (int i = 0; i < mainCategoryDMS.size(); i++) {

            if (subId == mainCategoryDMS.get(i).getId()) {
                category_id = mainCategoryDMS.get(i).getId();
                selectedSubCat = category_id;// this for all cat
                subCategoryDMS = new ArrayList<>(mainCategoryDMS.get(i).getChildCat());
                initData();
                break;
            }
            ArrayList<ChildCat> subArrayList = mainCategoryDMS.get(i).getChildCat();

            for (int j = 0; j < subArrayList.size(); j++) {
                if (subId == subArrayList.get(j).getId()) {
                    category_id = mainCategoryDMS.get(i).getId();
                    Log.i(getClass().getSimpleName(), "Log subCat category_id " + category_id);
                    selectedSubCat = subId;
                    subCategoryDMS = new ArrayList<>(mainCategoryDMS.get(i).getChildCat());
                    initData();
                    break;
                }

            }

        }

    }

//    private void searchCatIdAndSubId(int subId) {
//        // search for main cat and sub
//        // this used when deep link
//        for (int i = 0; i < mainCategoryDMS.size(); i++) {
//
//            if (subId == mainCategoryDMS.get(i).getId())
//            ArrayList<ChildCat> subArrayList = mainCategoryDMS.get(i).getChildCat();
//
//            for (int j = 0; j < subArrayList.size(); j++) {
//                if (subId == subArrayList.get(j).getId()) {
//                    category_id = mainCategoryDMS.get(i).getId();
//                    Log.i(getClass().getSimpleName(), "Log subCat category_id " + category_id);
//                    selectedSubCat = subId;
//                    subCategoryDMS = new ArrayList<>(mainCategoryDMS.get(i).getChildCat());
//                    initData();
//
//                }
//
//            }
//
//        }
//
//    }

    private void searchSubCatList() {
        // search for sub category list by main cat id

        if (mainCategoryDMS != null)
            for (int i = 0; i < mainCategoryDMS.size(); i++) {

                if (category_id == mainCategoryDMS.get(i).getId()) {
                    subCategoryDMS = new ArrayList<>(mainCategoryDMS.get(i).getChildCat());
                    break;
                }
            }
        if (subCategoryDMS == null)
            subCategoryDMS = new ArrayList<>();

    }

}